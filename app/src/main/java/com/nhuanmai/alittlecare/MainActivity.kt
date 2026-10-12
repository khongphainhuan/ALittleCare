package com.nhuanmai.alittlecare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nhuanmai.alittlecare.data.AuthRepository
import com.nhuanmai.alittlecare.ui.screens.LoginRoute
import com.nhuanmai.alittlecare.ui.theme.ALittleCareTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val authRepository = AuthRepository()

        setContent {
            ALittleCareTheme {
                var isSignedIn by rememberSaveable {
                    mutableStateOf(authRepository.currentUser != null)
                }

                if (isSignedIn) {
                    // TẠM THỜI: Phần C sẽ thay bằng điều hướng sang Trang chủ
                    Text(
                        text = "Đã đăng nhập: ${authRepository.currentUser?.email}",
                        modifier = Modifier.safeDrawingPadding().padding(24.dp),
                    )
                } else {
                    LoginRoute(onSignedIn = { isSignedIn = true })
                }
            }
        }
    }
}