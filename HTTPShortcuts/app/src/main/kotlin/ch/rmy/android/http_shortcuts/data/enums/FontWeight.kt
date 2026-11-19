package ch.rmy.android.http_shortcuts.data.enums

import androidx.annotation.StringRes
import androidx.compose.runtime.Stable
import ch.rmy.android.http_shortcuts.R

@Stable
enum class FontWeight(
    val weight: Int,
    @StringRes val labelRes: Int,
) {
    THIN(100, R.string.label_font_weight_thin),
    EXTRA_LIGHT(200, R.string.label_font_weight_extra_light),
    LIGHT(300, R.string.label_font_weight_light),
    NORMAL(350, R.string.label_font_weight_normal),
    REGULAR(400, R.string.label_font_weight_regular),
    MEDIUM(500, R.string.label_font_weight_medium),
    SEMI_BOLD(600, R.string.label_font_weight_semi_bold),
    DEMI_BOLD(650, R.string.label_font_weight_demi_bold),
    BOLD(700, R.string.label_font_weight_bold),
    HEAVY(800, R.string.label_font_weight_heavy);

    companion object {
        fun fromWeight(weight: Int?): FontWeight =
            entries.firstOrNull { it.weight == weight } ?: REGULAR
    }
}
