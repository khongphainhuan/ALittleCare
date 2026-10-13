package com.nhuanmai.alittlecare.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nhuanmai.alittlecare.data.AuthRepository
import com.nhuanmai.alittlecare.ui.screens.HomeRoute
import com.nhuanmai.alittlecare.ui.screens.LoginRoute

// Tên các màn hình trong app
object Routes {
    const val LOGIN = "login"
    const val HOME = "home"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // Chọn màn mở đầu một lần duy nhất: đã đăng nhập thì vào thẳng Trang chủ
    val startDestination = remember {
        if (AuthRepository().currentUser != null) Routes.HOME else Routes.LOGIN
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Routes.LOGIN) {
            LoginRoute(
                onSignedIn = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true } // bỏ màn Đăng nhập khỏi chồng
                    }
                },
            )
        }
        composable(Routes.HOME) {
            HomeRoute(
                onSignedOut = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.HOME) { inclusive = true } // bỏ Trang chủ khỏi chồng
                    }
                },
            )
        }
    }
}