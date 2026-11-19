package ch.rmy.android.http_shortcuts.activities.shortcutwidget

import androidx.compose.runtime.Stable
import ch.rmy.android.http_shortcuts.activities.variablewidget.models.SelectableShortcut
import ch.rmy.android.http_shortcuts.data.domains.shortcuts.ShortcutId
import ch.rmy.android.http_shortcuts.data.enums.FontWeight
import ch.rmy.android.http_shortcuts.data.enums.PaddingType
import ch.rmy.android.http_shortcuts.data.enums.WidgetBackgroundType
import ch.rmy.android.http_shortcuts.data.enums.WidgetTextSource

@Stable
data class ShortcutWidgetSettingsViewState(
    val showLabel: Boolean,
    val showIcon: Boolean,
    val labelColor: Int,
    val iconScale: Float,
    val selectableShortcuts: List<SelectableShortcut>,
    val selectedShortcutId: ShortcutId?,
    val background: WidgetBackgroundType?,
    val titleSize: Float,
    val titleColor: Int,
    val titleFontWeight: FontWeight,
    val titleSource: WidgetTextSource,
    val titleText: String,
    val subtitleSource: WidgetTextSource,
    val subtitleText: String,
    val subtitleSize: Float,
    val subtitleColor: Int,
    val subtitleFontWeight: FontWeight,
    val padding: PaddingType,
    val colorDialogVisible: Boolean = false,
    val colorDialogTarget: ColorTarget = ColorTarget.NONE,
) {
    val labelColorFormatted
        get() = String.format("#%06x", labelColor and 0xffffff)
    val titleColorFormatted
        get() = String.format("#%08x", titleColor)
    val subtitleColorFormatted
        get() = String.format("#%08x", subtitleColor)
}

enum class ColorTarget {
    NONE,
    LABEL,
    TITLE,
    SUBTITLE,
}
