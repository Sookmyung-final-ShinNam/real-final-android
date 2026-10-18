package com.veryshinnam.myapp.feature.classroom.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.veryshinnam.myapp.R
import com.veryshinnam.myapp.feature.classroom.model.AssignmentData

// 마감까지 이 일수 이하로 남으면 긴급(빨강)으로 표시
private const val URGENT_DAYS = 3

/**
 * 학생 학급 화면의 임박한 과제 안내
 * : 버튼/카드가 아닌 글씨로만 표시. 마감이 가까울수록 눈에 띄도록 강조(빨강)
 *   - 임박한 과제 = 아직 제출하지 않았고 마감 전인 과제 중 마감이 가장 빠른 것
 *   - 전체 과제와 제출 현황은 "과제 전체 보기" 링크로 이동한 과제 화면에서 확인
 *
 * - assignments: 학급의 전체 과제
 * - onShowAllClick: "과제 전체 보기" 클릭 시, 과제 목록 화면으로 이동
 */
@Composable
fun ImminentAssignmentNotice(
    assignments: List<AssignmentData>,
    onShowAllClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val imminent = assignments
        .filter { it.isSubmitted != true && !it.isClosed }
        .minByOrNull { it.dueAt }

    val isUrgent = imminent != null && imminent.dDay <= URGENT_DAYS
    val accent = colorResource(if (isUrgent) R.color.palette_red else R.color.brand_orange)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (imminent != null) {
            // 한 덩어리로 읽어 줌
            Column(
                modifier = Modifier.semantics(mergeDescendants = true) {
                    contentDescription = (if (isUrgent) "마감 임박. " else "가장 가까운 마감. ") +
                            "${imminent.title}, ${imminent.dDayText()}, ${imminent.dueDate}까지"
                },
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // 라벨 (긴급하면 아이콘 + 마감 임박!)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isUrgent) {
                        Icon(
                            imageVector = Icons.Filled.NotificationsActive,
                            contentDescription = null, // 장식용
                            tint = accent,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Text(
                        text = if (isUrgent) "마감 임박!" else "가장 가까운 마감",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = accent
                        ),
                        modifier = Modifier.padding(start = if (isUrgent) 4.dp else 0.dp)
                    )
                }

                // 과제명
                Text(
                    text = imminent.title,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // 남은 일수 + 마감 일시
                Text(
                    text = "${imminent.dDayText()} · ${imminent.dueDate}",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = accent
                    )
                )
            }
        } else {
            Text(
                text = if (assignments.isEmpty()) "아직 선생님이 내 준 과제가 없어요."
                else "지금 해야 할 과제가 없어요!",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
        }

        // 과제 전체 보기 (글씨 링크)
        Text(
            text = "과제 전체 보기 ›",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.main_orange_shade),
                textDecoration = TextDecoration.Underline
            ),
            modifier = Modifier
                .clickable(onClick = onShowAllClick)
                .padding(vertical = 4.dp)
                .semantics {
                    contentDescription = "과제 전체 보기"
                    role = Role.Button
                }
        )
    }
}
