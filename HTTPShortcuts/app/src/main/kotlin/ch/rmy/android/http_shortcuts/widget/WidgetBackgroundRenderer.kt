package ch.rmy.android.http_shortcuts.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import android.view.View
import android.widget.RemoteViews
import androidx.annotation.ColorInt
import androidx.annotation.IdRes
import ch.rmy.android.http_shortcuts.data.enums.WidgetBackgroundType

object WidgetBackgroundRenderer {

    private const val DEFAULT_WIDGET_WIDTH_DP = 110
    private const val DEFAULT_WIDGET_HEIGHT_DP = 110

    fun applyBackground(
        context: Context,
        views: RemoteViews,
        widgetId: Int,
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
                applyRoundedBitmapBackground(context, views, widgetId, containerId, backgroundImageViewId, background.color)
            }

            is WidgetBackgroundType.Image -> {
                applyImageBackground(context, views, widgetId, containerId, backgroundImageViewId, background.fileName)
            }
        }
    }

    private fun applyRoundedBitmapBackground(
        context: Context,
        views: RemoteViews,
        widgetId: Int,
        @IdRes containerId: Int,
        @IdRes backgroundImageViewId: Int,
        @ColorInt color: Int,
    ) {
        val (widthPx, heightPx) = getWidgetSizePx(context, widgetId)
        val radiusPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            WidgetConstants.CORNER_RADIUS_DP,
            context.resources.displayMetrics,
        )
        val bitmap = WidgetImageUtil.getRoundedColorBitmap(context, color, widthPx, heightPx, radiusPx)
        if (bitmap != null) {
            views.setViewVisibility(backgroundImageViewId, View.VISIBLE)
            views.setImageViewBitmap(backgroundImageViewId, bitmap)
            views.setInt(containerId, "setBackgroundResource", 0)
        } else {
            views.setViewVisibility(backgroundImageViewId, View.GONE)
            views.setInt(containerId, "setBackgroundColor", color)
        }
    }

    private fun applyImageBackground(
        context: Context,
        views: RemoteViews,
        widgetId: Int,
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
            applyRoundedBitmapBackground(context, views, widgetId, containerId, backgroundImageViewId, Color.DKGRAY)
        }
    }

    private fun getWidgetSizePx(context: Context, widgetId: Int): Pair<Int, Int> {
        val metrics = context.resources.displayMetrics
        val options = try {
            AppWidgetManager.getInstance(context).getAppWidgetOptions(widgetId)
        } catch (_: Exception) {
            null
        }
        val widthDp = options?.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, 0)?.takeIf { it > 0 }
            ?: DEFAULT_WIDGET_WIDTH_DP
        val heightDp = options?.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, 0)?.takeIf { it > 0 }
            ?: DEFAULT_WIDGET_HEIGHT_DP
        val widthPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, widthDp.toFloat(), metrics).toInt().coerceAtLeast(1)
        val heightPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, heightDp.toFloat(), metrics).toInt().coerceAtLeast(1)
        return widthPx to heightPx
    }
}