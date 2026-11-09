package com.nhuanmai.alittlecare.announcement

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

// Android gọi class này khi có tin Cloud Messaging đến lúc app đang mở.
// (Khi app tắt, Android tự hiện thông báo, không đi qua đây.)
class AnnouncementMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        val notification = message.notification ?: return // tin không có phần hiển thị thì bỏ qua
        val title = notification.title ?: return            // không có tiêu đề thì không hiện
        AnnouncementNotifier.show(applicationContext, title, notification.body)
    }
}