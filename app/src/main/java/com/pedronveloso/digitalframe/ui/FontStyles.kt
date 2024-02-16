package com.pedronveloso.digitalframe.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle

object FontStyles {

    fun textStyleTitleLarge(textColor: Color) =
        MyTypography.titleLarge.copy(
            color = textColor,
            shadow =
                Shadow(
                    color = Color.Black,
                    offset = Offset(0f, 2f),
                    blurRadius = 1f,
                ),
        )

    fun textStyleTitleMedium(textColor: Color) =
        MyTypography.titleMedium.copy(
            color = textColor,
            shadow =
            Shadow(
                color = Color.Black,
                offset = Offset(0f, 2f),
                blurRadius = 1f,
            ),
        )

    fun textStyleBodyLarge(textColor: Color) =
        MyTypography.bodyLarge.copy(
            color = textColor,
            shadow =
                Shadow(
                    color = Color.Black,
                    offset = Offset(0f, 2f),
                    blurRadius = 1f,
                ),
        )

    fun textStyleBodyMedium(textColor: Color) =
        MyTypography.bodyMedium.copy(
            color = textColor,
            shadow =
                Shadow(
                    color = Color.Black,
                    offset = Offset(0f, 2f),
                    blurRadius = 1f,
                ),
        )

    fun textStyleDisplayLarge(textColor: Color): TextStyle {
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

    fun textStyleDisplayMedium(textColor: Color) =
        MyTypography.displayMedium.copy(
            color = textColor,
            shadow =
                Shadow(
                    color = Color.Black,
                    offset = Offset(0f, 2f),
                    blurRadius = 1f,
                ),
        )

}
