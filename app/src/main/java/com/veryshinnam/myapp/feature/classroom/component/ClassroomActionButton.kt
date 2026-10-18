package com.veryshinnam.myapp.feature.classroom.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.veryshinnam.myapp.R

/**
 * 학급 주요 버튼
 * : 보관함 비어있음 화면(EmptyView)의 주황 그라데이션 버튼과 동일한 모양
 *
 * - isSmall: 카드 안에서 쓰는 작은 버튼
 * - enabled: false면 회색 처리
 */
@Composable
fun ClassroomActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isSmall: Boolean = false,
    textStyle: TextStyle = if (isSmall) MaterialTheme.typography.titleSmall
    else MaterialTheme.typography.titleMedium,
    horizontalPadding: Dp = if (isSmall) 4.dp else 10.dp,
    verticalPadding: Dp = if (isSmall) 0.dp else 5.dp
) {
    val brush = if (enabled) {
        Brush.verticalGradient(
            colors = listOf(
                colorResource(R.color.main_orange),
                colorResource(R.color.dark_orange),
            )
        )
    } else {
        Brush.verticalGradient(colors = listOf(Color.Gray.copy(alpha = 0.5f), Color.Gray.copy(alpha = 0.5f)))
    }

    Button(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = Color.White,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = Color.White
        ),
        shape = CircleShape,
        modifier = modifier
            .background(brush = brush, shape = CircleShape)
            .border(
                width = 2.dp,
                color = if (enabled) colorResource(id = R.color.main_orange) else Color.Gray.copy(alpha = 0.5f),
                shape = CircleShape
            )
    ) {
        Text(
            text = text,
            style = textStyle.copy(fontWeight = Bold),
            modifier = Modifier.padding(horizontal = horizontalPadding, vertical = verticalPadding)
        )
    }
}
