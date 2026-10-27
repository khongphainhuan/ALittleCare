package com.nhuanmai.alittlecare.ui.screens

import android.content.Context
import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhuanmai.alittlecare.R
import com.nhuanmai.alittlecare.data.AuthRepository
import com.nhuanmai.alittlecare.data.CheckInRepository
import com.nhuanmai.alittlecare.model.CheckInSummary
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import com.nhuanmai.alittlecare.reminder.ReminderScheduler

private const val TAG = "HomeViewModel"

// Chờ máy chủ xác nhận tối đa 10 giây; quá thời gian coi như đang mất mạng
private const val SYNC_TIMEOUT_MS = 10_000L

// Toàn bộ trạng thái màn Trang chủ gói trong một chỗ
data class HomeUiState(
    val isLoading: Boolean = true,            // đang tải dữ liệu lần đầu → hiện skeleton
    val summary: CheckInSummary? = null,      // null khi chưa tải xong hoặc tải lỗi
    val isCheckingIn: Boolean = false,        // đang gửi "Tôi vẫn ổn"
    @StringRes val errorMessageRes: Int? = null,
    @StringRes val infoMessageRes: Int? = null,
    val isSignedOut: Boolean = false,
)

class HomeViewModel : ViewModel() {

    private val authRepository = AuthRepository()
    private val checkInRepository = CheckInRepository()

    // Tên hiển thị lấy từ tài khoản Google; có thể rỗng nếu tài khoản không đặt tên
    val displayName: String = authRepository.currentUser?.displayName ?: ""
    // Màn hình đọc biến này; chỉ ViewModel được sửa (private set)
    var uiState by mutableStateOf(HomeUiState())
        private set

    init {
        loadSummary()
    }

    // Tải tình hình check-in (gọi khi mở màn và khi bấm "Thử lại")
    fun loadSummary() {
        uiState = uiState.copy(isLoading = true, errorMessageRes = null, infoMessageRes = null)
        viewModelScope.launch {
            uiState = try {
                uiState.copy(isLoading = false, summary = checkInRepository.getSummary())
            } catch (e: Exception) {
                Log.e(TAG, "Không tải được dữ liệu check-in", e)
                uiState.copy(isLoading = false, errorMessageRes = R.string.home_error_load)
            }
        }
    }

    // Bấm "Tôi vẫn ổn"
    fun checkIn() {
        val alreadyCheckedIn = uiState.summary?.checkedInTodayAt != null
        if (uiState.isCheckingIn || alreadyCheckedIn) return // đang gửi hoặc đã xác nhận hôm nay

        uiState = uiState.copy(isCheckingIn = true, errorMessageRes = null, infoMessageRes = null)
        viewModelScope.launch {
            uiState = try {
                // Mất mạng thì Firestore vẫn lưu trên máy và tự gửi khi có mạng lại;
                // mình chỉ chờ tối đa SYNC_TIMEOUT_MS rồi báo cho người dùng biết.
                val isSynced = withTimeoutOrNull(SYNC_TIMEOUT_MS) {
                    checkInRepository.checkInToday()
                } != null

                uiState.copy(
                    isCheckingIn = false,
                    summary = checkInRepository.getSummary(),
                    infoMessageRes = if (isSynced) null else R.string.home_check_in_pending,
                )
            } catch (e: Exception) {
                Log.e(TAG, "Không ghi nhận được lần xác nhận", e)
                uiState.copy(isCheckingIn = false, errorMessageRes = R.string.home_error_check_in)
            }
        }
    }

    // TẠM THỜI ở Trang chủ: chuyển sang màn Cài đặt khi có màn đó
    fun signOut(context: Context) {
        viewModelScope.launch {
            ReminderScheduler.cancelAll(context) // đăng xuất thì không nhắc nữa
            authRepository.signOut(context)
            uiState = uiState.copy(isSignedOut = true)
        }
    }
}