package com.nhuanmai.alittlecare.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nhuanmai.alittlecare.R
import com.nhuanmai.alittlecare.ui.components.PrimaryButton
import com.nhuanmai.alittlecare.ui.theme.ALittleCareTheme

// Nối giao diện (LoginScreen) với logic (LoginViewModel)
@Composable
fun LoginRoute(
    onSignedIn: () -> Unit,
    viewModel: LoginViewModel = viewModel(),
) {
    val context = LocalContext.current
    val state = viewModel.uiState

    // Đăng nhập xong thì báo cho bên ngoài để chuyển màn
    LaunchedEffect(state.isSignedIn) {
        if (state.isSignedIn) onSignedIn()
    }

    LoginScreen(
        isLoading = state.isLoading,
        errorMessage = state.errorMessageRes?.let { stringResource(it) },
        onGoogleSignInClick = { viewModel.signInWithGoogle(context) },
    )
}

// Màn 05 · Đăng nhập (Figma node 1:131).
// Màn hình chỉ hiển thị; việc đăng nhập do bên gọi xử lý qua onGoogleSignInClick.
@Composable
fun LoginScreen(
    isLoading: Boolean,
    errorMessage: String?,
    onGoogleSignInClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding() // chừa chỗ cho thanh trạng thái và thanh điều hướng
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Hai Spacer có weight bằng nhau đẩy khối giữa vào chính giữa phần trống
        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier
                .size(88.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_heart),
                contentDescription = null, // hình trang trí, trình đọc màn hình bỏ qua
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(40.dp),
            )
        }

        Text(
            text = stringResource(R.string.login_title),
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )

        Text(
            text = stringResource(R.string.login_subtitle),
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 15.sp, lineHeight = 21.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.weight(1f))

        // Chỉ hiện khi có lỗi (ví dụ mất mạng, đăng nhập thất bại)
        if (errorMessage != null) {
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
            )
        }

        PrimaryButton(
            text = stringResource(R.string.login_google_button),
            onClick = onGoogleSignInClick,
            isLoading = isLoading,
        )

        Text(
            text = stringResource(R.string.login_terms),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun LoginScreenPreview() {
    ALittleCareTheme {
        LoginScreen(isLoading = false, errorMessage = null, onGoogleSignInClick = {})
    }
}