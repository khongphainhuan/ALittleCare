package com.nhuanmai.alittlecare.reminder

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.nhuanmai.alittlecare.data.AuthRepository
import com.nhuanmai.alittlecare.data.CheckInRepository

private const val TAG = "ReminderWorker"

// Công việc chạy nền đúng giờ nhắc: nếu hôm nay chưa xác nhận thì hiện thông báo.
// WorkManager tự chạy công việc này kể cả khi app đã tắt hoặc điện thoại vừa khởi động lại.
class ReminderWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val typeName = inputData.getString(KEY_REMINDER_TYPE)
        val type = ReminderType.entries.firstOrNull { it.name == typeName }
            ?: return Result.failure() // dữ liệu lên lịch sai: không làm gì

        // Đã đăng xuất thì không nhắc
        if (AuthRepository().currentUser == null) return Result.success()

        val isCheckedInToday = try {
            CheckInRepository().getSummary().checkedInTodayAt != null
        } catch (e: Exception) {
            // Không đọc được dữ liệu (ví dụ mất mạng): vẫn nhắc cho chắc
            Log.w(TAG, "Không kiểm tra được trạng thái hôm nay", e)
            false
        }

        if (!isCheckedInToday) {
            ReminderNotifier.show(applicationContext, type)
        }
        return Result.success()
    }

    companion object {
        const val KEY_REMINDER_TYPE = "reminder_type"
    }
}