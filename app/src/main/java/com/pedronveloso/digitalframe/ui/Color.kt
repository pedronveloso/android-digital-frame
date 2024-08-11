package com.pedronveloso.digitalframe.ui

import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

fun deriveHUDColor(backgroundHsl: FloatArray): Color {
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
