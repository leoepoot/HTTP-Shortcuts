package ch.rmy.android.http_shortcuts.data.enums

import androidx.compose.runtime.Stable

@Stable
enum class WidgetTextSource(val value: String) {
    SHORTCUT_NAME("shortcut_name"),
    SHORTCUT_DESCRIPTION("shortcut_description"),
    CUSTOM("custom"),
    ;

    override fun toString() =
        value

    companion object {
        val DEFAULT_TITLE = SHORTCUT_NAME
        val DEFAULT_SUBTITLE = SHORTCUT_DESCRIPTION

        fun parse(value: String): WidgetTextSource? =
            entries.find { it.value == value }
    }
}
