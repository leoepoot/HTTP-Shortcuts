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
import ch.rmy.android.http_shortcuts.data.enums.FontWeight
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
        titleFontWeight: Int? = null,
        valueSize: Float? = null,
        valueColor: String? = null,
        valueFontWeight: Int? = null,
        unit: String? = null,
        unitSize: Float? = null,
        unitColor: String? = null,
        unitFontWeight: Int? = null,
        subtitleText: String? = null,
        subtitlePosition: SubtitlePosition? = null,
        subtitleSize: Float? = null,
        subtitleColor: String? = null,
        subtitleFontWeight: Int? = null,
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
            titleFontWeight = titleFontWeight,
            valueSize = valueSize,
            valueColor = valueColor,
            valueFontWeight = valueFontWeight,
            unit = unit,
            unitSize = unitSize,
            unitColor = unitColor,
            unitFontWeight = unitFontWeight,
            subtitleText = subtitleText,
            subtitlePosition = subtitlePosition,
            subtitleSize = subtitleSize,
            subtitleColor = subtitleColor,
            subtitleFontWeight = subtitleFontWeight,
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
                backgroundImageViewId = R.id.widget_background,
                background = variableWidget.background,
            )

            val paddingDp = (variableWidget.padding ?: PaddingType.DEFAULT).paddingDp
            val paddingPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, paddingDp.toFloat(), context.resources.displayMetrics).toInt()
            views.setViewPadding(R.id.widget_container, paddingPx, paddingPx, paddingPx, paddingPx)

            val titleFontWeight = (variableWidget.titleFontWeight?.let { FontWeight.fromWeight(it) } ?: WidgetConstants.DEFAULT_TITLE_FONT_WEIGHT).weight
            val valueFontWeight = (variableWidget.valueFontWeight?.let { FontWeight.fromWeight(it) } ?: WidgetConstants.DEFAULT_VALUE_FONT_WEIGHT).weight
            val unitFontWeight = (variableWidget.unitFontWeight?.let { FontWeight.fromWeight(it) } ?: WidgetConstants.DEFAULT_UNIT_FONT_WEIGHT).weight
            val subtitleFontWeight = (variableWidget.subtitleFontWeight?.let { FontWeight.fromWeight(it) } ?: WidgetConstants.DEFAULT_SUBTITLE_FONT_WEIGHT).weight

            val title = variableWidget.title
            if (title.isNotEmpty()) {
                views.setViewVisibility(R.id.widget_title, View.VISIBLE)
                views.setTextViewTextWithFontWeight(R.id.widget_title, title, titleFontWeight)
                val titleSize = variableWidget.titleSize ?: WidgetConstants.DEFAULT_TITLE_SIZE
                views.setTextViewTextSize(R.id.widget_title, COMPLEX_UNIT_SP, titleSize)
                val titleColor = variableWidget.titleColor?.let { parseColorSafe(it) }
                    ?: WidgetConstants.DEFAULT_TITLE_COLOR
                views.setTextColor(R.id.widget_title, titleColor)
                views.setTextViewFontWeight(R.id.widget_title, titleFontWeight)
            } else {
                views.setViewVisibility(R.id.widget_title, View.GONE)
            }

            val value = globalVariable?.value?.ifEmpty { "-" } ?: "???"
            views.setTextViewTextWithFontWeight(R.id.widget_value, value, valueFontWeight)
            val valueSize = variableWidget.valueSize ?: WidgetConstants.DEFAULT_VALUE_SIZE
            views.setTextViewTextSize(R.id.widget_value, COMPLEX_UNIT_SP, valueSize)
            val valueColor = variableWidget.valueColor?.let { parseColorSafe(it) }
                ?: WidgetConstants.DEFAULT_VALUE_COLOR
            views.setTextColor(R.id.widget_value, valueColor)
            views.setTextViewFontWeight(R.id.widget_value, valueFontWeight)

            val unit = variableWidget.unit
            if (!unit.isNullOrEmpty()) {
                views.setViewVisibility(R.id.widget_unit, View.VISIBLE)
                views.setTextViewTextWithFontWeight(R.id.widget_unit, unit, unitFontWeight)
                val unitSize = variableWidget.unitSize ?: WidgetConstants.DEFAULT_UNIT_SIZE
                views.setTextViewTextSize(R.id.widget_unit, COMPLEX_UNIT_SP, unitSize)
                val unitColor = variableWidget.unitColor?.let { parseColorSafe(it) }
                    ?: WidgetConstants.DEFAULT_UNIT_COLOR
                views.setTextColor(R.id.widget_unit, unitColor)
                views.setTextViewFontWeight(R.id.widget_unit, unitFontWeight)
            } else {
                views.setViewVisibility(R.id.widget_unit, View.GONE)
            }

            val subtitleText = variableWidget.subtitleText
            val widgetWidth = getWidgetWidth(variableWidget.widgetId)
            val showSubtitle = !subtitleText.isNullOrEmpty() && widgetWidth > 160
            val subtitlePosition = variableWidget.subtitlePosition ?: SubtitlePosition.DEFAULT
            val subtitleSize = variableWidget.subtitleSize ?: WidgetConstants.DEFAULT_SUBTITLE_SIZE
            val subtitleColor = variableWidget.subtitleColor?.let { parseColorSafe(it) }
                ?: WidgetConstants.DEFAULT_SUBTITLE_COLOR

            if (showSubtitle) {
                when (subtitlePosition) {
                    SubtitlePosition.TOP_END -> {
                        views.setViewVisibility(R.id.widget_subtitle_top, View.VISIBLE)
                        views.setViewVisibility(R.id.widget_subtitle_bottom, View.GONE)
                        views.setTextViewTextWithFontWeight(R.id.widget_subtitle_top, subtitleText!!, subtitleFontWeight)
                        views.setTextViewTextSize(R.id.widget_subtitle_top, COMPLEX_UNIT_SP, subtitleSize)
                        views.setTextColor(R.id.widget_subtitle_top, subtitleColor)
                        views.setTextViewFontWeight(R.id.widget_subtitle_top, subtitleFontWeight)
                    }
                    SubtitlePosition.BOTTOM_END -> {
                        views.setViewVisibility(R.id.widget_subtitle_top, View.GONE)
                        views.setViewVisibility(R.id.widget_subtitle_bottom, View.VISIBLE)
                        views.setTextViewTextWithFontWeight(R.id.widget_subtitle_bottom, subtitleText!!, subtitleFontWeight)
                        views.setTextViewTextSize(R.id.widget_subtitle_bottom, COMPLEX_UNIT_SP, subtitleSize)
                        views.setTextColor(R.id.widget_subtitle_bottom, subtitleColor)
                        views.setTextViewFontWeight(R.id.widget_subtitle_bottom, subtitleFontWeight)
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
