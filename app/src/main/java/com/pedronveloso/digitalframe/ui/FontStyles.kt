package com.pedronveloso.digitalframe.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle

object FontStyles {
    fun textStyleTitleLarge(backgroundHsl: FloatArray) =
        MyTypography.titleLarge.copy(
            color = deriveTextColor(backgroundHsl),
            shadow =
                Shadow(
                    color = Color.Black,
                    offset = Offset(0f, 2f),
                    blurRadius = 1f,
                ),
        )

    fun textStyleTitleMedium(backgroundHsl: FloatArray) =
        MyTypography.titleMedium.copy(
            color = deriveTextColor(backgroundHsl),
            shadow =
                Shadow(
                    color = Color.Black,
                    offset = Offset(0f, 2f),
                    blurRadius = 1f,
                ),
        )

    fun textStyleBodyLarge(backgroundHsl: FloatArray) =
        MyTypography.bodyLarge.copy(
            color = deriveTextColor(backgroundHsl),
            shadow =
                Shadow(
                    color = Color.Black,
                    offset = Offset(0f, 2f),
                    blurRadius = 1f,
                ),
        )

    fun textStyleBodyMedium(backgroundHsl: FloatArray) =
        MyTypography.bodyMedium.copy(
            color = deriveTextColor(backgroundHsl),
            shadow =
                Shadow(
                    color = Color.Black,
                    offset = Offset(0f, 2f),
                    blurRadius = 1f,
                ),
        )

    fun textStyleDisplayLarge(backgroundHsl: FloatArray): TextStyle {
        val textColor: Color = deriveTextColor(backgroundHsl)

        return MyTypography.displayLarge.copy(
            color = textColor,
            shadow =
                Shadow(
                    color = Color.Black,
                    offset = Offset(0f, 2f),
                    blurRadius = 1f,
                ),
        )
    }

    fun textStyleDisplayMedium(backgroundHsl: FloatArray) =
        MyTypography.displayMedium.copy(
            color = deriveTextColor(backgroundHsl),
            shadow =
                Shadow(
                    color = Color.Black,
                    offset = Offset(0f, 2f),
                    blurRadius = 1f,
                ),
        )

    private fun deriveTextColor(backgroundHsl: FloatArray): Color {
        var textColor: Color =
            if (backgroundHsl[2] > 0.5f) {
                Color.Black
            } else {
                Color.White
            }

        // If Hue or Saturation are medium to high, prefer white text.
        if (backgroundHsl[0] > 30f || backgroundHsl[1] > 0.2f) {
            textColor = Color.White
        }

        // TODO: Support for complementary colors. Didn't like the results so far, but I'll keep it here for now.
        /* val textColor : Color
        if (backgroundHsl[2] > 0.8f) {
            textColor = Color.Black
        } else if (backgroundHsl[2] < 0.2f) {
            textColor = Color.White
        } else {
            // Calculate complementary color.
            val complementaryHsl = floatArrayOf(
                backgroundHsl[0] + 180f,
                backgroundHsl[1],
                backgroundHsl[2]
            )
            textColor = Color(ColorUtils.HSLToColor(complementaryHsl))
        } */

        return textColor
    }
}
