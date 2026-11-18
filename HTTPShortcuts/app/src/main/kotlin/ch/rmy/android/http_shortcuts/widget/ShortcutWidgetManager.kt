package ch.rmy.android.http_shortcuts.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.TypedValue
import android.util.TypedValue.COMPLEX_UNIT_SP
import android.view.View
import android.widget.RemoteViews
import ch.rmy.android.http_shortcuts.R
import ch.rmy.android.http_shortcuts.activities.ExecuteActivity
import ch.rmy.android.http_shortcuts.data.domains.shortcuts.ShortcutId
import ch.rmy.android.http_shortcuts.data.domains.shortcuts.ShortcutRepository
import ch.rmy.android.http_shortcuts.data.domains.widgets.ShortcutWidgetsRepository
import ch.rmy.android.http_shortcuts.data.enums.PaddingType
import ch.rmy.android.http_shortcuts.data.enums.ShortcutTriggerType
import ch.rmy.android.http_shortcuts.data.enums.WidgetTextSource
import ch.rmy.android.http_shortcuts.data.models.Shortcut
import ch.rmy.android.http_shortcuts.data.models.ShortcutWidget
import ch.rmy.android.http_shortcuts.utils.IconUtil
import javax.inject.Inject

class ShortcutWidgetManager
@Inject
constructor(
    private val context: Context,
    private val shortcutWidgetsRepository: ShortcutWidgetsRepository,
    private val shortcutRepository: ShortcutRepository,
) {
    suspend fun updateAllWidgets() {
        updateWidgets(shortcutWidgetsRepository.getShortcutWidgets())
    }

    suspend fun updateWidgets(widgetIds: List<Int>) {
        updateWidgets(shortcutWidgetsRepository.getShortcutWidgetsByIds(widgetIds))
    }

    suspend fun updateWidgets(shortcutId: ShortcutId) {
        updateWidgets(shortcutWidgetsRepository.getShortcutWidgetsByShortcutId(shortcutId))
    }

    @JvmName(name = "_updateWidgets")
    private suspend fun updateWidgets(shortcutWidgets: List<ShortcutWidget>) {
        shortcutWidgets.forEach { widget ->
            try {
                updateWidget(
                    widget,
                    shortcut = shortcutRepository.getShortcutById(widget.shortcutId),
                )
            } catch (_: NoSuchElementException) {
                // skip the widget
            }
        }
    }

    private fun updateWidget(shortcutWidget: ShortcutWidget, shortcut: Shortcut) {
        RemoteViews(context.packageName, R.layout.shortcut_widget).also { views ->
            views.setOnClickPendingIntent(
                R.id.widget_base,
                ExecuteActivity.IntentBuilder(shortcut.id)
                    .trigger(ShortcutTriggerType.WIDGET)
                    .build(context)
                    .let { intent ->
                        PendingIntent.getActivity(
                            context,
                            shortcutWidget.widgetId,
                            intent,
                            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT,
                        )
                    },
            )

            WidgetBackgroundRenderer.applyBackground(
                context = context,
                views = views,
                containerId = R.id.widget_container,
                backgroundImageViewId = R.id.widget_background,
                background = shortcutWidget.background,
            )

            val paddingDp = (shortcutWidget.padding ?: PaddingType.DEFAULT).paddingDp
            val paddingPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, paddingDp.toFloat(), context.resources.displayMetrics).toInt()
            views.setViewPadding(R.id.widget_container, paddingPx, paddingPx, paddingPx, paddingPx)

            if (shortcutWidget.showIcon) {
                views.setViewVisibility(R.id.widget_icon, View.VISIBLE)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val color = shortcut.icon.tint
                    if (color != null) {
                        views.setColorInt(R.id.widget_icon, "setColorFilter", color, color)
                    } else {
                        views.setInt(R.id.widget_icon, "setColorFilter", 0)
                    }
                }
                views.setImageViewIcon(R.id.widget_icon, IconUtil.getIcon(context, shortcut.icon, adaptive = false))
                views.setImageViewScaleX(R.id.widget_icon, shortcutWidget.iconScale)
                views.setImageViewScaleY(R.id.widget_icon, shortcutWidget.iconScale)
            } else {
                views.setViewVisibility(R.id.widget_icon, View.GONE)
            }

            val titleText = when (shortcutWidget.titleSource ?: WidgetTextSource.DEFAULT_TITLE) {
                WidgetTextSource.SHORTCUT_NAME -> shortcut.name
                WidgetTextSource.CUSTOM -> shortcutWidget.titleText.orEmpty()
                else -> shortcut.name
            }

            if (shortcutWidget.showLabel && titleText.isNotEmpty()) {
                views.setViewVisibility(R.id.widget_title, View.VISIBLE)
                views.setTextViewText(R.id.widget_title, titleText)
                val titleSize = shortcutWidget.titleSize ?: WidgetConstants.DEFAULT_TITLE_SIZE
                views.setTextViewTextSize(R.id.widget_title, COMPLEX_UNIT_SP, titleSize)
                val titleColor = shortcutWidget.titleColor?.let { parseColorSafe(it) }
                    ?: shortcutWidget.labelColor?.let { parseColorSafe(it) }
                    ?: WidgetConstants.DEFAULT_WIDGET_TEXT_COLOR
                views.setTextColor(R.id.widget_title, titleColor)
            } else {
                views.setViewVisibility(R.id.widget_title, View.GONE)
            }

            val subtitleText = when (shortcutWidget.subtitleSource ?: WidgetTextSource.DEFAULT_SUBTITLE) {
                WidgetTextSource.SHORTCUT_DESCRIPTION -> shortcut.description
                WidgetTextSource.CUSTOM -> shortcutWidget.subtitleText.orEmpty()
                else -> shortcut.description
            }

            if (subtitleText.isNotEmpty()) {
                views.setViewVisibility(R.id.widget_subtitle, View.VISIBLE)
                views.setTextViewText(R.id.widget_subtitle, subtitleText)
                val subtitleSize = (shortcutWidget.titleSize ?: WidgetConstants.DEFAULT_TITLE_SIZE) - WidgetConstants.SUBTITLE_SIZE_OFFSET
                views.setTextViewTextSize(R.id.widget_subtitle, COMPLEX_UNIT_SP, maxOf(subtitleSize, 8f))
                views.setTextColor(R.id.widget_subtitle, WidgetConstants.DEFAULT_SUBTITLE_COLOR)
            } else {
                views.setViewVisibility(R.id.widget_subtitle, View.GONE)
            }

            AppWidgetManager.getInstance(context)
                .updateAppWidget(shortcutWidget.widgetId, views)
        }
    }

    private fun parseColorSafe(colorString: String): Int? = try {
        Color.parseColor(colorString)
    } catch (_: Exception) {
        null
    }

    suspend fun deleteWidgets(widgetIds: List<Int>) {
        shortcutWidgetsRepository.deleteDeadShortcutWidgets()
        shortcutWidgetsRepository.deleteShortcutWidgets(widgetIds)
    }
}
