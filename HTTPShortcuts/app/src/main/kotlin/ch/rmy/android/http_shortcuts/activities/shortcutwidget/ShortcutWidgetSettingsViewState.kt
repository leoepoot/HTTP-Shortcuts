package ch.rmy.android.http_shortcuts.activities.shortcutwidget

import androidx.compose.runtime.Stable
import ch.rmy.android.http_shortcuts.data.enums.PaddingType
import ch.rmy.android.http_shortcuts.data.enums.WidgetBackgroundType
import ch.rmy.android.http_shortcuts.data.enums.WidgetTextSource
import ch.rmy.android.http_shortcuts.icons.ShortcutIcon

@Stable
data class ShortcutWidgetSettingsViewState(
    val showLabel: Boolean,
    val showIcon: Boolean,
    val labelColor: Int,
    val iconScale: Float,
    val shortcutName: String,
    val shortcutDescription: String,
    val shortcutIcon: ShortcutIcon,
    val background: WidgetBackgroundType?,
    val titleSize: Int,
    val titleColor: Int,
    val titleSource: WidgetTextSource,
    val titleText: String,
    val subtitleSource: WidgetTextSource,
    val subtitleText: String,
    val padding: PaddingType,
    val colorDialogVisible: Boolean = false,
    val colorDialogTarget: ColorTarget = ColorTarget.NONE,
) {
    val labelColorFormatted
        get() = String.format("#%06x", labelColor and 0xffffff)
    val titleColorFormatted
        get() = String.format("#%08x", titleColor)
}

enum class ColorTarget {
    NONE,
    LABEL,
    TITLE,
}
