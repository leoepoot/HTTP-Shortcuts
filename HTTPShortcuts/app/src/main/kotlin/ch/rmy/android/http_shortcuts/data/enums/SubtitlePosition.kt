package ch.rmy.android.http_shortcuts.data.enums

import androidx.compose.runtime.Stable

@Stable
enum class SubtitlePosition(val value: String) {
    TOP_END("top_end"),
    BOTTOM_END("bottom_end"),
    ;

    override fun toString() =
        value

    companion object {
        val DEFAULT = TOP_END

        fun parse(value: String): SubtitlePosition? =
            entries.find { it.value == value }
    }
}
