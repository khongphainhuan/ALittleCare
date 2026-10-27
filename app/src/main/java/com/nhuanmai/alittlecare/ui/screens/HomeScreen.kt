package com.nhuanmai.alittlecare.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nhuanmai.alittlecare.R
import com.nhuanmai.alittlecare.model.CheckInSummary
import com.nhuanmai.alittlecare.reminder.ReminderNotifier
import com.nhuanmai.alittlecare.reminder.ReminderScheduler
import com.nhuanmai.alittlecare.reminder.ReminderType
import com.nhuanmai.alittlecare.ui.components.SecondaryButton
import com.nhuanmai.alittlecare.ui.components.SkeletonBox
import com.nhuanmai.alittlecare.ui.theme.ALittleCareTheme
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

// Mốc giờ đổi lời chào
private const val MORNING_START_HOUR = 5
private const val AFTERNOON_START_HOUR = 11
private const val EVENING_START_HOUR = 18

// Kích thước theo thiết kế Figma
private val CHECK_IN_OUTER_SIZE = 236.dp
private val CHECK_IN_BUTTON_SIZE = 196.dp
private val STAT_CARD_HEIGHT = 80.dp

private val TIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
private val DATE_FORMATTER: DateTimeFormatter =
    DateTimeFormatter.ofPattern("EEEE, d 'tháng' M", Locale.forLanguageTag("vi")) // "Thứ Sáu, 23 tháng 10"

// Nối giao diện (HomeScreen) với logic (HomeViewModel)
@Composable
fun HomeRoute(
    onSignedOut: () -> Unit,
    viewModel: HomeViewModel = viewModel(),
) {
    val context = LocalContext.current
    val state = viewModel.uiState

    // Hộp thoại xin quyền thông báo (Android 13+). Được hay không vẫn lên lịch:
    // không có quyền thì ReminderNotifier tự bỏ qua, cấp quyền sau vẫn nhắc được.
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { }

    // Vào Trang chủ (đã đăng nhập): lên lịch nhắc, xin quyền nếu chưa có
    LaunchedEffect(Unit) {
        ReminderScheduler.scheduleAll(context)
        if (!ReminderNotifier.hasNotificationPermission(context)) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    // Đăng xuất xong thì báo cho bên ngoài để quay về màn Đăng nhập
    LaunchedEffect(state.isSignedOut) {
        if (state.isSignedOut) onSignedOut()
    }

    HomeScreen(
        displayName = viewModel.displayName,
        state = state,
        onCheckInClick = viewModel::checkIn,
        onRetryClick = viewModel::loadSummary,
        onSignOutClick = { viewModel.signOut(context) },
    )
}

// Màn 09 · Trang chủ (Figma node 2:60)
@Composable
fun HomeScreen(
    displayName: String,
    state: HomeUiState,
    onCheckInClick: () -> Unit,
    onRetryClick: () -> Unit,
    onSignOutClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Tài khoản không có tên thì gọi là "bạn"
    val name = displayName.ifBlank { stringResource(R.string.home_default_name) }
    val today = remember { LocalDate.now() }
    val summary = state.summary

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Lời chào theo buổi + tên
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(greetingRes()),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = name,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground,
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = stringResource(R.string.home_question),
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )
        Text(
            text = today.format(DATE_FORMATTER),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        when {
            // Đang tải: skeleton hình tròn đúng chỗ nút
            state.isLoading -> SkeletonBox(
                modifier = Modifier.size(CHECK_IN_OUTER_SIZE),
                shape = CircleShape,
            )

            // Tải lỗi: báo lỗi và cho thử lại
            summary == null -> {
                MessageText(messageRes = state.errorMessageRes, isError = true)
                TextButton(onClick = onRetryClick) {
                    Text(text = stringResource(R.string.home_retry))
                }
            }

            // Có dữ liệu: nút "Tôi vẫn ổn" và dòng thông tin bên dưới
            else -> {
                val checkedInAt = summary.checkedInTodayAt
                CheckInButton(
                    isCheckedIn = checkedInAt != null,
                    isCheckingIn = state.isCheckingIn,
                    onClick = onCheckInClick,
                )
                Text(
                    text = if (checkedInAt != null) {
                        stringResource(R.string.home_checked_in_info, checkedInAt.format(TIME_FORMATTER))
                    } else {
                        stringResource(R.string.home_reminder_info, ReminderType.MORNING.time.format(TIME_FORMATTER))
                    },
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp, fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
                MessageText(messageRes = state.errorMessageRes, isError = true)
                MessageText(messageRes = state.infoMessageRes, isError = false)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Hai thẻ thống kê
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (state.isLoading) {
                SkeletonBox(
                    modifier = Modifier.weight(1f).height(STAT_CARD_HEIGHT),
                    shape = RoundedCornerShape(16.dp),
                )
                SkeletonBox(
                    modifier = Modifier.weight(1f).height(STAT_CARD_HEIGHT),
                    shape = RoundedCornerShape(16.dp),
                )
            } else if (summary != null) {
                StatCard(
                    value = summary.streakDays.toString(),
                    label = stringResource(R.string.home_streak_label),
                    modifier = Modifier.weight(1f),
                )
                // Tạm thời luôn là 0: tính năng người thân làm ở tuần 7–8
                StatCard(
                    value = "0",
                    label = stringResource(R.string.home_relatives_label),
                    modifier = Modifier.weight(1f),
                )
            }
        }

        // TẠM THỜI: chuyển sang màn Cài đặt khi có màn đó
        SecondaryButton(
            text = stringResource(R.string.home_sign_out),
            onClick = onSignOutClick,
        )
    }
}

// Nút tròn lớn "Tôi vẫn ổn": vòng ngoài xanh nhạt, nút trong xanh đậm có bóng
@Composable
private fun CheckInButton(
    isCheckedIn: Boolean,
    isCheckingIn: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(CHECK_IN_OUTER_SIZE)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .size(CHECK_IN_BUTTON_SIZE)
                .shadow(elevation = 12.dp, shape = CircleShape, spotColor = MaterialTheme.colorScheme.primary)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
                .clickable(
                    enabled = !isCheckedIn && !isCheckingIn, // đã xác nhận hoặc đang gửi thì không bấm được
                    role = Role.Button,
                    onClick = onClick,
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterVertically),
        ) {
            if (isCheckingIn) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Icon(
                    painter = painterResource(R.drawable.ic_check),
                    contentDescription = null, // chữ bên dưới đã mô tả nút
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(56.dp),
                )
                Text(
                    text = stringResource(
                        if (isCheckedIn) R.string.home_checked_in_button else R.string.home_check_in_button,
                    ),
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            }
        }
    }
}

// Thẻ thống kê: số lớn ở trên, nhãn nhỏ ở dưới
@Composable
private fun StatCard(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(16.dp)
    Column(
        modifier = modifier
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface)
            .border(width = 1.dp, color = MaterialTheme.colorScheme.outline, shape = shape)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onPrimaryContainer,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

// Dòng thông báo (lỗi màu đỏ, thông tin màu xám); không có thông báo thì không hiện gì
@Composable
private fun MessageText(
    @StringRes messageRes: Int?,
    isError: Boolean,
) {
    if (messageRes == null) return
    Text(
        text = stringResource(messageRes),
        style = MaterialTheme.typography.bodyMedium,
        color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
    )
}

// Chọn lời chào theo giờ hiện tại
@StringRes
private fun greetingRes(hour: Int = LocalTime.now().hour): Int = when (hour) {
    in MORNING_START_HOUR until AFTERNOON_START_HOUR -> R.string.home_greeting_morning
    in AFTERNOON_START_HOUR until EVENING_START_HOUR -> R.string.home_greeting_afternoon
    else -> R.string.home_greeting_evening
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun HomeScreenPreview() {
    ALittleCareTheme {
        HomeScreen(
            displayName = "Minh",
            state = HomeUiState(
                isLoading = false,
                summary = CheckInSummary(checkedInTodayAt = null, streakDays = 12),
            ),
            onCheckInClick = {},
            onRetryClick = {},
            onSignOutClick = {},
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun HomeScreenLoadingPreview() {
    ALittleCareTheme {
        HomeScreen(
            displayName = "Minh",
            state = HomeUiState(), // isLoading = true → hiện skeleton
            onCheckInClick = {},
            onRetryClick = {},
            onSignOutClick = {},
        )
    }
}