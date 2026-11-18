package ch.rmy.android.http_shortcuts.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.TypedValue
import android.util.TypedValue.COMPLEX_UNIT_SP
import android.view.View
import android.widget.RemoteViews
import ch.rmy.android.http_shortcuts.R
import ch.rmy.android.http_shortcuts.activities.ExecuteActivity
import ch.rmy.android.http_shortcuts.data.domains.shortcuts.ShortcutId
import ch.rmy.android.http_shortcuts.data.domains.variables.GlobalVariableId
import ch.rmy.android.http_shortcuts.data.domains.variables.GlobalVariableRepository
import ch.rmy.android.http_shortcuts.data.domains.variables.VariableKeyOrId
import ch.rmy.android.http_shortcuts.data.domains.widgets.VariableWidgetsRepository
import ch.rmy.android.http_shortcuts.data.enums.PaddingType
import ch.rmy.android.http_shortcuts.data.enums.ShortcutTriggerType
import ch.rmy.android.http_shortcuts.data.enums.SubtitlePosition
import ch.rmy.android.http_shortcuts.data.enums.WidgetBackgroundType
import ch.rmy.android.http_shortcuts.data.models.GlobalVariable
import ch.rmy.android.http_shortcuts.data.models.VariableWidget
import javax.inject.Inject

class VariableWidgetManager
@Inject
constructor(
    private val context: Context,
    private val variableWidgetsRepository: VariableWidgetsRepository,
    private val globalVariableRepository: GlobalVariableRepository,
) {
    suspend fun createOrUpdateWidget(
        widgetId: Int,
        globalVariableId: GlobalVariableId,
        fontSize: Int,
        title: String,
        background: WidgetBackgroundType?,
        shortcutId: ShortcutId?,
        titleSize: Float? = null,
        titleColor: String? = null,
        valueSize: Float? = null,
        valueColor: String? = null,
        unit: String? = null,
        subtitleText: String? = null,
        subtitlePosition: SubtitlePosition? = null,
        padding: PaddingType? = null,
    ) {
        variableWidgetsRepository.createOrUpdateVariableWidget(
            widgetId = widgetId,
            variableId = globalVariableId,
            fontSize = fontSize,
            title = title,
            background = background,
            shortcutId = shortcutId,
            titleSize = titleSize,
            titleColor = titleColor,
            valueSize = valueSize,
            valueColor = valueColor,
            unit = unit,
            subtitleText = subtitleText,
            subtitlePosition = subtitlePosition,
            padding = padding,
        )
    }

    suspend fun updateAllWidgets() {
        updateWidgets(variableWidgetsRepository.getVariableWidgets())
    }

    suspend fun updateWidgets(widgetIds: List<Int>) {
        updateWidgets(variableWidgetsRepository.getVariableWidgetsByIds(widgetIds))
    }

    suspend fun updateWidgets(variableId: GlobalVariableId) {
        updateWidgets(variableWidgetsRepository.getVariableWidgetsByVariableId(variableId))
    }

    @JvmName(name = "_updateWidgets")
    private suspend fun updateWidgets(variableWidgets: List<VariableWidget>) {
        variableWidgets.forEach { widget ->
            val variable = try {
                globalVariableRepository.getVariableByKeyOrId(VariableKeyOrId(widget.variableId))
            } catch (_: NoSuchElementException) {
                null
            }
            updateWidget(
                widget,
                globalVariable = variable,
            )
        }
    }

    private fun updateWidget(variableWidget: VariableWidget, globalVariable: GlobalVariable?) {
        RemoteViews(context.packageName, R.layout.variable_widget).also { views ->
            val shortcutId = variableWidget.shortcutId
            val pendingIntent = if (shortcutId != null) {
                ExecuteActivity.IntentBuilder(shortcutId)
                    .trigger(ShortcutTriggerType.WIDGET)
                    .build(context)
                    .let { intent ->
                        PendingIntent.getActivity(
                            context,
                            variableWidget.widgetId,
                            intent,
                            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT,
                        )
                    }
            } else {
                val intent = Intent("http-shortcuts-dummy-action")
                PendingIntent.getBroadcast(context, variableWidget.widgetId, intent, PendingIntent.FLAG_IMMUTABLE)
            }

            views.setOnClickPendingIntent(R.id.widget_base, pendingIntent)

            WidgetBackgroundRenderer.applyBackground(
                context = context,
                views = views,
                widgetId = variableWidget.widgetId,
                containerId = R.id.widget_container,
                backgroundImageViewId = R.id.widget_background,
                background = variableWidget.background,
            )

            val paddingDp = (variableWidget.padding ?: PaddingType.DEFAULT).paddingDp
            val paddingPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, paddingDp.toFloat(), context.resources.displayMetrics).toInt()
            views.setViewPadding(R.id.widget_container, paddingPx, paddingPx, paddingPx, paddingPx)

            val title = variableWidget.title
            if (title.isNotEmpty()) {
                views.setViewVisibility(R.id.widget_title, View.VISIBLE)
                views.setTextViewText(R.id.widget_title, title)
                val titleSize = variableWidget.titleSize ?: WidgetConstants.DEFAULT_TITLE_SIZE
                views.setTextViewTextSize(R.id.widget_title, COMPLEX_UNIT_SP, titleSize)
                val titleColor = variableWidget.titleColor?.let { parseColorSafe(it) }
                    ?: WidgetConstants.DEFAULT_WIDGET_TEXT_COLOR
                views.setTextColor(R.id.widget_title, titleColor)
            } else {
                views.setViewVisibility(R.id.widget_title, View.GONE)
            }

            val value = globalVariable?.value?.ifEmpty { "-" } ?: "???"
            views.setTextViewText(R.id.widget_value, value)
            val valueSize = variableWidget.valueSize ?: WidgetConstants.DEFAULT_VALUE_SIZE
            views.setTextViewTextSize(R.id.widget_value, COMPLEX_UNIT_SP, valueSize)
            val valueColor = variableWidget.valueColor?.let { parseColorSafe(it) }
                ?: WidgetConstants.DEFAULT_WIDGET_TEXT_COLOR
            views.setTextColor(R.id.widget_value, valueColor)

            val unit = variableWidget.unit
            if (!unit.isNullOrEmpty()) {
                views.setViewVisibility(R.id.widget_unit, View.VISIBLE)
                views.setTextViewText(R.id.widget_unit, unit)
                val unitSize = valueSize - WidgetConstants.SUBTITLE_SIZE_OFFSET
                views.setTextViewTextSize(R.id.widget_unit, COMPLEX_UNIT_SP, maxOf(unitSize, 8f))
            } else {
                views.setViewVisibility(R.id.widget_unit, View.GONE)
            }

            val subtitleText = variableWidget.subtitleText
            val widgetWidth = getWidgetWidth(variableWidget.widgetId)
            val showSubtitle = !subtitleText.isNullOrEmpty() && widgetWidth > 160
            val subtitlePosition = variableWidget.subtitlePosition ?: SubtitlePosition.DEFAULT

            if (showSubtitle) {
                val subtitleSize = valueSize - WidgetConstants.SUBTITLE_SIZE_SMALL_OFFSET
                when (subtitlePosition) {
                    SubtitlePosition.TOP_END -> {
                        views.setViewVisibility(R.id.widget_subtitle_top, View.VISIBLE)
                        views.setViewVisibility(R.id.widget_subtitle_bottom, View.GONE)
                        views.setTextViewText(R.id.widget_subtitle_top, subtitleText!!)
                        views.setTextViewTextSize(R.id.widget_subtitle_top, COMPLEX_UNIT_SP, maxOf(subtitleSize, 8f))
                        views.setTextColor(R.id.widget_subtitle_top, WidgetConstants.DEFAULT_SUBTITLE_COLOR)
                    }
                    SubtitlePosition.BOTTOM_END -> {
                        views.setViewVisibility(R.id.widget_subtitle_top, View.GONE)
                        views.setViewVisibility(R.id.widget_subtitle_bottom, View.VISIBLE)
                        views.setTextViewText(R.id.widget_subtitle_bottom, subtitleText!!)
                        views.setTextViewTextSize(R.id.widget_subtitle_bottom, COMPLEX_UNIT_SP, maxOf(subtitleSize, 8f))
                        views.setTextColor(R.id.widget_subtitle_bottom, WidgetConstants.DEFAULT_SUBTITLE_COLOR)
                    }
                }
            } else {
                views.setViewVisibility(R.id.widget_subtitle_top, View.GONE)
                views.setViewVisibility(R.id.widget_subtitle_bottom, View.GONE)
            }

            AppWidgetManager.getInstance(context)
                .updateAppWidget(variableWidget.widgetId, views)
        }
    }

    private fun getWidgetWidth(widgetId: Int): Int {
        return try {
            val options = AppWidgetManager.getInstance(context).getAppWidgetOptions(widgetId)
            options?.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, 0) ?: 0
        } catch (_: Exception) {
            0
        }
    }

    private fun parseColorSafe(colorString: String): Int? = try {
        Color.parseColor(colorString)
    } catch (_: Exception) {
        null
    }

    suspend fun deleteWidgets(widgetIds: List<Int>) {
        variableWidgetsRepository.deleteDeadVariableWidgets()
        variableWidgetsRepository.deleteVariableWidgets(widgetIds)
    }
}
