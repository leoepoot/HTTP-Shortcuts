package ch.rmy.android.http_shortcuts.widget

object WidgetConstants {
    val ICON_SCALE_OPTIONS = listOf(0.5f, 0.75f, 1f, 1.25f, 1.5f, 1.75f, 2f)
    val FONT_SIZE_OPTIONS = listOf(0.5f, 0.75f, 1f, 1.25f, 1.5f, 1.75f, 2f)

    val TITLE_SIZE_OPTIONS = listOf(12f, 14f, 16f)
    const val DEFAULT_TITLE_SIZE = 14f

    val VALUE_SIZE_OPTIONS = listOf(20f, 24f, 28f)
    const val DEFAULT_VALUE_SIZE = 24f

    val ICON_SIZE_OPTIONS = listOf(32f, 48f, 64f)
    const val DEFAULT_ICON_SIZE = 48f

    const val SUBTITLE_SIZE_OFFSET = 4f
    const val SUBTITLE_SIZE_SMALL_OFFSET = 6f

    const val DEFAULT_WIDGET_TEXT_COLOR = android.graphics.Color.WHITE
    const val DEFAULT_SUBTITLE_COLOR = 0x88888888.toInt()

    const val WIDGET_BACKGROUND_MAX_SIZE_PX = 400
    const val WIDGET_BACKGROUND_CACHE_MAX_SIZE_BYTES = 4 * 1024 * 1024L
}
