package ch.rmy.android.framework.utils

import android.content.ContentResolver
import android.content.Context
import android.content.res.AssetFileDescriptor
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import androidx.core.content.FileProvider
import ch.rmy.android.framework.extensions.applyIf
import ch.rmy.android.framework.extensions.minus
import ch.rmy.android.framework.extensions.tryOrLog
import java.io.BufferedWriter
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.OutputStreamWriter
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Duration

object FileUtil {

    private val cacheFileNames: MutableMap<Uri, String> = ConcurrentHashMap()
    private val cacheFileTypes: MutableMap<Uri, String> = ConcurrentHashMap()

    fun createCacheFile(context: Context, file: String, deleteIfExists: Boolean = false): Uri =
        getUriFromFile(
            context,
            File(context.cacheDir, file)
                .applyIf(deleteIfExists) {
                    delete()
                },
        )

    fun deleteCacheFile(context: Context, file: String) {
        File(context.cacheDir, file).delete()
    }

    fun getCacheFileIfValid(context: Context, file: String): Uri? =
        File(context.cacheDir, file)
            .takeIf { it.isFile && it.length() > 0 }
            ?.let {
                getUriFromFile(context, it)
            }

    fun getOutputStream(context: Context, uri: Uri) =
        context.contentResolver.openOutputStream(uri, "wt")
            ?: throw IOException("Failed to open URI $uri")

    fun getWriter(context: Context, uri: Uri) =
        BufferedWriter(OutputStreamWriter(getOutputStream(context, uri)))

    fun getUriFromFile(context: Context, file: File): Uri =
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file,
        )

    fun deleteOldCacheFiles(context: Context, maxCacheFileAge: Duration) {
        val now = Instant.now()
        context.cacheDir
            .listFiles()
            ?.filter { now - Instant.ofEpochMilli(it.lastModified()) > maxCacheFileAge }
            ?.filter { !it.name.endsWith(".lck") }
            ?.forEach {
                it.delete()
            }
    }

    fun getFileName(contentResolver: ContentResolver, fileUri: Uri): String? {
        if (fileUri.scheme == "content") {
            tryOrLog {
                contentResolver.query(fileUri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
                    ?.use { cursor ->
                        cursor.moveToFirst()
                        val fileName = cursor
                            .takeUnless { it.isAfterLast }
                            ?.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                            .takeUnless { it == -1 }
                            ?.let(cursor::getString)
                        if (fileName != null) {
                            return fileName
                        }
                    }
            }
        }
        return null
    }

    fun getFileSize(contentResolver: ContentResolver, fileUri: Uri): Long? =
        try {
            contentResolver.openAssetFileDescriptor(fileUri, "r")
                ?.use {
                    it.length
                }
                ?.takeUnless { it == AssetFileDescriptor.UNKNOWN_LENGTH }
        } catch (_: FileNotFoundException) {
            null
        }

    fun getFileLastModified(contentResolver: ContentResolver, fileUri: Uri): Long? {
        if (fileUri.scheme == ContentResolver.SCHEME_FILE) {
            val path = fileUri.path ?: return null
            return try {
                java.io.File(path).lastModified().takeIf { it > 0 }
            } catch (_: Exception) {
                null
            }
        }

        if (fileUri.scheme == ContentResolver.SCHEME_CONTENT) {
            tryOrLog {
                contentResolver.query(
                    fileUri,
                    arrayOf(
                        DocumentsContract.Document.COLUMN_LAST_MODIFIED,
                        MediaStore.MediaColumns.DATE_MODIFIED,
                    ),
                    null, null, null,
                )?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        val lastModifiedCol = cursor.getColumnIndex(DocumentsContract.Document.COLUMN_LAST_MODIFIED)
                        if (lastModifiedCol != -1 && !cursor.isNull(lastModifiedCol)) {
                            val value = cursor.getLong(lastModifiedCol)
                            if (value > 0) {
                                return value
                            }
                        }
                        val dateModifiedCol = cursor.getColumnIndex(MediaStore.MediaColumns.DATE_MODIFIED)
                        if (dateModifiedCol != -1 && !cursor.isNull(dateModifiedCol)) {
                            val value = cursor.getLong(dateModifiedCol)
                            if (value > 0) {
                                return value * 1000
                            }
                        }
                    }
                }
            }
        }
        return null
    }

    fun getCacheFileOriginalName(cacheFileUri: Uri): String? =
        cacheFileNames[cacheFileUri]

    fun putCacheFileOriginalName(cacheFileUri: Uri, name: String) {
        cacheFileNames[cacheFileUri] = name
    }

    fun getCacheFileOriginalType(cacheFileUri: Uri): String? =
        cacheFileTypes[cacheFileUri]

    fun putCacheFileOriginalType(cacheFileUri: Uri, name: String) {
        cacheFileTypes[cacheFileUri] = name
    }
}