package ch.rmy.android.http_shortcuts.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.graphics.scale
import ch.rmy.android.framework.extensions.logException
import ch.rmy.android.framework.extensions.tryOrLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

object WidgetImageUtil {

    private const val BACKGROUND_FILE_PREFIX = "widget_bg_"

    private val bitmapCache = android.util.LruCache<String, Bitmap>(
        (WidgetConstants.WIDGET_BACKGROUND_CACHE_MAX_SIZE_BYTES / 1024).toInt(),
    )

    suspend fun saveImageFromUri(
        context: Context,
        imageUri: Uri,
    ): String? = withContext(Dispatchers.IO) {
        try {
            val bitmap = context.contentResolver.openInputStream(imageUri)?.use { inputStream ->
                BitmapFactory.decodeStream(inputStream)
            } ?: return@withContext null

            try {
                val scaled = scaleBitmap(bitmap)
                try {
                    val fileName = "$BACKGROUND_FILE_PREFIX${UUID.randomUUID()}.png"
                    val file = File(context.filesDir, fileName)
                    file.outputStream().use { outputStream ->
                        scaled.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                    }
                    bitmapCache.put(fileName, scaled.copy(scaled.config ?: Bitmap.Config.ARGB_8888, false))
                    fileName
                } finally {
                    if (scaled !== bitmap) {
                        scaled.recycle()
                    }
                }
            } finally {
                bitmap.recycle()
            }
        } catch (e: Exception) {
            logException(e)
            null
        }
    }

    private fun scaleBitmap(bitmap: Bitmap): Bitmap {
        val maxSize = WidgetConstants.WIDGET_BACKGROUND_MAX_SIZE_PX
        val width = bitmap.width
        val height = bitmap.height
        if (width <= maxSize && height <= maxSize) {
            return bitmap
        }
        val scale = minOf(maxSize.toFloat() / width, maxSize.toFloat() / height)
        val newWidth = (width * scale).toInt()
        val newHeight = (height * scale).toInt()
        return bitmap.scale(newWidth, newHeight, filter = true)
    }

    fun getBackgroundBitmap(context: Context, fileName: String): Bitmap? {
        bitmapCache.get(fileName)?.let { return it }
        return tryOrLog {
            val file = File(context.filesDir, fileName)
            if (!file.exists()) return@tryOrLog null
            val options = BitmapFactory.Options().apply {
                inPreferredConfig = Bitmap.Config.ARGB_8888
            }
            BitmapFactory.decodeFile(file.absolutePath, options)
        }?.also {
            bitmapCache.put(fileName, it)
        }
    }

    suspend fun deleteImage(context: Context, fileName: String?) {
        if (fileName == null) return
        withContext(Dispatchers.IO) {
            bitmapCache.remove(fileName)
            File(context.filesDir, fileName).takeIf { it.exists() }?.delete()
        }
    }

    fun isBackgroundFile(fileName: String): Boolean =
        fileName.startsWith(BACKGROUND_FILE_PREFIX) && fileName.endsWith(".png")
}
