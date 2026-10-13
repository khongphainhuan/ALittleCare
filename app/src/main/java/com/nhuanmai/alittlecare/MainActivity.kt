package com.nhuanmai.alittlecare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.nhuanmai.alittlecare.ui.AppNavigation
import com.nhuanmai.alittlecare.ui.theme.ALittleCareTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ALittleCareTheme {
                AppNavigation()
            }
        }
    }
}