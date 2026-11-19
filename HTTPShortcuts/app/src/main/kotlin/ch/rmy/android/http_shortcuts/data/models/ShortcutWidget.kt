package ch.rmy.android.http_shortcuts.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ch.rmy.android.http_shortcuts.data.domains.shortcuts.ShortcutId
import ch.rmy.android.http_shortcuts.data.enums.PaddingType
import ch.rmy.android.http_shortcuts.data.enums.WidgetBackgroundType
import ch.rmy.android.http_shortcuts.data.enums.WidgetTextSource

@Entity(tableName = "widget")
data class ShortcutWidget(
    @PrimaryKey
    @ColumnInfo(name = "widget_id")
    val widgetId: Int,
    @ColumnInfo(name = "shortcut_id", index = true)
    val shortcutId: ShortcutId,
    @ColumnInfo(name = "label_color")
    val labelColor: String?,
    @ColumnInfo(name = "show_label")
    val showLabel: Boolean,
    @ColumnInfo(name = "show_icon")
    val showIcon: Boolean,
    @ColumnInfo(name = "icon_scale")
    val iconScale: Float,
    @ColumnInfo(name = "background")
    val background: WidgetBackgroundType? = null,
    @ColumnInfo(name = "title_size")
    val titleSize: Float? = null,
    @ColumnInfo(name = "title_color")
    val titleColor: String? = null,
    @ColumnInfo(name = "title_source")
    val titleSource: WidgetTextSource? = null,
    @ColumnInfo(name = "title_text")
    val titleText: String? = null,
    @ColumnInfo(name = "subtitle_source")
    val subtitleSource: WidgetTextSource? = null,
    @ColumnInfo(name = "subtitle_text")
    val subtitleText: String? = null,
    @ColumnInfo(name = "padding")
    val padding: PaddingType? = null,
    @ColumnInfo(name = "title_font_weight")
    val titleFontWeight: Int? = null,
    @ColumnInfo(name = "subtitle_size")
    val subtitleSize: Float? = null,
    @ColumnInfo(name = "subtitle_color")
    val subtitleColor: String? = null,
    @ColumnInfo(name = "subtitle_font_weight")
    val subtitleFontWeight: Int? = null,
)
