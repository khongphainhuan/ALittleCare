package com.nhuanmai.alittlecare.ui.screens

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhuanmai.alittlecare.data.AuthRepository
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val authRepository = AuthRepository()

    // Tên hiển thị lấy từ tài khoản Google; có thể rỗng nếu tài khoản không đặt tên
    val displayName: String = authRepository.currentUser?.displayName.orEmpty()

    var isSignedOut by mutableStateOf(false)
        private set

    fun signOut(context: Context) {
        viewModelScope.launch {
            authRepository.signOut(context)
            isSignedOut = true
        }
    }
}