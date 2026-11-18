package ch.rmy.android.http_shortcuts.widget

import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.ImageView
import android.widget.RemoteViews
import androidx.annotation.ColorInt
import androidx.annotation.IdRes
import ch.rmy.android.http_shortcuts.R
import ch.rmy.android.http_shortcuts.data.enums.WidgetBackgroundType

object WidgetBackgroundRenderer {

    fun applyBackground(
        context: Context,
        views: RemoteViews,
        @IdRes containerId: Int,
        @IdRes backgroundImageViewId: Int,
        background: WidgetBackgroundType?,
    ) {
        when (background) {
            null,
            is WidgetBackgroundType.None,
            -> {
                views.setViewVisibility(backgroundImageViewId, View.GONE)
                views.setInt(containerId, "setBackgroundResource", 0)
            }

            is WidgetBackgroundType.Color -> {
                views.setViewVisibility(backgroundImageViewId, View.GONE)
                applyColorBackground(views, containerId, background.color)
            }

            is WidgetBackgroundType.Image -> {
                applyImageBackground(context, views, containerId, backgroundImageViewId, background.fileName)
            }
        }
    }

    private fun applyColorBackground(
        views: RemoteViews,
        @IdRes containerId: Int,
        @ColorInt color: Int,
    ) {
        views.setInt(containerId, "setBackgroundResource", R.drawable.bg_widget_radius_28)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            views.setColorIntSafely(
                clazz = android.widget.FrameLayout::class.java,
                id = containerId,
                methodName = "setBackgroundColor",
                value = color,
            )
        } else {
            views.setInt(containerId, "setBackgroundColor", color)
        }
    }

    private fun applyImageBackground(
        context: Context,
        views: RemoteViews,
        @IdRes containerId: Int,
        @IdRes backgroundImageViewId: Int,
        fileName: String,
    ) {
        val bitmap = WidgetImageUtil.getBackgroundBitmap(context, fileName)
        if (bitmap != null) {
            views.setViewVisibility(backgroundImageViewId, View.VISIBLE)
            views.setImageViewBitmap(backgroundImageViewId, bitmap)
            views.setInt(containerId, "setBackgroundResource", 0)
        } else {
            views.setViewVisibility(backgroundImageViewId, View.GONE)
            applyColorBackground(views, containerId, Color.DKGRAY)
        }
    }
}
