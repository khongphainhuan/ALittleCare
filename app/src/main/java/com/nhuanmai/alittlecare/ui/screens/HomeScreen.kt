package com.nhuanmai.alittlecare.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nhuanmai.alittlecare.R
import com.nhuanmai.alittlecare.ui.components.SecondaryButton
import com.nhuanmai.alittlecare.ui.theme.ALittleCareTheme

// Nối giao diện (HomeScreen) với logic (HomeViewModel)
@Composable
fun HomeRoute(
    onSignedOut: () -> Unit,
    viewModel: HomeViewModel = viewModel(),
) {
    val context = LocalContext.current

    // Đăng xuất xong thì báo cho bên ngoài để quay về màn Đăng nhập
    LaunchedEffect(viewModel.isSignedOut) {
        if (viewModel.isSignedOut) onSignedOut()
    }

    HomeScreen(
        displayName = viewModel.displayName,
        onSignOutClick = { viewModel.signOut(context) },
    )
}

// Màn 09 · Trang chủ (Figma node 2:60). Bản đầu: lời chào và nút Đăng xuất.
@Composable
fun HomeScreen(
    displayName: String,
    onSignOutClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Tài khoản không có tên thì gọi là "bạn"
    val name = displayName.ifBlank { stringResource(R.string.home_default_name) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = stringResource(R.string.home_greeting, name),
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground,
        )

        Spacer(modifier = Modifier.weight(1f))

        SecondaryButton(
            text = stringResource(R.string.home_sign_out),
            onClick = onSignOutClick,
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun HomeScreenPreview() {
    ALittleCareTheme {
        HomeScreen(displayName = "Minh", onSignOutClick = {})
    }
}