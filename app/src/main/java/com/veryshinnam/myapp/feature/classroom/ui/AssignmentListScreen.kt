package com.veryshinnam.myapp.feature.classroom.ui

import android.content.pm.ActivityInfo
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.veryshinnam.myapp.common.component.InstructionText
import com.veryshinnam.myapp.common.component.LoadErrorView
import com.veryshinnam.myapp.common.component.WarningConfirmSheet
import com.veryshinnam.myapp.common.component.WarningSheet
import com.veryshinnam.myapp.core.orientation.OrientationManager
import com.veryshinnam.myapp.feature.classroom.component.AssignmentCard
import com.veryshinnam.myapp.feature.classroom.component.AssignmentFilter
import com.veryshinnam.myapp.feature.classroom.component.AssignmentFilterChips
import com.veryshinnam.myapp.feature.classroom.component.ClassroomAddCard
import com.veryshinnam.myapp.feature.classroom.component.ClassroomHeader
import com.veryshinnam.myapp.feature.classroom.component.ClassroomLoading
import com.veryshinnam.myapp.feature.classroom.component.ClassroomScaffold
import com.veryshinnam.myapp.feature.classroom.component.SubmissionSheet
import com.veryshinnam.myapp.feature.creation.model.ResumeRequest

/**
 * 학급 과제 화면
 *   - 선생님: 과제 카드(제출 완료/미제출 인원) + 새로운 과제 만들기
 *   - 학생: 과제 카드 + 동화 만들기 (제출했으면 진행 완료)
 *
 * - classroomId: 학급 아이디
 * - isTeacher: 선생님 여부
 * - onBack: 뒤로 가기 버튼 클릭 시
 * - onLogoClick: 로고 클릭 시, 홈 화면으로 복구
 * - onCreateAssignment: 새로운 과제 만들기 클릭 시, 과제 만들기 화면으로 이동
 * - onStartAssignment: 동화 만들기 확인 시, 동화 생성 플로우로 이동 (과제 아이디 전달)
 */
@Composable
fun AssignmentListScreen(
    classroomId: Long,
    isTeacher: Boolean,
    onBack: () -> Unit,
    onLogoClick: () -> Unit,
    onCreateAssignment: () -> Unit,
    onStartAssignment: (assignmentId: Long) -> Unit,
    onResumeAssignment: (ResumeRequest) -> Unit,
    spacerPadding: Dp = 10.dp,
    horizontalPadding: Dp = 16.dp,
    vm: AssignmentListViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    // 상태 구독
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val submissionState by vm.submissionState.collectAsStateWithLifecycle()
    val isSending by vm.isSending.collectAsStateWithLifecycle()
    val warning by vm.warning.collectAsStateWithLifecycle()

    var startAssignmentId by remember { mutableStateOf<Long?>(null) } // 동화 만들기 확인창
    var filter by rememberSaveable { mutableStateOf(AssignmentFilter.ALL) } // 과제 상태 필터
    var submissionNumber by remember { mutableStateOf(0) }            // 제출 현황을 보는 과제 번호
    var isRemindConfirm by remember { mutableStateOf(false) }         // 알림 메일 발송 확인창

    // 세로 모드 고정
    SideEffect {
        OrientationManager.setOrientation?.invoke(
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        )
    }

    // 화면 진입 시 조회 (동화 생성/과제 등록 후 돌아올 때도 제출 여부 갱신)
    LaunchedEffect(classroomId) { vm.load(classroomId, isTeacher) }

    // 토스트 이벤트
    LaunchedEffect(Unit) {
        vm.toast.collect { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
    }

    ClassroomScaffold(
        onBack = onBack,
        onLogoClick = onLogoClick
    ) {
        when (val state = uiState) {
            // 조회 로딩
            is AssignmentListUiState.Loading -> ClassroomLoading()

            // 조회 오류
            is AssignmentListUiState.Error -> {
                LoadErrorView(
                    message = state.message,
                    onRetry = { vm.retry() }
                )
            }

            // 조회 성공
            is AssignmentListUiState.Success -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = horizontalPadding)
                ) {
                    ClassroomHeader(message = "${state.classroomName}\n과제 페이지입니다.")

                    Spacer(Modifier.height(spacerPadding * 1.5f))

                    if (state.assignments.isEmpty() && !isTeacher) {
                        // 학생: 아직 과제 없음
                        Box(Modifier.weight(1f).fillMaxSize(), contentAlignment = Alignment.Center) {
                            InstructionText(
                                text = "아직 선생님이 내 준 과제가 없어요!",
                                textStyle = MaterialTheme.typography.titleSmall
                            )
                        }
                    } else {
                        // 과제 상태 필터 (전체 / 진행중 / 마감) - 보관함 화면의 필터와 같은 모양
                        if (state.assignments.isNotEmpty()) {
                            AssignmentFilterChips(
                                selected = filter,
                                onSelect = { filter = it }
                            )
                            Spacer(Modifier.height(spacerPadding))
                        }

                        // 과제 번호("과제 N")는 필터와 상관없이 전체 목록 기준 순서를 유지
                        val filtered = state.assignments
                            .mapIndexed { index, assignment -> (index + 1) to assignment }
                            .filter { (_, assignment) ->
                                when (filter) {
                                    AssignmentFilter.ALL -> true
                                    AssignmentFilter.ONGOING -> !assignment.isClosed
                                    AssignmentFilter.CLOSED -> assignment.isClosed
                                }
                            }

                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(spacerPadding * 1.5f)
                        ) {
                            // 필터에 해당하는 과제가 없을 때
                            if (filtered.isEmpty() && state.assignments.isNotEmpty()) {
                                item {
                                    InstructionText(
                                        text = when (filter) {
                                            AssignmentFilter.ONGOING -> "진행 중인 과제가 없어요!"
                                            AssignmentFilter.CLOSED -> "마감된 과제가 없어요!"
                                            AssignmentFilter.ALL -> "과제가 없어요!"
                                        },
                                        textStyle = MaterialTheme.typography.titleSmall,
                                        modifier = Modifier.fillMaxWidth().padding(vertical = spacerPadding * 2)
                                    )
                                }
                            }

                            items(filtered, key = { it.second.id }) { (number, assignment) ->
                                AssignmentCard(
                                    number = number,
                                    data = assignment,
                                    isTeacher = isTeacher,
                                    onStartClick = { startAssignmentId = it },
                                    onSubmissionClick = {
                                        submissionNumber = number
                                        vm.openSubmissions(it)
                                    },
                                    isInProgress = state.resumables.containsKey(assignment.id),
                                    onResumeClick = { id -> state.resumables[id]?.let(onResumeAssignment) }
                                )
                            }

                            // 선생님: 새로운 과제 만들기
                            if (isTeacher) {
                                item {
                                    ClassroomAddCard(
                                        title = "새로운 과제 만들기",
                                        onClick = onCreateAssignment
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // 제출 현황 (선생님)
    if (submissionState !is SubmissionUiState.Closed) {
        SubmissionSheet(
            number = submissionNumber,
            state = submissionState,
            isSending = isSending,
            onRemindClick = { isRemindConfirm = true },
            onRetry = {
                // 현재 보고 있는 과제를 다시 조회
                (uiState as? AssignmentListUiState.Success)
                    ?.assignments?.getOrNull(submissionNumber - 1)
                    ?.let { vm.openSubmissions(it.id) }
            },
            onDismiss = { vm.closeSubmissions() }
        )
    }

    // 알림 메일 발송 확인 (선생님)
    if (isRemindConfirm) {
        WarningConfirmSheet(
            warningText = "아직 제출하지 않은 학생들에게\n알림 메일을 보낼까요?\n(같은 과제에는 24시간에 한 번만 보낼 수 있어요)",
            confirmText = "알림 메일 보내기",
            onDismiss = { isRemindConfirm = false },
            onConfirm = { vm.sendReminder() }
        )
    }

    // 경고창 (서버 오류 문구 등)
    warning?.let { text ->
        WarningSheet(
            warningText = text,
            onDismiss = { vm.hideWarning() }
        )
    }

    // 동화 만들기 확인 (학생)
    startAssignmentId?.let { assignmentId ->
        WarningConfirmSheet(
            warningText = "과제 동화를 만들면 학급 도토리가 1개 사용돼요.\n지금 동화를 만들러 갈까요?",
            confirmText = "동화 만들기",
            onDismiss = { startAssignmentId = null },
            onConfirm = { onStartAssignment(assignmentId) }
        )
    }
}
