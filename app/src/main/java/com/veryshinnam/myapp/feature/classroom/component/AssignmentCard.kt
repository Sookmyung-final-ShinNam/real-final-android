package com.veryshinnam.myapp.feature.classroom.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.veryshinnam.myapp.R
import com.veryshinnam.myapp.feature.classroom.model.AssignmentData

// 마감까지 남은 일수 표시
internal fun AssignmentData.dDayText(): String = when {
    isClosed -> "마감"
    dDay > 0 -> "${dDay}일 남음"
    else -> "오늘 마감"
}

/**
 * 과제 카드 (주황 채움)
 * : "과제 N" + 제목 + (학생) 설명 + 남은 일수 + 마감 일시
 *   - 선생님: 제출 완료 / 미제출 인원
 *   - 학생: 동화 만들기 버튼 (제출했으면 진행 완료)
 *
 * - number: 과제 번호 (등록 순서)
 * - onStartClick: (학생) 동화 만들기 클릭 시
 */
@Composable
fun AssignmentCard(
    number: Int,
    data: AssignmentData,
    isTeacher: Boolean,
    onStartClick: (Long) -> Unit,
    onSubmissionClick: (Long) -> Unit = {},
    isInProgress: Boolean = false,
    onResumeClick: (Long) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val lightColor = colorResource(R.color.main_orange_50)
    val textColor = colorResource(R.color.main_orange_shade)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(colorResource(R.color.main_orange))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // 과제 번호 + 제목 + 설명 + 마감 (한 덩어리로 읽어 줌)
        Column(
            modifier = Modifier.semantics(mergeDescendants = true) {
                contentDescription = "과제 $number. ${data.title}. " +
                        (data.description?.let { "$it. " } ?: "") +
                        "${data.dDayText()}. ${data.dueDate}까지"
            },
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "과제 $number",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )

            Text(
                text = data.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // 설명 (학생 조회에서만 내려옴)
            if (!data.description.isNullOrBlank()) {
                Text(
                    text = data.description,
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.White),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // 남은 일수
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Schedule,
                    contentDescription = null, // 장식용
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = data.dDayText(),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    ),
                    modifier = Modifier.padding(start = 6.dp)
                )
            }

            // 마감 일시
            Text(
                text = data.dueDate,
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.White)
            )
        }

        if (isTeacher) {
            // 선생님: 제출 완료 / 미제출 인원
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 누르면 학생별 제출 현황을 볼 수 있음
                CountBox(
                    label = "제출 완료",
                    count = data.submittedCount ?: 0,
                    color = lightColor,
                    textColor = textColor,
                    onClick = { onSubmissionClick(data.id) },
                    modifier = Modifier.weight(1f)
                )
                CountBox(
                    label = "미제출",
                    count = data.notSubmittedCount ?: 0,
                    color = lightColor,
                    textColor = textColor,
                    onClick = { onSubmissionClick(data.id) },
                    modifier = Modifier.weight(1f)
                )
            }
        } else {
            // 학생: 진행 완료 / 마감(시작 불가) / 동화 만들기
            val isDone = data.isSubmitted == true
            val isBlocked = !isDone && data.isClosed // 마감 지난 미제출 과제는 시작 불가
            val isEnabled = !isDone && !isBlocked
            val buttonText = when {
                isDone -> "진행 완료"
                isBlocked -> "마감되어 시작할 수 없어요"
                isInProgress -> "이어하기" // 만들던 동화가 있으면 이어서 진행 (학급 도토리 차감 없음)
                else -> "동화 만들기"
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
                    .clip(CircleShape)
                    .background(if (isEnabled) lightColor else lightColor.copy(alpha = 0.7f))
                    .then(
                        if (isEnabled) {
                            Modifier.clickable {
                                if (isInProgress) onResumeClick(data.id) else onStartClick(data.id)
                            }
                        } else Modifier
                    )
                    .semantics {
                        contentDescription = "${data.title} $buttonText"
                        role = Role.Button
                        if (!isEnabled) disabled()
                    }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = buttonText,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = textColor,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.clearAndSetSemantics { }
                )
            }
        }
    }
}

// 선생님 카드의 제출 현황 칸
@Composable
private fun CountBox(
    label: String,
    count: Int,
    color: Color,
    textColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp)
            .semantics(mergeDescendants = true) {
                contentDescription = "$label ${count}명. 학생별 제출 현황 보기"
                role = Role.Button
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = textColor)
        )
        Text(
            text = "${count}명",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = textColor)
        )
    }
}
