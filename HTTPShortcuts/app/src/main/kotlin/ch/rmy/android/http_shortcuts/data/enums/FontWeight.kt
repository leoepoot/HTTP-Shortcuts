package ch.rmy.android.http_shortcuts.data.enums

import androidx.annotation.StringRes
import androidx.compose.runtime.Stable
import ch.rmy.android.http_shortcuts.R

@Stable
enum class FontWeight(
    val weight: Int,
    @StringRes val labelRes: Int,
    val familyName: String,
) {
    THIN(100, R.string.label_font_weight_thin, "sans-serif-thin"),
    EXTRA_LIGHT(200, R.string.label_font_weight_extra_light, "sans-serif-light"),
    LIGHT(300, R.string.label_font_weight_light, "sans-serif-light"),
    NORMAL(350, R.string.label_font_weight_normal, "sans-serif"),
    REGULAR(400, R.string.label_font_weight_regular, "sans-serif"),
    MEDIUM(500, R.string.label_font_weight_medium, "sans-serif-medium"),
    SEMI_BOLD(600, R.string.label_font_weight_semi_bold, "sans-serif-medium"),
    DEMI_BOLD(650, R.string.label_font_weight_demi_bold, "sans-serif-bold"),
    BOLD(700, R.string.label_font_weight_bold, "sans-serif-bold"),
    HEAVY(800, R.string.label_font_weight_heavy, "sans-serif-black");

    companion object {
        fun fromWeight(weight: Int?): FontWeight =
            entries.firstOrNull { it.weight == weight } ?: REGULAR
    }
}
