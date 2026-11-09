package com.nhuanmai.alittlecare.announcement

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging
import com.nhuanmai.alittlecare.MainActivity
import com.nhuanmai.alittlecare.R
import com.nhuanmai.alittlecare.reminder.ReminderNotifier

private const val TAG = "AnnouncementNotifier"

// Phải trùng với giá trị default_notification_channel_id trong AndroidManifest.xml
private const val CHANNEL_ID = "announcements"

// Chủ đề mọi máy cài app đều đăng ký; gửi từ Firebase Console tới chủ đề này là ai cũng nhận
private const val TOPIC_ANNOUNCEMENTS = "announcements"

// Tin mới thay thế tin cũ trên thanh thông báo, không chồng lên nhau
private const val ANNOUNCEMENT_NOTIFICATION_ID = 100

// Thông báo chung gửi bằng tay từ Firebase Console (Cloud Messaging)
object AnnouncementNotifier {

    // Gọi khi vào Trang chủ: tạo kênh, đăng ký chủ đề. Gọi nhiều lần cũng không sao.
    fun setUp(context: Context) {
        createChannel(context)
        Firebase.messaging.subscribeToTopic(TOPIC_ANNOUNCEMENTS)
            .addOnFailureListener { e -> Log.w(TAG, "Chưa đăng ký được chủ đề thông báo", e) }

        // In mã máy (token) ra Logcat để gửi thử đúng máy này từ Firebase Console
        Firebase.messaging.token
            .addOnSuccessListener { token -> Log.d(TAG, "FCM token: $token") }
    }

    // Hiện tin khi app đang mở (khi app tắt, Android tự hiện theo cấu hình trong Manifest)
    fun show(context: Context, title: String, body: String?) {
        if (!ReminderNotifier.hasNotificationPermission(context)) return

        createChannel(context)

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            ANNOUNCEMENT_NOTIFICATION_ID,
            openAppIntent,
            PendingIntent.FLAG_IMMUTABLE,
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_heart)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body)) // tin dài thì hiện đủ khi mở rộng
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(ANNOUNCEMENT_NOTIFICATION_ID, notification)
    }

    // Kênh riêng, mức mặc định (không bật popup): tin chung ít quan trọng hơn lời nhắc xác nhận,
    // và người dùng tắt được kênh này mà vẫn giữ lời nhắc.
    private fun createChannel(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            context.getString(R.string.announcement_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply {
            description = context.getString(R.string.announcement_channel_description)
        }
        context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }
}