package ch.rmy.android.http_shortcuts.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.net.Uri
import androidx.core.graphics.createBitmap
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
        cornerRadiusDp: Float = 0f,
    ): String? = withContext(Dispatchers.IO) {
        try {
            val bitmap = context.contentResolver.openInputStream(imageUri)?.use { inputStream ->
                BitmapFactory.decodeStream(inputStream)
            } ?: return@withContext null

            try {
                val scaled = scaleBitmap(bitmap)
                try {
                    val rounded = if (cornerRadiusDp > 0f) {
                        applyCornerRadius(scaled, cornerRadiusDp)
                    } else {
                        scaled
                    }
                    try {
                        val fileName = "$BACKGROUND_FILE_PREFIX${UUID.randomUUID()}.png"
                        val file = File(context.filesDir, fileName)
                        file.outputStream().use { outputStream ->
                            rounded.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                        }
                        bitmapCache.put(fileName, rounded.copy(rounded.config, false))
                        fileName
                    } finally {
                        if (rounded !== scaled) {
                            rounded.recycle()
                        }
                    }
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

    private fun applyCornerRadius(bitmap: Bitmap, cornerRadiusDp: Float): Bitmap {
        val output = createBitmap(bitmap.width, bitmap.height)
        val canvas = Canvas(output)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val rect = RectF(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat())
        canvas.drawRoundRect(rect, cornerRadiusDp, cornerRadiusDp, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
        return output
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
