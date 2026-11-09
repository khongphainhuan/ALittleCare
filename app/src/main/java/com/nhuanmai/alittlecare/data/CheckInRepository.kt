package com.nhuanmai.alittlecare.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.nhuanmai.alittlecare.model.CheckInSummary
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.ZoneId
import com.google.firebase.firestore.DocumentSnapshot

// Số ngày gần nhất đọc về để tính chuỗi liên tiếp (tối đa 1 năm)
private const val MAX_DAYS_TO_LOAD = 365L

// Tên trường trong Firestore, đặt một chỗ để không gõ sai
private const val FIELD_CHECKED_IN_AT = "checkedInAt"

// Nơi duy nhất trong app đọc/ghi lần xác nhận "Tôi vẫn ổn" trên Firestore.
// Dữ liệu nằm ở: users/{uid}/checkIns/{yyyy-MM-dd} → { checkedInAt: thời điểm xác nhận }
class CheckInRepository {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    // Thư mục checkIns của người đang đăng nhập; báo lỗi rõ ràng nếu chưa đăng nhập
    private fun checkInsOfCurrentUser(): CollectionReference {
        val uid = auth.currentUser?.uid
            ?: throw IllegalStateException("Chưa đăng nhập")
        return firestore.collection("users").document(uid).collection("checkIns")
    }

    // Ghi nhận "Tôi vẫn ổn" cho hôm nay.
    // Tên tài liệu là ngày hôm nay, nên bấm nhiều lần trong ngày chỉ ghi đè, không bị trùng.
    suspend fun checkInToday() {
        val today = LocalDate.now().toString() // ví dụ "2026-10-23"
        checkInsOfCurrentUser()
            .document(today)
            .set(mapOf(FIELD_CHECKED_IN_AT to FieldValue.serverTimestamp()))
            .await()
    }

    // Đọc tình hình check-in: hôm nay đã xác nhận chưa, lúc mấy giờ, chuỗi bao nhiêu ngày.
    suspend fun getSummary(): CheckInSummary {
        val documents = checkInsOfCurrentUser()
            .orderBy(FieldPath.documentId(), Query.Direction.DESCENDING) // ngày mới nhất trước
            .limit(MAX_DAYS_TO_LOAD)
            .get()
            .await()
            .documents

        val today = LocalDate.now()

        // Giờ xác nhận hôm nay (null nếu chưa có)
        val checkedInTodayAt = documents
            .firstOrNull { it.id == today.toString() }
            ?.getTimestamp(FIELD_CHECKED_IN_AT, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
            ?.toDate()
            ?.toInstant()
            ?.atZone(ZoneId.systemDefault())
            ?.toLocalTime()

        // Tập các ngày đã xác nhận; bỏ qua tài liệu có tên không phải ngày hợp lệ
        val checkedInDates = documents
            .mapNotNull { runCatching { LocalDate.parse(it.id) }.getOrNull() }
            .toSet()

        return CheckInSummary(
            checkedInTodayAt = checkedInTodayAt,
            streakDays = countStreak(checkedInDates, today),
        )
    }

    // Đếm số ngày liên tiếp tính lùi từ hôm nay.
    // Hôm nay chưa xác nhận thì tính từ hôm qua: người dùng vẫn còn thời gian đến 0h,
    // chuỗi chưa bị đứt.
    private fun countStreak(dates: Set<LocalDate>, today: LocalDate): Int {
        var day = if (today in dates) today else today.minusDays(1)
        var streak = 0
        while (day in dates) {
            streak++
            day = day.minusDays(1)
        }
        return streak
    }
}