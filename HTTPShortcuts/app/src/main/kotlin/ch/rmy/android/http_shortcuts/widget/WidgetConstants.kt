package ch.rmy.android.http_shortcuts.widget

import ch.rmy.android.http_shortcuts.data.enums.FontWeight

object WidgetConstants {
    val ICON_SCALE_OPTIONS = listOf(0.5f, 0.75f, 1f, 1.25f, 1.5f, 1.75f, 2f)

    const val FONT_SIZE_MIN = 6f
    const val FONT_SIZE_MAX = 48f
    const val FONT_SIZE_STEP = 2f

    const val DEFAULT_TITLE_SIZE = 20f
    const val DEFAULT_TITLE_COLOR = 0xCC000000.toInt()
    val DEFAULT_TITLE_FONT_WEIGHT = FontWeight.DEMI_BOLD

    const val DEFAULT_VALUE_SIZE = 42f
    const val DEFAULT_VALUE_COLOR = 0xCC000000.toInt()
    val DEFAULT_VALUE_FONT_WEIGHT = FontWeight.SEMI_BOLD

    const val DEFAULT_UNIT_SIZE = 20f
    const val DEFAULT_UNIT_COLOR = 0x80000000.toInt()
    val DEFAULT_UNIT_FONT_WEIGHT = FontWeight.HEAVY

    const val DEFAULT_SUBTITLE_SIZE = 12f
    const val DEFAULT_SUBTITLE_COLOR = 0x80000000.toInt()
    val DEFAULT_SUBTITLE_FONT_WEIGHT = FontWeight.REGULAR

    const val WIDGET_BACKGROUND_MAX_SIZE_PX = 400
    const val WIDGET_BACKGROUND_CACHE_MAX_SIZE_BYTES = 4 * 1024 * 1024L
}
