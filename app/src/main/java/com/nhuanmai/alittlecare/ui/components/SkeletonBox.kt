package com.nhuanmai.alittlecare.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.common.util.CollectionUtils.listOf
import com.nhuanmai.alittlecare.ui.theme.ALittleCareTheme

// Thời gian một lần dải sáng chạy từ trái sang phải
private const val SHIMMER_DURATION_MS = 1200

// Khối skeleton: hiện thay cho nội dung khi đang tải dữ liệu.
// Kích thước do bên gọi quyết định qua modifier, ví dụ Modifier.fillMaxWidth().height(20.dp).
@Composable
fun SkeletonBox(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(12.dp),
) {
    // progress chạy mãi từ 0 đến 1, rồi lặp lại từ đầu
    val transition = rememberInfiniteTransition(label = "skeleton")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = SHIMMER_DURATION_MS, easing = LinearEasing),
        ),
        label = "skeletonProgress",
    )

    val baseColor = MaterialTheme.colorScheme.primaryContainer // xanh nhạt #E6EDF4
    val highlightColor = MaterialTheme.colorScheme.surface      // trắng

    Box(
        modifier = modifier
            .clip(shape)
            .drawBehind {
                // Dải sáng rộng bằng khối, đi từ ngoài mép trái (-width) đến ngoài mép phải (+width)
                val width = size.width
                val startX = -width + progress * 2 * width
                drawRect(
                    brush = Brush.linearGradient(
                        colors = listOf(baseColor, highlightColor, baseColor),
                        start = Offset(startX, 0f),
                        end = Offset(startX + width, 0f),
                    ),
                )
            },
    )
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun SkeletonBoxPreview() {
    ALittleCareTheme {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            SkeletonBox(modifier = Modifier.width(120.dp).height(28.dp))
            SkeletonBox(modifier = Modifier.fillMaxWidth().height(16.dp))
            SkeletonBox(modifier = Modifier.fillMaxWidth().height(80.dp), shape = RoundedCornerShape(16.dp))
        }
    }
}