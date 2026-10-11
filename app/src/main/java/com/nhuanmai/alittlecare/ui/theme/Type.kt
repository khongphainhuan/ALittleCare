package com.nhuanmai.alittlecare.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.nhuanmai.alittlecare.R

// Font Be Vietnam Pro với 4 độ đậm dùng trong thiết kế
val BeVietnamPro = FontFamily(
    Font(R.font.be_vietnam_pro_regular, FontWeight.Normal),
    Font(R.font.be_vietnam_pro_medium, FontWeight.Medium),
    Font(R.font.be_vietnam_pro_semibold, FontWeight.SemiBold),
    Font(R.font.be_vietnam_pro_bold, FontWeight.Bold),
)

// Giữ nguyên cỡ chữ mặc định của Material 3, chỉ đổi font
private val default = Typography()

val Typography = Typography(
    displayLarge = default.displayLarge.copy(fontFamily = BeVietnamPro),
    displayMedium = default.displayMedium.copy(fontFamily = BeVietnamPro),
    displaySmall = default.displaySmall.copy(fontFamily = BeVietnamPro),
    headlineLarge = default.headlineLarge.copy(fontFamily = BeVietnamPro),
    headlineMedium = default.headlineMedium.copy(fontFamily = BeVietnamPro),
    headlineSmall = default.headlineSmall.copy(fontFamily = BeVietnamPro),
    titleLarge = default.titleLarge.copy(fontFamily = BeVietnamPro),
    titleMedium = default.titleMedium.copy(fontFamily = BeVietnamPro),
    titleSmall = default.titleSmall.copy(fontFamily = BeVietnamPro),
    bodyLarge = default.bodyLarge.copy(fontFamily = BeVietnamPro),
    bodyMedium = default.bodyMedium.copy(fontFamily = BeVietnamPro),
    bodySmall = default.bodySmall.copy(fontFamily = BeVietnamPro),
    labelLarge = default.labelLarge.copy(fontFamily = BeVietnamPro),
    labelMedium = default.labelMedium.copy(fontFamily = BeVietnamPro),
    labelSmall = default.labelSmall.copy(fontFamily = BeVietnamPro),
)