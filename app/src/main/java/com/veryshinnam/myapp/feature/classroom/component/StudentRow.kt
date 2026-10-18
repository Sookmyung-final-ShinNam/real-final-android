package com.veryshinnam.myapp.feature.classroom.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.veryshinnam.myapp.R
import com.veryshinnam.myapp.feature.classroom.model.StudentData

/**
 * 학급 학생 한 줄
 * : 번호 + 이름 + (선생님) 승인 대기 학생이면 승인 버튼
 *
 * - showApprove: 선생님 화면에서만 true
 * - onApproveClick: 승인 버튼 클릭 시, 가입 승인
 */
@Composable
fun StudentRow(
    student: StudentData,
    showApprove: Boolean,
    onApproveClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(16.dp)
    val isWaiting = !student.isApproved
    val hasApproveButton = isWaiting && showApprove // 버튼이 있으면 행을 묶지 않고 버튼을 따로 읽게 함
    val rowDescription = "${student.no}번 ${student.name}" + if (isWaiting) ", 승인 대기 중" else ""

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White, shape)
            .border(
                width = 2.dp,
                color = colorResource(if (isWaiting) R.color.main_orange_50 else R.color.main_orange),
                shape = shape
            )
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .semantics(mergeDescendants = !hasApproveButton) {
                if (!hasApproveButton) contentDescription = rowDescription
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 번호
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(colorResource(R.color.lemon_yellow), CircleShape)
                .border(2.dp, colorResource(R.color.main_orange), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${student.no}",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.clearAndSetSemantics { }
            )
        }

        // 이름
        Text(
            text = student.name,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            maxLines = 1,
            modifier = Modifier
                .weight(1f)
                .clearAndSetSemantics {
                    // 승인 버튼이 따로 읽히는 경우, 이름 쪽에서 번호와 상태를 함께 읽어 줌
                    if (hasApproveButton) contentDescription = rowDescription
                }
        )

        // 승인 대기 학생: 선생님은 승인 버튼, 그 외는 상태 칩
        if (isWaiting) {
            if (showApprove) {
                ClassroomActionButton(
                    text = "승인",
                    onClick = { onApproveClick(student.id) },
                    isSmall = true,
                    modifier = Modifier.semantics { contentDescription = "${student.name} 가입 승인" }
                )
            } else {
                ClassroomChip(text = "승인 대기", color = colorResource(R.color.main_orange_50))
            }
        }
    }
}
