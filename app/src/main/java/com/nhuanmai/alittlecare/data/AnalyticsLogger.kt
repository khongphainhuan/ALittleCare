package com.nhuanmai.alittlecare.data

import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.analytics.logEvent

// Tên sự kiện và tham số riêng của app (chữ thường, dấu gạch dưới, tối đa 40 ký tự)
private const val EVENT_CHECK_IN = "check_in"
private const val EVENT_REMINDER_SHOWN = "reminder_shown"
private const val PARAM_STREAK_DAYS = "streak_days"
private const val PARAM_REMINDER_TYPE = "reminder_type"
private const val LOGIN_METHOD_GOOGLE = "google"

// Nơi duy nhất trong app ghi sự kiện lên Firebase Analytics.
// Analytics tự lưu tạm khi mất mạng và gửi sau, nên các hàm này không bao giờ làm app chờ.
object AnalyticsLogger {

    fun logLogin() {
        Firebase.analytics.logEvent(FirebaseAnalytics.Event.LOGIN) {
            param(FirebaseAnalytics.Param.METHOD, LOGIN_METHOD_GOOGLE)
        }
    }

    fun logCheckIn(streakDays: Int) {
        Firebase.analytics.logEvent(EVENT_CHECK_IN) {
            param(PARAM_STREAK_DAYS, streakDays.toLong())
        }
    }

    fun logReminderShown(reminderType: String) {
        Firebase.analytics.logEvent(EVENT_REMINDER_SHOWN) {
            param(PARAM_REMINDER_TYPE, reminderType)
        }
    }
}