package com.veryshinnam.myapp.feature.classroom.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.veryshinnam.myapp.R
import com.veryshinnam.myapp.feature.classroom.model.SubmissionData
import com.veryshinnam.myapp.feature.classroom.model.SubmissionStudentData
import com.veryshinnam.myapp.feature.classroom.ui.SubmissionUiState

/**
 * 과제 제출 현황 시트 (선생님)
 * : 과제 카드의 제출 완료/미제출 칸을 누르면 열림
 *   학생별 제출 여부를 미제출 → 제출 완료 순으로 보여주고,
 *   미제출 학생에게 알림 메일을 보낼 수 있음
 *
 * - number: 과제 번호 (과제 목록의 "과제 N")
 * - state: 조회 상태 (로딩/오류/성공)
 * - isSending: 알림 메일 발송 요청 중이면 버튼 비활성
 * - onRemindClick: 알림 메일 보내기 클릭 시 (확인 후 발송은 호출한 화면에서 처리)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubmissionSheet(
    number: Int,
    state: SubmissionUiState,
    isSending: Boolean,
    onRemindClick: () -> Unit,
    onRetry: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = null, // 손잡이 제거
        containerColor = colorResource(R.color.main_orange)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 제목 + 닫기 버튼
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "과제 $number 제출 현황",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Outlined.Cancel,
                        contentDescription = "시트 닫기",
                        tint = Color.White
                    )
                }
            }

            when (state) {
                is SubmissionUiState.Closed -> Unit

                is SubmissionUiState.Loading -> {
                    Box(Modifier.fillMaxWidth().padding(vertical = 32.dp), contentAlignment = Alignment.Center) {
                        ClassroomLoading()
                    }
                }

                is SubmissionUiState.Error -> {
                    Text(
                        text = state.message,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                    )
                    SheetButton(text = "다시 시도", enabled = true, onClick = onRetry)
                }

                is SubmissionUiState.Success -> {
                    SubmissionContent(
                        submission = state.submission,
                        isClosed = state.isClosed,
                        isSending = isSending,
                        onRemindClick = onRemindClick
                    )
                }
            }
        }
    }
}

@Composable
private fun SubmissionContent(
    submission: SubmissionData,
    isClosed: Boolean,
    isSending: Boolean,
    onRemindClick: () -> Unit
) {
    // 미제출 학생을 먼저, 그 안에서는 번호순
    val sorted = submission.students.sortedWith(compareBy({ it.isSubmitted }, { it.no }))
    val hasNotSubmitted = submission.notSubmittedCount > 0

    // 요약
    Text(
        text = "제출 완료 ${submission.submittedCount}명 · 미제출 ${submission.notSubmittedCount}명",
        style = MaterialTheme.typography.titleSmall.copy(color = Color.White, fontWeight = FontWeight.Bold)
    )

    if (sorted.isEmpty()) {
        Text(
            text = "아직 가입이 승인된 학생이 없어요.",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp)
        )
    } else {
        // 학생별 제출 여부
        LazyColumn(
            modifier = Modifier.heightIn(max = 360.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(sorted, key = { it.id }) { student ->
                SubmissionRow(student)
            }
        }
    }

    // 알림 메일 보내기 (마감 전 + 미제출 학생이 있을 때만)
    val canRemind = hasNotSubmitted && !isClosed && !isSending
    Text(
        text = when {
            isClosed -> "마감된 과제에는 알림 메일을 보낼 수 없어요."
            !hasNotSubmitted -> "모든 학생이 제출했어요!"
            else -> "미제출 학생에게만 메일이 가고, 같은 과제에는 24시간에 한 번만 보낼 수 있어요."
        },
        style = MaterialTheme.typography.bodyMedium.copy(color = Color.White)
    )
    SheetButton(
        text = if (isSending) "보내는 중..." else "미제출 학생에게 알림 메일 보내기",
        enabled = canRemind,
        onClick = onRemindClick
    )
}

// 학생 한 줄: 번호 + 이름 + 제출 여부
@Composable
private fun SubmissionRow(student: SubmissionStudentData) {
    val statusText = if (student.isSubmitted) "제출 완료" else "미제출"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .semantics(mergeDescendants = true) {
                contentDescription = "${student.no}번 ${student.name}, $statusText"
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
                .clearAndSetSemantics { }
        )

        // 제출 여부
        ClassroomChip(
            text = statusText,
            color = colorResource(if (student.isSubmitted) R.color.deep_green else R.color.brand_orange),
            modifier = Modifier.clearAndSetSemantics { }
        )
    }
}

// 시트 버튼 (경고창 시트의 확인 버튼과 같은 모양)
@Composable
private fun SheetButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = colorResource(R.color.main_orange_50),
            disabledContainerColor = Color.White.copy(alpha = 0.4f)
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = if (enabled) Color.Black else Color.Black.copy(alpha = 0.4f),
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(4.dp)
        )
    }
}
