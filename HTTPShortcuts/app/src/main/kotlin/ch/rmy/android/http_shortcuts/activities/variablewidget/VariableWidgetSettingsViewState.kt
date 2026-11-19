package ch.rmy.android.http_shortcuts.activities.variablewidget

import androidx.compose.runtime.Stable
import ch.rmy.android.http_shortcuts.activities.variablewidget.models.SelectableShortcut
import ch.rmy.android.http_shortcuts.activities.variablewidget.models.SelectableVariable
import ch.rmy.android.http_shortcuts.data.domains.shortcuts.ShortcutId
import ch.rmy.android.http_shortcuts.data.enums.FontWeight
import ch.rmy.android.http_shortcuts.data.enums.PaddingType
import ch.rmy.android.http_shortcuts.data.enums.SubtitlePosition
import ch.rmy.android.http_shortcuts.data.enums.WidgetBackgroundType

@Stable
data class VariableWidgetSettingsViewState(
    val selectableVariables: List<SelectableVariable>,
    val selectedVariable: SelectableVariable?,
    val variableValue: String?,
    val fontSize: Int,
    val title: String,
    val background: WidgetBackgroundType?,
    val shortcutId: ShortcutId?,
    val selectableShortcuts: List<SelectableShortcut>,
    val titleSize: Float,
    val titleColor: Int,
    val titleFontWeight: FontWeight,
    val valueSize: Float,
    val valueColor: Int,
    val valueFontWeight: FontWeight,
    val unit: String,
    val unitSize: Float,
    val unitColor: Int,
    val unitFontWeight: FontWeight,
    val subtitleText: String,
    val subtitlePosition: SubtitlePosition,
    val subtitleSize: Float,
    val subtitleColor: Int,
    val subtitleFontWeight: FontWeight,
    val padding: PaddingType,
) {
    val isSaveEnabled
        get() = selectedVariable != null

    val titleColorFormatted
        get() = String.format("#%08x", titleColor)
    val valueColorFormatted
        get() = String.format("#%08x", valueColor)
    val unitColorFormatted
        get() = String.format("#%08x", unitColor)
    val subtitleColorFormatted
        get() = String.format("#%08x", subtitleColor)
}
