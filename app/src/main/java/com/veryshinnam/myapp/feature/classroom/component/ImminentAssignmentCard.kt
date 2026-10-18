package com.veryshinnam.myapp.feature.classroom.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.veryshinnam.myapp.R
import com.veryshinnam.myapp.feature.classroom.model.AssignmentData

/**
 * 학생 학급 화면의 과제 요약 카드
 * : 마감이 가장 임박한 과제(아직 제출 안 했고 마감 전) + 내 제출 현황을 한눈에 보여줌
 *   카드를 누르면 과제 목록 화면으로 이동
 *
 * - assignments: 학급의 전체 과제
 * - onClick: 카드 클릭 시, 과제 목록 화면으로 이동
 */
@Composable
fun ImminentAssignmentCard(
    assignments: List<AssignmentData>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 아직 제출하지 않았고 마감 전인 과제 중 마감이 가장 빠른 것
    val imminent = assignments
        .filter { it.isSubmitted != true && !it.isClosed }
        .minByOrNull { it.dueAt }
    val submitted = assignments.count { it.isSubmitted == true }
    val missed = assignments.count { it.isSubmitted != true && it.isClosed } // 제출 없이 마감된 과제

    val summary = "제출 완료 ${submitted}개 · 전체 ${assignments.size}개" +
            if (missed > 0) " · 마감 놓침 ${missed}개" else ""

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(colorResource(R.color.main_orange))
            .clickable(onClick = onClick)
            .semantics(mergeDescendants = true) {
                contentDescription = "가장 임박한 과제. " + when {
                    assignments.isEmpty() -> "아직 선생님이 내 준 과제가 없어요. "
                    imminent == null -> "지금 해야 할 과제가 없어요. "
                    else -> "${imminent.title}, ${imminent.dDayText()}, ${imminent.dueDate}까지. "
                } + summary + ". 과제 보러 가기"
                role = Role.Button
            }
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = "가장 임박한 과제",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        )

        when {
            assignments.isEmpty() -> {
                Text(
                    text = "아직 선생님이 내 준 과제가 없어요.",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }

            imminent == null -> {
                Text(
                    text = "지금 해야 할 과제가 없어요!",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }

            else -> {
                // 과제명
                Text(
                    text = imminent.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // 남은 일수
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Schedule,
                        contentDescription = null, // 장식용
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = imminent.dDayText(),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        ),
                        modifier = Modifier.padding(start = 6.dp)
                    )
                }

                // 마감 일시
                Text(
                    text = imminent.dueDate,
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.White)
                )
            }
        }

        // 내 제출 현황
        Text(
            text = summary,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.main_orange_50)
            ),
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
