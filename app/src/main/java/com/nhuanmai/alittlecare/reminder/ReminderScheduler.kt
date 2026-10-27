package com.nhuanmai.alittlecare.reminder

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

// Lặp lại mỗi 1 ngày
private const val REPEAT_INTERVAL_DAYS = 1L

// Lên lịch và huỷ lịch các lần nhắc hằng ngày
object ReminderScheduler {

    // Lên lịch cả hai lần nhắc. Gọi nhiều lần cũng không bị trùng (enqueueUniquePeriodicWork).
    fun scheduleAll(context: Context) {
        val workManager = WorkManager.getInstance(context)
        ReminderType.entries.forEach { type ->
            val request = PeriodicWorkRequestBuilder<ReminderWorker>(REPEAT_INTERVAL_DAYS, TimeUnit.DAYS)
                .setInitialDelay(millisUntilNext(type), TimeUnit.MILLISECONDS)
                .setInputData(workDataOf(ReminderWorker.KEY_REMINDER_TYPE to type.name))
                .build()
            // UPDATE: nếu đã có lịch thì cập nhật theo giờ mới, không tạo thêm lịch thứ hai
            workManager.enqueueUniquePeriodicWork(type.workName, ExistingPeriodicWorkPolicy.UPDATE, request)
        }
    }

    // Huỷ mọi lần nhắc (khi đăng xuất)
    fun cancelAll(context: Context) {
        val workManager = WorkManager.getInstance(context)
        ReminderType.entries.forEach { type -> workManager.cancelUniqueWork(type.workName) }
    }

    // Số mili giây từ bây giờ đến lần nhắc gần nhất của loại này.
    // Giờ nhắc hôm nay đã qua thì tính sang ngày mai.
    private fun millisUntilNext(type: ReminderType): Long {
        val now = LocalDateTime.now()
        var next = now.toLocalDate().atTime(type.time)
        if (!next.isAfter(now)) next = next.plusDays(1)
        return Duration.between(now, next).toMillis()
    }
}