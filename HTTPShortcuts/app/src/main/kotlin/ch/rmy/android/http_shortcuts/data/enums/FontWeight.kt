package ch.rmy.android.http_shortcuts.data.enums

import androidx.annotation.StringRes
import androidx.compose.runtime.Stable
import ch.rmy.android.http_shortcuts.R

@Stable
enum class FontWeight(
    val weight: Int,
    @StringRes val labelRes: Int,
    val familyName: String,
    val renderWeight: Int,
) {
    THIN(100, R.string.label_font_weight_thin, "sans-serif-thin", 100),
    EXTRA_LIGHT(200, R.string.label_font_weight_extra_light, "sans-serif-light", 200),
    LIGHT(300, R.string.label_font_weight_light, "sans-serif-light", 300),
    NORMAL(350, R.string.label_font_weight_normal, "sans-serif", 350),
    REGULAR(400, R.string.label_font_weight_regular, "sans-serif", 400),
    MEDIUM(500, R.string.label_font_weight_medium, "sans-serif-medium", 500),
    SEMI_BOLD(600, R.string.label_font_weight_semi_bold, "sans-serif-medium", 600),
    DEMI_BOLD(650, R.string.label_font_weight_demi_bold, "sans-serif-bold", 800),
    BOLD(700, R.string.label_font_weight_bold, "sans-serif-bold", 900),
    HEAVY(800, R.string.label_font_weight_heavy, "sans-serif-black", 950);

    companion object {
        fun fromWeight(weight: Int?): FontWeight =
            entries.firstOrNull { it.weight == weight } ?: REGULAR
    }
}
