package ch.rmy.android.http_shortcuts.activities.variablewidget

import androidx.compose.runtime.Stable
import ch.rmy.android.http_shortcuts.activities.variablewidget.models.SelectableShortcut
import ch.rmy.android.http_shortcuts.activities.variablewidget.models.SelectableVariable
import ch.rmy.android.http_shortcuts.data.domains.shortcuts.ShortcutId
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
    val titleSize: Int,
    val titleColor: Int,
    val valueSize: Int,
    val valueColor: Int,
    val unit: String,
    val subtitleText: String,
    val subtitlePosition: SubtitlePosition,
    val padding: PaddingType,
    val colorDialogVisible: Boolean = false,
    val colorDialogTarget: ColorTarget = ColorTarget.NONE,
) {
    val isSaveEnabled
        get() = selectedVariable != null

    val titleColorFormatted
        get() = String.format("#%08x", titleColor)
    val valueColorFormatted
        get() = String.format("#%08x", valueColor)
}

enum class ColorTarget {
    NONE,
    TITLE,
    VALUE,
}
