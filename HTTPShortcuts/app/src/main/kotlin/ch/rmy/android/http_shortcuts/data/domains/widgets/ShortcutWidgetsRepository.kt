package ch.rmy.android.http_shortcuts.data.domains.widgets

import ch.rmy.android.http_shortcuts.data.Database
import ch.rmy.android.http_shortcuts.data.domains.BaseRepository
import ch.rmy.android.http_shortcuts.data.domains.shortcuts.ShortcutId
import ch.rmy.android.http_shortcuts.data.enums.PaddingType
import ch.rmy.android.http_shortcuts.data.enums.WidgetBackgroundType
import ch.rmy.android.http_shortcuts.data.enums.WidgetTextSource
import ch.rmy.android.http_shortcuts.data.models.ShortcutWidget
import javax.inject.Inject

class ShortcutWidgetsRepository
@Inject
constructor(
    database: Database,
) : BaseRepository(database) {
    suspend fun createOrUpdateShortcutWidget(
        widgetId: Int,
        shortcutId: ShortcutId,
        showLabel: Boolean,
        showIcon: Boolean,
        labelColor: String?,
        iconScale: Float,
        background: WidgetBackgroundType? = null,
        titleSize: Float? = null,
        titleColor: String? = null,
        titleFontWeight: Int? = null,
        titleSource: WidgetTextSource? = null,
        titleText: String? = null,
        subtitleSource: WidgetTextSource? = null,
        subtitleText: String? = null,
        subtitleSize: Float? = null,
        subtitleColor: String? = null,
        subtitleFontWeight: Int? = null,
        padding: PaddingType? = null,
    ) = query {
        shortcutWidgetDao().insertOrReplace(
            ShortcutWidget(
                widgetId = widgetId,
                shortcutId = shortcutId,
                showLabel = showLabel,
                showIcon = showIcon,
                labelColor = labelColor,
                iconScale = iconScale,
                background = background,
                titleSize = titleSize,
                titleColor = titleColor,
                titleFontWeight = titleFontWeight,
                titleSource = titleSource,
                titleText = titleText,
                subtitleSource = subtitleSource,
                subtitleText = subtitleText,
                subtitleSize = subtitleSize,
                subtitleColor = subtitleColor,
                subtitleFontWeight = subtitleFontWeight,
                padding = padding,
            ),
        )
    }

    suspend fun getShortcutWidgets(): List<ShortcutWidget> = query {
        shortcutWidgetDao().getWidgets()
    }

    suspend fun getShortcutWidgetById(widgetId: Int): ShortcutWidget? = query {
        shortcutWidgetDao().getWidget(widgetId)
    }

    suspend fun getShortcutWidgetsByIds(widgetIds: List<Int>): List<ShortcutWidget> = query {
        shortcutWidgetDao().getWidgets(widgetIds)
    }

    suspend fun getShortcutWidgetsByShortcutId(shortcutId: ShortcutId): List<ShortcutWidget> = query {
        shortcutWidgetDao().getWidgetsByShortcutId(shortcutId)
    }

    suspend fun deleteDeadShortcutWidgets() = query {
        val shortcutDao = shortcutDao()
        val widgetDao = shortcutWidgetDao()
        widgetDao.getWidgets()
            .mapNotNull { widget ->
                val shortcutExists = shortcutDao.getShortcutById(widget.shortcutId).firstOrNull() != null
                if (shortcutExists) null else widget.widgetId
            }
            .let { deadWidgetIds ->
                widgetDao.deleteWidgets(deadWidgetIds)
            }
    }

    suspend fun deleteShortcutWidgets(widgetIds: List<Int>) = query {
        shortcutWidgetDao().deleteWidgets(widgetIds)
    }
}
