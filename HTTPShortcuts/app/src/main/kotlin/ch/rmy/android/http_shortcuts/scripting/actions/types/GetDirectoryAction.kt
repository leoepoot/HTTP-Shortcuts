package ch.rmy.android.http_shortcuts.scripting.actions.types

import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import android.webkit.MimeTypeMap
import androidx.documentfile.provider.DocumentFile
import ch.rmy.android.framework.extensions.logInfo
import ch.rmy.android.framework.extensions.takeUnlessEmpty
import ch.rmy.android.http_shortcuts.data.domains.working_directories.WorkingDirectoryRepository
import ch.rmy.android.http_shortcuts.exceptions.ActionException
import ch.rmy.android.http_shortcuts.scripting.ExecutionContext
import ch.rmy.android.http_shortcuts.utils.WorkingDirectoryUtil
import ch.rmy.android.scripting.JsObject
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.nio.charset.Charset
import java.nio.charset.IllegalCharsetNameException
import java.nio.charset.UnsupportedCharsetException
import javax.inject.Inject

class GetDirectoryAction
@Inject
constructor(
    @ApplicationContext
    private val context: Context,
    private val workingDirectoryRepository: WorkingDirectoryRepository,
    private val workingDirectoryUtil: WorkingDirectoryUtil,
) : Action<GetDirectoryAction.Params> {
    override suspend fun Params.execute(executionContext: ExecutionContext): JsObject {
        val directoryHandle = getDirectoryHandle(directoryNameOrId, executionContext)
        val contentResolver = context.contentResolver
        return executionContext.scriptingEngine.buildJsObject {
            function("readFile") { args ->
                logInfo("directory.readFile() called")
                val filePath = args.getString(0)
                    ?: executionContext.throwException(
                        ActionException {
                            "No file path parameter provided to 'readFile'"
                        },
                    )
                val encoding = args.getString(1)
                val fileUri = directoryHandle.getFileUriForReading(filePath)
                val charset = encoding?.let {
                    try {
                        Charset.forName(it)
                    } catch (_: IllegalCharsetNameException) {
                        executionContext.throwException(
                            ActionException {
                                "Invalid charset: $it"
                            },
                        )
                    } catch (_: UnsupportedCharsetException) {
                        executionContext.throwException(
                            ActionException {
                                "Unsupported charset: $it"
                            },
                        )
                    }
                } ?: Charsets.UTF_8
                contentResolver.openInputStream(fileUri)!!
                    .use {
                        it.reader(charset).readText()
                    }
            }
            function("writeFile") { args ->
                logInfo("directory.writeFile() called")
                val filePath = args.getString(0)
                    ?: executionContext.throwException(
                        ActionException {
                            "No file path parameter provided to 'writeFile'"
                        },
                    )
                val content = args.getByteArray(1) ?: return@function null
                val fileUri = directoryHandle.getFileUriForWriting(filePath)
                contentResolver.openOutputStream(fileUri, "wt")!!
                    .use { out ->
                        out.write(content)
                    }
            }
            function("appendFile") { args ->
                logInfo("directory.appendFile() called")
                val filePath = args.getString(0)
                    ?: executionContext.throwException(
                        ActionException {
                            "No file path parameter provided to 'appendFile'"
                        },
                    )
                val content = args.getByteArray(1) ?: return@function null
                val fileUri = directoryHandle.getFileUriForWriting(filePath)
                contentResolver.openOutputStream(fileUri, "wa")!!
                    .use { out ->
                        out.write(content)
                    }
            }
            function("listFiles") { args ->
                logInfo("directory.listFiles() called")
                val dirPath = args.getString(0) ?: ""
                val dirFile = if (dirPath.isEmpty()) {
                    directoryHandle.getRootDirectory()
                } else {
                    directoryHandle.findFile(dirPath)
                }
                if (dirFile == null || !dirFile.isDirectory) {
                    executionContext.throwException(
                        ActionException {
                            "Directory \"$dirPath\" not found"
                        },
                    )
                }
                dirFile.listFiles().map { file ->
                    executionContext.scriptingEngine.buildJsObject {
                        property("name", file.name)
                        property("type", file.type)
                        property("size", file.length())
                        property("mtime", file.lastModified())
                        property("isDirectory", file.isDirectory)
                        property("isFile", file.isFile)
                    }
                }
            }
            function("exists") { args ->
                logInfo("directory.exists() called")
                val filePath = args.getString(0)
                    ?: executionContext.throwException(
                        ActionException {
                            "No file path parameter provided to 'exists'"
                        },
                    )
                directoryHandle.findFile(filePath) != null
            }
            function("isDirectory") { args ->
                logInfo("directory.isDirectory() called")
                val filePath = args.getString(0)
                    ?: executionContext.throwException(
                        ActionException {
                            "No file path parameter provided to 'isDirectory'"
                        },
                    )
                directoryHandle.findFile(filePath)?.isDirectory
            }
            function("isFile") { args ->
                logInfo("directory.isFile() called")
                val filePath = args.getString(0)
                    ?: executionContext.throwException(
                        ActionException {
                            "No file path parameter provided to 'isFile'"
                        },
                    )
                directoryHandle.findFile(filePath)?.isFile
            }
            function("fileSize") { args ->
                logInfo("directory.fileSize() called")
                val filePath = args.getString(0)
                    ?: executionContext.throwException(
                        ActionException {
                            "No file path parameter provided to 'fileSize'"
                        },
                    )
                directoryHandle.findFile(filePath)?.length()
            }
            function("lastModified") { args ->
                logInfo("directory.lastModified() called")
                val filePath = args.getString(0)
                    ?: executionContext.throwException(
                        ActionException {
                            "No file path parameter provided to 'lastModified'"
                        },
                    )
                directoryHandle.findFile(filePath)?.lastModified()
            }
            function("delete") { args ->
                logInfo("directory.delete() called")
                val filePath = args.getString(0)
                    ?: executionContext.throwException(
                        ActionException {
                            "No file path parameter provided to 'delete'"
                        },
                    )
                val file = directoryHandle.findFile(filePath)
                    ?: executionContext.throwException(
                        ActionException {
                            "File \"$filePath\" not found"
                        },
                    )
                file.delete()
            }
            function("mkdir") { args ->
                logInfo("directory.mkdir() called")
                val dirPath = args.getString(0)
                    ?: executionContext.throwException(
                        ActionException {
                            "No directory path parameter provided to 'mkdir'"
                        },
                    )
                directoryHandle.findOrCreateDirectory(dirPath) != null
            }
            function("rename") { args ->
                logInfo("directory.rename() called")
                val filePath = args.getString(0)
                    ?: executionContext.throwException(
                        ActionException {
                            "No file path parameter provided to 'rename'"
                        },
                    )
                val newName = args.getString(1)
                    ?: executionContext.throwException(
                        ActionException {
                            "No new name parameter provided to 'rename'"
                        },
                    )
                val file = directoryHandle.findFile(filePath)
                    ?: executionContext.throwException(
                        ActionException {
                            "File \"$filePath\" not found"
                        },
                    )
                file.renameTo(newName)
            }
            function("copy") { args ->
                logInfo("directory.copy() called")
                val srcPath = args.getString(0)
                    ?: executionContext.throwException(
                        ActionException {
                            "No source path parameter provided to 'copy'"
                        },
                    )
                val destPath = args.getString(1)
                    ?: executionContext.throwException(
                        ActionException {
                            "No destination path parameter provided to 'copy'"
                        },
                    )
                val srcFile = directoryHandle.findFile(srcPath)
                    ?: executionContext.throwException(
                        ActionException {
                            "File \"$srcPath\" not found"
                        },
                    )
                if (srcFile.isDirectory) {
                    executionContext.throwException(
                        ActionException {
                            "Copying directories is not supported"
                        },
                    )
                }
                val destUri = directoryHandle.getFileUriForWriting(destPath)
                contentResolver.openInputStream(srcFile.uri)!!
                    .use { input ->
                        contentResolver.openOutputStream(destUri, "wt")!!
                            .use { output ->
                                input.copyTo(output)
                            }
                    }
            }
            function("move") { args ->
                logInfo("directory.move() called")
                val srcPath = args.getString(0)
                    ?: executionContext.throwException(
                        ActionException {
                            "No source path parameter provided to 'move'"
                        },
                    )
                val destPath = args.getString(1)
                    ?: executionContext.throwException(
                        ActionException {
                            "No destination path parameter provided to 'move'"
                        },
                    )
                val srcFile = directoryHandle.findFile(srcPath)
                    ?: executionContext.throwException(
                        ActionException {
                            "File \"$srcPath\" not found"
                        },
                    )
                if (srcFile.isDirectory) {
                    executionContext.throwException(
                        ActionException {
                            "Moving directories is not supported"
                        },
                    )
                }
                var moved = false
                val srcParentPath = srcPath.substringBeforeLast('/', "")
                val parentFile = if (srcParentPath.isEmpty()) {
                    directoryHandle.getRootDirectory()
                } else {
                    directoryHandle.findFile(srcParentPath)
                }
                val destParentPath = destPath.substringBeforeLast('/', "")
                val destParentFile = if (destParentPath.isEmpty()) {
                    directoryHandle.getRootDirectory()
                } else {
                    directoryHandle.findFile(destParentPath)
                }
                if (parentFile != null && destParentFile != null) {
                    try {
                        val movedUri = DocumentsContract.moveDocument(
                            contentResolver,
                            srcFile.uri,
                            parentFile.uri,
                            destParentFile.uri,
                        )
                        if (movedUri != null) {
                            val newName = destPath.substringAfterLast('/')
                            if (newName != srcFile.name) {
                                DocumentFile.fromSingleUri(context, movedUri)?.renameTo(newName)
                            }
                            moved = true
                        }
                    } catch (_: Exception) {
                        moved = false
                    }
                }
                if (!moved) {
                    val destUri = directoryHandle.getFileUriForWriting(destPath)
                    contentResolver.openInputStream(srcFile.uri)!!
                        .use { input ->
                            contentResolver.openOutputStream(destUri, "wt")!!
                                .use { output ->
                                    input.copyTo(output)
                                }
                        }
                    srcFile.delete()
                }
            }
        }
    }

    private suspend fun getDirectoryHandle(directoryNameOrId: String, executionContext: ExecutionContext): DirectoryHandle {
        if (directoryNameOrId == TEMPORARY_DIRECTORY_ID) {
            return getTemporaryDirectoryHandle(executionContext)
        }

        val workingDirectory = try {
            workingDirectoryRepository.getWorkingDirectoryByNameOrId(directoryNameOrId)
        } catch (_: NoSuchElementException) {
            throw ActionException {
                "Directory \"${directoryNameOrId}\" not found"
            }
        }
        workingDirectoryRepository.touchWorkingDirectory(workingDirectory.id)
        val directory = workingDirectoryUtil.getDocumentFile(workingDirectory)
        if (directory == null || !directory.isDirectory) {
            throw ActionException {
                "Directory \"${workingDirectory.name}\" is not mounted"
            }
        }

        return object : DirectoryHandle {
            override fun getFileUriForReading(filePath: String): Uri =
                directory.findFileFromPath(filePath)
                    ?.uri
                    ?: executionContext.throwException(
                        ActionException {
                            "File \"$filePath\" not found in directory \"${workingDirectory.name}\""
                        },
                    )

            override fun getFileUriForWriting(filePath: String): Uri =
                directory.findOrCreateFileFromPath(filePath)
                    ?.uri
                    ?: executionContext.throwException(
                        ActionException {
                            "File \"$filePath\" not found in directory \"${workingDirectory.name}\""
                        },
                    )

            override fun findFile(filePath: String): DocumentFile? =
                directory.findFileFromPath(filePath)

            override fun findOrCreateDirectory(filePath: String): DocumentFile? =
                directory.findOrCreateDirectoryFromPath(filePath)

            override fun getRootDirectory(): DocumentFile? = directory

            override fun isReadOnly(): Boolean = false
        }
    }

    private fun getTemporaryDirectoryHandle(executionContext: ExecutionContext): DirectoryHandle =
        object : DirectoryHandle {
            override fun getFileUriForReading(filePath: String): Uri =
                executionContext.fileUploadResult?.getFiles()
                    ?.find { it.id == filePath || it.fileName == filePath }
                    ?.data
                    ?: executionContext.throwException(
                        ActionException {
                            "No file with name or id \"$filePath\" found"
                        },
                    )

            override fun getFileUriForWriting(filePath: String): Uri =
                executionContext.throwException(
                    ActionException {
                        "Selected files are readonly"
                    },
                )

            override fun findFile(filePath: String): DocumentFile? =
                executionContext.fileUploadResult?.getFiles()
                    ?.find { it.id == filePath || it.fileName == filePath }
                    ?.let { DocumentFile.fromSingleUri(context, it.data) }

            override fun findOrCreateDirectory(filePath: String): DocumentFile? =
                executionContext.throwException(
                    ActionException {
                        "Selected files are readonly"
                    },
                )

            override fun getRootDirectory(): DocumentFile? = null

            override fun isReadOnly(): Boolean = true
        }

    private fun DocumentFile.findFileFromPath(filePath: String): DocumentFile? {
        var fileHandle: DocumentFile = this
        filePath.split('/').forEach { fileName ->
            fileHandle = fileHandle.findFile(fileName)
                ?: return null
        }
        return fileHandle
    }

    private fun DocumentFile.findOrCreateFileFromPath(filePath: String): DocumentFile? {
        var fileHandle: DocumentFile = this
        val parts = filePath.split('/')
        parts.forEachIndexed { index, fileName ->
            if (fileName == "." || fileName == "..") {
                return null
            }
            fileHandle = fileHandle.findFile(fileName)
                ?: (
                    if (index != parts.lastIndex) {
                        fileHandle.createDirectory(fileName)
                    } else {
                        fileHandle.createFile(
                            determineMimeType(fileName),
                            fileName,
                        )
                    }
                    )
                ?: return null
        }
        return fileHandle
    }

    private fun DocumentFile.findOrCreateDirectoryFromPath(filePath: String): DocumentFile? {
        var fileHandle: DocumentFile = this
        filePath.split('/').forEach { fileName ->
            if (fileName == "." || fileName == "..") {
                return null
            }
            fileHandle = fileHandle.findFile(fileName)
                ?: fileHandle.createDirectory(fileName)
                ?: return null
        }
        return fileHandle
    }

    private fun determineMimeType(fileName: String): String =
        File(fileName).extension.takeUnlessEmpty()?.let { extension ->
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        }
            ?: "text/plain"

    interface DirectoryHandle {
        fun getFileUriForReading(filePath: String): Uri

        fun getFileUriForWriting(filePath: String): Uri

        fun findFile(filePath: String): DocumentFile?

        fun findOrCreateDirectory(filePath: String): DocumentFile?

        fun getRootDirectory(): DocumentFile?

        fun isReadOnly(): Boolean
    }

    data class Params(
        val directoryNameOrId: String,
    )

    companion object {
        private const val TEMPORARY_DIRECTORY_ID = ""
    }
}