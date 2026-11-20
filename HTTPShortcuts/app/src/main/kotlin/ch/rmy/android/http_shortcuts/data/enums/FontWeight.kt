package ch.rmy.android.http_shortcuts.data.enums

import androidx.annotation.StringRes
import androidx.compose.runtime.Stable
import ch.rmy.android.http_shortcuts.R

@Stable
enum class FontWeight(
    val weight: Int,
    @StringRes val labelRes: Int,
    val familyName: String,
    val miproFamilyName: String,
) {
    THIN(100, R.string.label_font_weight_thin, "sans-serif-thin", "mipro-thin"),
    EXTRA_LIGHT(200, R.string.label_font_weight_extra_light, "sans-serif-light", "mipro-extralight"),
    LIGHT(300, R.string.label_font_weight_light, "sans-serif-light", "mipro-light"),
    NORMAL(350, R.string.label_font_weight_normal, "sans-serif", "mipro-normal"),
    REGULAR(400, R.string.label_font_weight_regular, "sans-serif", "sans-serif"),
    MEDIUM(500, R.string.label_font_weight_medium, "sans-serif-medium", "mipro-medium"),
    SEMI_BOLD(600, R.string.label_font_weight_semi_bold, "sans-serif-medium", "mipro-demibold"),
    DEMI_BOLD(650, R.string.label_font_weight_demi_bold, "sans-serif-bold", "mipro-semibold"),
    BOLD(700, R.string.label_font_weight_bold, "sans-serif-bold", "mipro-bold"),
    HEAVY(800, R.string.label_font_weight_heavy, "sans-serif-black", "mipro-heavy");

    companion object {
        fun fromWeight(weight: Int?): FontWeight =
            entries.firstOrNull { it.weight == weight } ?: REGULAR
    }
}
