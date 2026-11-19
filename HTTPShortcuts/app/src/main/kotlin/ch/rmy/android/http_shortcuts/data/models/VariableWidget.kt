package ch.rmy.android.http_shortcuts.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ch.rmy.android.http_shortcuts.data.domains.shortcuts.ShortcutId
import ch.rmy.android.http_shortcuts.data.domains.variables.GlobalVariableId
import ch.rmy.android.http_shortcuts.data.enums.PaddingType
import ch.rmy.android.http_shortcuts.data.enums.SubtitlePosition
import ch.rmy.android.http_shortcuts.data.enums.WidgetBackgroundType

@Entity(tableName = "variable_widget")
data class VariableWidget(
    @PrimaryKey
    @ColumnInfo(name = "widget_id")
    val widgetId: Int,
    @ColumnInfo(name = "variable_id", index = true)
    val variableId: GlobalVariableId,
    @ColumnInfo(name = "font_size", defaultValue = "22")
    val fontSize: Int,
    @ColumnInfo(name = "Title", defaultValue = "")
    val title: String,
    @ColumnInfo(name = "shortcut_id")
    val shortcutId: ShortcutId? = null,
    @ColumnInfo(name = "background")
    val background: WidgetBackgroundType? = null,
    @ColumnInfo(name = "title_size")
    val titleSize: Float? = null,
    @ColumnInfo(name = "title_color")
    val titleColor: String? = null,
    @ColumnInfo(name = "value_size")
    val valueSize: Float? = null,
    @ColumnInfo(name = "value_color")
    val valueColor: String? = null,
    @ColumnInfo(name = "unit")
    val unit: String? = null,
    @ColumnInfo(name = "subtitle_text")
    val subtitleText: String? = null,
    @ColumnInfo(name = "subtitle_position")
    val subtitlePosition: SubtitlePosition? = null,
    @ColumnInfo(name = "padding")
    val padding: PaddingType? = null,
    @ColumnInfo(name = "title_font_weight")
    val titleFontWeight: Int? = null,
    @ColumnInfo(name = "value_font_weight")
    val valueFontWeight: Int? = null,
    @ColumnInfo(name = "unit_size")
    val unitSize: Float? = null,
    @ColumnInfo(name = "unit_color")
    val unitColor: String? = null,
    @ColumnInfo(name = "unit_font_weight")
    val unitFontWeight: Int? = null,
    @ColumnInfo(name = "subtitle_size")
    val subtitleSize: Float? = null,
    @ColumnInfo(name = "subtitle_color")
    val subtitleColor: String? = null,
    @ColumnInfo(name = "subtitle_font_weight")
    val subtitleFontWeight: Int? = null,
)
