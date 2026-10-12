package com.nhuanmai.alittlecare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.nhuanmai.alittlecare.ui.screens.LoginScreen
import com.nhuanmai.alittlecare.ui.theme.ALittleCareTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ALittleCareTheme {
                // Phần B sẽ thay bằng trạng thái thật từ LoginViewModel
                LoginScreen(
                    isLoading = false,
                    errorMessage = null,
                    onGoogleSignInClick = {},
                )
            }
        }
    }
}