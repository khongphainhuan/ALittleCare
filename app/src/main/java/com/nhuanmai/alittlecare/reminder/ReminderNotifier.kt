package com.nhuanmai.alittlecare.reminder

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.nhuanmai.alittlecare.MainActivity
import com.nhuanmai.alittlecare.R
import com.nhuanmai.alittlecare.data.AnalyticsLogger

// Kênh thông báo của app (người dùng thấy tên kênh này trong Cài đặt điện thoại)
private const val CHANNEL_ID = "daily_reminder"

// Hiện thông báo nhắc xác nhận
object ReminderNotifier {

    fun show(context: Context, type: ReminderType) {
        // Android 13 trở lên: chưa được cấp quyền thông báo thì không hiện gì
        if (!hasNotificationPermission(context)) return

        createChannel(context)

        // Chạm vào thông báo thì mở app ở Trang chủ
        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            type.notificationId,
            openAppIntent,
            PendingIntent.FLAG_IMMUTABLE,
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_heart)
            .setContentTitle(context.getString(type.titleRes))
            .setContentText(context.getString(type.textRes))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true) // chạm vào thì thông báo tự biến mất
            .build()

        NotificationManagerCompat.from(context).notify(type.notificationId, notification)
        AnalyticsLogger.logReminderShown(type.name.lowercase()) // "morning" hoặc "evening"
    }

    fun hasNotificationPermission(context: Context): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED

    // Tạo kênh thông báo; gọi nhiều lần cũng không sao, Android chỉ tạo một lần
    private fun createChannel(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            context.getString(R.string.reminder_channel_name),
            NotificationManager.IMPORTANCE_HIGH, // hiện popup trượt xuống từ đỉnh màn hình
        ).apply {
            description = context.getString(R.string.reminder_channel_description)
        }
        context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }
}