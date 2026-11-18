package ch.rmy.android.http_shortcuts.data.enums

import androidx.annotation.ColorInt
import androidx.compose.runtime.Stable
import ch.rmy.android.http_shortcuts.utils.ColorUtil.colorIntToHexString
import ch.rmy.android.http_shortcuts.utils.ColorUtil.colorIntWithAlphaToHexString

@Stable
sealed interface WidgetBackgroundType {

    fun serialize(): String

    @Stable
    data object None : WidgetBackgroundType {
        const val VALUE = "none"

        override fun serialize() = VALUE
    }

    @Stable
    data class Color(@ColorInt val color: Int) : WidgetBackgroundType {
        override fun serialize(): String = "$PREFIX${color.colorIntWithAlphaToHexString()}"

        fun toHumanReadableString(): String {
            val alpha = android.graphics.Color.alpha(color)
            val string = "#${color.colorIntToHexString()}"
            if (alpha == 0xff) {
                return string
            }
            val percentage = ((alpha.toFloat() / 0xff) * 100).toInt()
            return "$string ($percentage%)"
        }

        override fun toString() =
            serialize()

        companion object {
            const val PREFIX = "color="

            fun deserialize(input: String): Color? =
                input.removePrefix(PREFIX)
                    .toUIntOrNull(16)
                    ?.toInt()
                    ?.let(::Color)
        }
    }

    @Stable
    data class Image(val fileName: String) : WidgetBackgroundType {
        override fun serialize(): String = "$PREFIX$fileName"

        override fun toString() =
            serialize()

        companion object {
            const val PREFIX = "image="

            fun deserialize(input: String): Image? =
                input.removePrefix(PREFIX)
                    .takeIf { it.isNotEmpty() }
                    ?.let(::Image)
        }
    }

    companion object {
        fun parse(type: String): WidgetBackgroundType? =
            when {
                type == None.VALUE -> None
                type.startsWith(Color.PREFIX) -> Color.deserialize(type)
                type.startsWith(Image.PREFIX) -> Image.deserialize(type)
                else -> null
            }
    }
}
