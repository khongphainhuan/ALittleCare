package com.nhuanmai.alittlecare.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Gán màu thiết kế vào các "vai trò" màu của Material 3
private val LightColors = lightColorScheme(
    primary = Blue,
    onPrimary = Color.White,          // chữ trên nút màu chính
    primaryContainer = BlueLight,
    onPrimaryContainer = BlueDark,
    background = Background,
    onBackground = TextPrimary,
    surface = Color.White,            // nền thẻ, hộp thoại
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary, // chữ phụ
    outline = Border,
    error = Danger,
    onError = Color.White,
)

@Composable
fun ALittleCareTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = Typography,
        content = content
    )
}