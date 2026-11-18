package ch.rmy.android.http_shortcuts.widget

import android.content.Context
import android.content.res.ColorStateList
import android.view.View
import android.widget.RemoteViews
import androidx.annotation.ColorInt
import androidx.annotation.IdRes
import ch.rmy.android.http_shortcuts.R
import ch.rmy.android.http_shortcuts.data.enums.WidgetBackgroundType

object WidgetBackgroundRenderer {

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
                applyColorBackground(views, backgroundImageViewId, background.color)

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

    private fun applyColorBackground(views: RemoteViews, @IdRes id: Int, @ColorInt color: Int) {
        views.setViewVisibility(id, View.VISIBLE)
        views.setInt(id, "setImageResource", 0)
        views.setInt(id, "setBackgroundResource", R.drawable.bg_widget_rounded)
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
        views.setInt(id, "setBackgroundResource", R.drawable.bg_widget_rounded)
        views.setBooleanSafely(View::class.java, id, "setClipToOutline", true)
        views.setImageViewBitmap(id, bitmap)
    }

    private fun clearBackgroundTint(views: RemoteViews, @IdRes id: Int) {
        views.setColorStateList(id, "setBackgroundTintList", null)
    }
}
