package ch.rmy.android.http_shortcuts.data.enums

import androidx.annotation.StringRes
import androidx.compose.runtime.Stable
import ch.rmy.android.http_shortcuts.R

@Stable
enum class PaddingType(val value: String, val paddingDp: Int, @StringRes val labelRes: Int) {
    COMPACT("compact", 4, R.string.label_widget_padding_compact),
    STANDARD("standard", 8, R.string.label_widget_padding_standard),
    LOOSE("loose", 16, R.string.label_widget_padding_loose),
    ;

    override fun toString() =
        value

    companion object {
        val DEFAULT = STANDARD

        fun parse(value: String): PaddingType? =
            entries.find { it.value == value }
    }
}
