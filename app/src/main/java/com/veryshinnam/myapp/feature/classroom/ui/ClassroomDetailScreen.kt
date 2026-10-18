package com.veryshinnam.myapp.feature.classroom.ui

import android.content.pm.ActivityInfo
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.veryshinnam.myapp.R
import com.veryshinnam.myapp.common.component.LoadErrorView
import com.veryshinnam.myapp.common.component.WarningSheet
import com.veryshinnam.myapp.core.orientation.OrientationManager
import com.veryshinnam.myapp.feature.classroom.component.ClassroomHeader
import com.veryshinnam.myapp.feature.classroom.component.ClassroomLoading
import com.veryshinnam.myapp.feature.classroom.component.ClassroomScaffold
import com.veryshinnam.myapp.feature.classroom.component.ClassroomSquareButton
import com.veryshinnam.myapp.feature.classroom.component.StudentRow

/**
 * 학급 화면 (메뉴 허브)
 *   - 선생님: 학급 도토리 + 학생 목록(가입 승인) + [동화 모두 보기 / 학급 도토리 충전 / 과제 관리]
 *   - 학생: [과제 확인 / 다람쥐 / 내 동화 보기 / 동화 모두 보기]
 *
 * - classroomId: 학급 아이디
 * - isTeacher: 선생님 여부 (목록 화면에서 전달)
 * - onBack: 뒤로 가기 버튼 클릭 시
 * - onLogoClick: 로고 클릭 시, 홈 화면으로 복구
 * - onAssignmentsClick: 과제 관리(선생님) / 과제 확인(학생) 클릭 시, 과제 목록 화면으로 이동
 * - onStoriesClick: 동화 모두 보기 클릭 시, 학급 동화 화면으로 이동
 * - onMyStoriesClick: (학생) 내 동화 보기 클릭 시, 보관함 화면으로 이동
 */
@Composable
fun ClassroomDetailScreen(
    classroomId: Long,
    isTeacher: Boolean,
    onBack: () -> Unit,
    onLogoClick: () -> Unit,
    onAssignmentsClick: () -> Unit,
    onStoriesClick: () -> Unit,
    onMyStoriesClick: () -> Unit,
    spacerPadding: Dp = 10.dp,
    horizontalPadding: Dp = 16.dp,
    vm: ClassroomDetailViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    // 상태 구독
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val warning by vm.warning.collectAsStateWithLifecycle()

    // 세로 모드 고정
    SideEffect {
        OrientationManager.setOrientation?.invoke(
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        )
    }

    // 화면 진입 시 조회 (과제 화면 등에서 돌아올 때도 갱신)
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
            is ClassroomDetailUiState.Loading -> ClassroomLoading()

            // 조회 오류
            is ClassroomDetailUiState.Error -> {
                LoadErrorView(
                    message = state.message,
                    onRetry = { vm.retry() }
                )
            }

            // 조회 성공
            is ClassroomDetailUiState.Success -> {
                val detail = state.detail

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = horizontalPadding)
                ) {
                    // 상단: 학급 이름 (+ 선생님은 학급 도토리 수)
                    ClassroomHeader(
                        message = "${detail.name} 학급입니다.",
                        acornCount = if (state.isTeacher) detail.points else null
                    )

                    Spacer(Modifier.height(spacerPadding * 1.5f))

                    if (state.isTeacher) {
                        // --- 선생님 ---
                        // 학생 목록 (가입 요청 학생은 승인 버튼)
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(colorResource(R.color.main_orange))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "학생 ${detail.approvedCount}명" +
                                        if (detail.pendingCount > 0) " · 승인 대기 ${detail.pendingCount}명" else "",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                ),
                                modifier = Modifier.padding(bottom = spacerPadding)
                            )

                            if (detail.students.isEmpty()) {
                                Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "아직 가입한 학생이 없어요.\n학급 코드를 학생들에게 알려 주세요!",
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            textAlign = TextAlign.Center
                                        )
                                    )
                                }
                            } else {
                                LazyColumn(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(spacerPadding / 2)
                                ) {
                                    items(detail.students, key = { it.id }) { student ->
                                        StudentRow(
                                            student = student,
                                            showApprove = true,
                                            onApproveClick = { vm.approveStudent(it) }
                                        )
                                    }
                                }
                            }
                        }

                        // 메뉴 버튼
                        Spacer(Modifier.height(spacerPadding * 1.5f))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = spacerPadding * 1.5f),
                            horizontalArrangement = Arrangement.spacedBy(spacerPadding)
                        ) {
                            ClassroomSquareButton(
                                text = "동화\n모두 보기",
                                onClick = onStoriesClick,
                                modifier = Modifier.weight(1f).aspectRatio(1f)
                            )
                            ClassroomSquareButton(
                                text = "학급 도토리\n충전",
                                onClick = { vm.chargeAcorn() },
                                modifier = Modifier.weight(1f).aspectRatio(1f)
                            )
                            ClassroomSquareButton(
                                text = "과제 관리",
                                onClick = onAssignmentsClick,
                                modifier = Modifier.weight(1f).aspectRatio(1f)
                            )
                        }
                    } else {
                        // --- 학생 ---
                        Spacer(Modifier.weight(1f))

                        // 버튼 3개를 한 줄(1x3)로 배치
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(spacerPadding)
                        ) {
                            ClassroomSquareButton(
                                text = "과제 확인",
                                onClick = onAssignmentsClick,
                                modifier = Modifier.weight(1f).aspectRatio(1f)
                            )
                            ClassroomSquareButton(
                                text = "내 동화\n보기",
                                onClick = onMyStoriesClick,
                                modifier = Modifier.weight(1f).aspectRatio(1f)
                            )
                            ClassroomSquareButton(
                                text = "동화\n모두 보기",
                                onClick = onStoriesClick,
                                modifier = Modifier.weight(1f).aspectRatio(1f)
                            )
                        }

                        // 위아래 같은 여백 > 패널을 남은 공간의 세로 가운데에 배치
                        Spacer(Modifier.weight(1f))
                    }
                }
            }
        }
    }

    // 경고창 (도토리 충전 한도 등)
    warning?.let { text ->
        WarningSheet(
            warningText = text,
            onDismiss = { vm.hideWarning() }
        )
    }
}
