package ch.rmy.android.http_shortcuts.widget

import android.content.Context
import android.content.res.ColorStateList
import android.os.Build
import android.view.View
import android.widget.RemoteViews
import androidx.annotation.ColorInt
import androidx.annotation.IdRes
import ch.rmy.android.http_shortcuts.R
import ch.rmy.android.http_shortcuts.data.enums.WidgetBackgroundType
import kotlin.math.abs

object WidgetBackgroundRenderer {

    private val CORNER_RADIUS_DRAWABLES = listOf(
        8 to R.drawable.bg_widget_rounded_8,
        12 to R.drawable.bg_widget_rounded_12,
        16 to R.drawable.bg_widget_rounded_16,
        20 to R.drawable.bg_widget_rounded_20,
    )

    private const val FALLBACK_CORNER_RADIUS_DP = 16f

    fun applyBackground(
        context: Context,
        views: RemoteViews,
        @IdRes backgroundImageViewId: Int,
        background: WidgetBackgroundType?,
    ) {
        when (background) {
            null,
            is WidgetBackgroundType.None,
            -> applyNoBackground(views, backgroundImageViewId)

            is WidgetBackgroundType.Color ->
                applyColorBackground(context, views, backgroundImageViewId, background.color)

            is WidgetBackgroundType.Image ->
                applyImageBackground(context, views, backgroundImageViewId, background.fileName)
        }
    }

    private fun applyNoBackground(views: RemoteViews, @IdRes id: Int) {
        views.setViewVisibility(id, View.GONE)
        views.setInt(id, "setBackgroundResource", 0)
        views.setInt(id, "setImageResource", 0)
        clearBackgroundTint(views, id)
    }

    private fun applyColorBackground(context: Context, views: RemoteViews, @IdRes id: Int, @ColorInt color: Int) {
        views.setViewVisibility(id, View.VISIBLE)
        views.setInt(id, "setImageResource", 0)
        views.setInt(id, "setBackgroundResource", getRoundedBackgroundResId(context))
        val tintApplied = views.setColorStateListSafely(
            View::class.java,
            id,
            "setBackgroundTintList",
            ColorStateList.valueOf(color),
        )
        if (!tintApplied) {
            views.setInt(id, "setBackgroundColor", color)
        }
    }

    private fun applyImageBackground(context: Context, views: RemoteViews, @IdRes id: Int, fileName: String) {
        clearBackgroundTint(views, id)
        val bitmap = WidgetImageUtil.getBackgroundBitmap(context, fileName)
        if (bitmap == null) {
            views.setViewVisibility(id, View.GONE)
            return
        }
        views.setViewVisibility(id, View.VISIBLE)
        views.setInt(id, "setBackgroundResource", getRoundedBackgroundResId(context))
        views.setBooleanSafely(View::class.java, id, "setClipToOutline", true)
        views.setImageViewBitmap(id, bitmap)
    }

    private fun clearBackgroundTint(views: RemoteViews, @IdRes id: Int) {
        views.setColorStateList(id, "setBackgroundTintList", null)
    }

    private fun getRoundedBackgroundResId(context: Context): Int {
        val density = context.resources.displayMetrics.density
        val systemRadiusDp = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val px = context.resources.getDimensionPixelSize(
                android.R.dimen.system_app_widget_inner_radius,
            )
            px / density
        } else {
            FALLBACK_CORNER_RADIUS_DP
        }
        return CORNER_RADIUS_DRAWABLES.minBy { abs(it.first - systemRadiusDp) }.second
    }
}
