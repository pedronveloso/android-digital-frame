package com.pedronveloso.digitalframe.ui

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val MyTypography =
    Typography(
        bodyMedium =
            TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                letterSpacing = 0.5.sp,
            ),
        bodyLarge =
            TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                lineHeight = 18.sp,
                letterSpacing = 0.5.sp,
            ),
        titleLarge =
            TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                fontSize = 24.sp,
                lineHeight = 28.sp,
                letterSpacing = 0.sp,
            ),
        titleMedium =
            TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                fontSize = 20.sp,
                lineHeight = 22.sp,
                letterSpacing = 0.sp,
            ),
        titleSmall =
            TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Light,
                fontSize = 20.sp,
                lineHeight = 22.sp,
                letterSpacing = 0.sp,
            ),
        labelSmall =
            TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp,
                lineHeight = 16.sp,
                letterSpacing = 0.5.sp,
            ),
        displayLarge =
            TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Medium,
                fontSize = 60.sp,
                lineHeight = 62.sp,
                letterSpacing = 0.5.sp,
            ),
        displayMedium =
            TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Medium,
                fontSize = 46.sp,
                lineHeight = 48.sp,
                letterSpacing = 0.5.sp,
            ),
        displaySmall =
            TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Medium,
                fontSize = 38.sp,
                lineHeight = 44.sp,
                letterSpacing = 0.5.sp,
            ),
    )
