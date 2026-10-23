package com.nhuanmai.alittlecare.model

import java.time.LocalTime

// Tóm tắt tình hình check-in để hiển thị ở Trang chủ
data class CheckInSummary(
    val checkedInTodayAt: LocalTime?, // giờ xác nhận hôm nay; null nếu hôm nay chưa xác nhận
    val streakDays: Int,              // số ngày xác nhận liên tiếp
)