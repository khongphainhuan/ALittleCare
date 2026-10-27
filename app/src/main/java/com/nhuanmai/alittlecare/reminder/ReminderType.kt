package com.nhuanmai.alittlecare.reminder

import androidx.annotation.StringRes
import com.nhuanmai.alittlecare.R
import java.time.LocalTime

// Hai lần nhắc mỗi ngày. Giờ nhắc đặt ở đây là nơi duy nhất trong app;
// sau này người dùng chỉnh được ở màn Thiết lập (Mức 2).
enum class ReminderType(
    val time: LocalTime,
    val workName: String,          // tên công việc nền, dùng để không lên lịch trùng
    val notificationId: Int,       // mỗi loại một id để thông báo sau thay thế thông báo trước cùng loại
    @StringRes val titleRes: Int,
    @StringRes val textRes: Int,
) {
    MORNING(
        time = LocalTime.of(8, 0),
        workName = "reminder_morning",
        notificationId = 1,
        titleRes = R.string.reminder_morning_title,
        textRes = R.string.reminder_morning_text,
    ),
    EVENING(
        time = LocalTime.of(21, 0),
        workName = "reminder_evening",
        notificationId = 2,
        titleRes = R.string.reminder_evening_title,
        textRes = R.string.reminder_evening_text,
    ),
}