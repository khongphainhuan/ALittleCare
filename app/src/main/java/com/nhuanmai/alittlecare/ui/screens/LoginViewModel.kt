package com.nhuanmai.alittlecare.ui.screens

import android.content.Context
import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.NoCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseNetworkException
import com.nhuanmai.alittlecare.R
import com.nhuanmai.alittlecare.data.AuthRepository
import kotlinx.coroutines.launch

private const val TAG = "LoginViewModel"

// Toàn bộ trạng thái màn Đăng nhập gói trong một chỗ
data class LoginUiState(
    val isLoading: Boolean = false,
    @StringRes val errorMessageRes: Int? = null,
    val isSignedIn: Boolean = false,
)

class LoginViewModel : ViewModel() {

    private val authRepository = AuthRepository()

    // Màn hình đọc biến này; chỉ ViewModel được sửa (private set)
    var uiState by mutableStateOf(
        LoginUiState(isSignedIn = authRepository.currentUser != null)
    )
        private set

    fun signInWithGoogle(context: Context) {
        if (uiState.isLoading) return // đang xử lý thì bỏ qua lần bấm thêm

        uiState = LoginUiState(isLoading = true)
        viewModelScope.launch {
            uiState = try {
                authRepository.signInWithGoogle(context)
                LoginUiState(isSignedIn = true)
            } catch (e: GetCredentialCancellationException) {
                LoginUiState() // người dùng tự đóng hộp chọn tài khoản: không coi là lỗi
            } catch (e: NoCredentialException) {
                LoginUiState(errorMessageRes = R.string.login_error_no_account)
            } catch (e: FirebaseNetworkException) {
                LoginUiState(errorMessageRes = R.string.login_error_network)
            } catch (e: Exception) {
                Log.e(TAG, "Đăng nhập Google thất bại", e)
                LoginUiState(errorMessageRes = R.string.login_error_generic)
            }
        }
    }
}