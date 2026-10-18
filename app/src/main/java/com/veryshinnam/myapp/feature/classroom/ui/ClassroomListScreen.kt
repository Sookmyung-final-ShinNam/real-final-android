package com.veryshinnam.myapp.feature.classroom.ui

import android.content.pm.ActivityInfo
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.delay
import com.veryshinnam.myapp.common.component.LoadErrorView
import com.veryshinnam.myapp.common.component.WarningSheet
import com.veryshinnam.myapp.core.orientation.OrientationManager
import com.veryshinnam.myapp.feature.classroom.component.ClassroomAddCard
import com.veryshinnam.myapp.feature.classroom.component.ClassroomCodeSheet
import com.veryshinnam.myapp.feature.classroom.component.ClassroomHeader
import com.veryshinnam.myapp.feature.classroom.component.ClassroomInputSheet
import com.veryshinnam.myapp.feature.classroom.component.ClassroomLoading
import com.veryshinnam.myapp.feature.classroom.component.ClassroomMenuCard
import com.veryshinnam.myapp.feature.classroom.component.ClassroomScaffold
import com.veryshinnam.myapp.feature.classroom.component.rememberCopyToClipboard
import com.veryshinnam.myapp.feature.classroom.model.JoinStatus
import androidx.compose.ui.res.colorResource
import com.veryshinnam.myapp.R

/**
 * 나의 학급 화면 (학급 목록)
 * : 환경설정의 "나의 학급" 버튼으로 진입. 역할(선생님/학생)에 따라 구성이 달라짐
 *   - 선생님: 만든 학급 목록 + 새로운 학급 만들기
 *   - 학생: 가입한(대기 중 포함) 학급 목록 + 새로운 학급 가입하기
 *
 * - onBack: 뒤로 가기 버튼 클릭 시
 * - onLogoClick: 로고 클릭 시, 홈 화면으로 복구
 * - onClassroomClick: 학급 클릭 시, 학급 화면으로 이동 (학급 아이디, 선생님 여부 전달)
 */
@Composable
fun ClassroomListScreen(
    onBack: () -> Unit,
    onLogoClick: () -> Unit,
    onClassroomClick: (classroomId: Long, isTeacher: Boolean) -> Unit,
    spacerPadding: Dp = 10.dp,
    horizontalPadding: Dp = 16.dp,
    vm: ClassroomListViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val copyToClipboard = rememberCopyToClipboard()

    // 상태 구독
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val warning by vm.warning.collectAsStateWithLifecycle()
    val createdCode by vm.createdCode.collectAsStateWithLifecycle()

    var isCreateSheet by remember { mutableStateOf(false) } // 학급 만들기 시트
    var isJoinSheet by remember { mutableStateOf(false) }   // 학급 가입하기 시트

    // 세로 모드 고정
    SideEffect {
        OrientationManager.setOrientation?.invoke(
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        )
    }

    // 화면 진입 시 목록 조회 (학급 화면에서 돌아올 때도 갱신)
    LaunchedEffect(Unit) { vm.load() }

    // 토스트 이벤트
    LaunchedEffect(Unit) {
        vm.toast.collect { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
    }

    // 승인 대기 중인 학급이 있으면 화면이 보이는 동안 주기적으로 확인
    // : 선생님이 승인하면 뒤로 갔다 오지 않아도 이 화면에서 바로 들어갈 수 있음
    //   (대기 중인 학급이 없으면 호출해도 아무 일도 하지 않음)
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            while (true) {
                delay(5_000)
                vm.refreshPending()
            }
        }
    }

    ClassroomScaffold(
        onBack = onBack,
        onLogoClick = onLogoClick
    ) {
        when (val state = uiState) {
            // 조회 로딩
            is ClassroomListUiState.Loading -> ClassroomLoading()

            // 조회 오류
            is ClassroomListUiState.Error -> {
                LoadErrorView(
                    message = state.message,
                    onRetry = { vm.load() }
                )
            }

            // 조회 성공
            is ClassroomListUiState.Success -> {
                val isTeacher = state.isTeacher

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = horizontalPadding)
                ) {
                    // 상단 인사
                    ClassroomHeader(
                        message = "안녕하세요, ${state.nickname} ${if (isTeacher) "선생님" else "학생"}!"
                    )

                    Spacer(Modifier.height(spacerPadding * 2))

                    // 학급 목록 + 추가 카드
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(spacerPadding * 1.5f)
                    ) {
                        items(state.classrooms, key = { it.id }) { classroom ->
                            val isPending = classroom.joinStatus == JoinStatus.PENDING

                            ClassroomMenuCard(
                                title = classroom.name,
                                subtitle = when {
                                    isTeacher -> "학급 코드 ${classroom.code} · 학생 ${classroom.studentCount}명"
                                    isPending -> "승인 대기 중"
                                    else -> "학생 ${classroom.studentCount}명"
                                },
                                onClick = {
                                    if (isPending) {
                                        // 승인 전 학급은 상세 조회 불가
                                        vm.showWarning("아직 선생님이 승인하지 않았어요.\n승인되면 학급에 들어갈 수 있어요!")
                                    } else {
                                        onClassroomClick(classroom.id, isTeacher)
                                    }
                                },
                                trailing = {
                                    // 선생님: 학급 코드 복사
                                    if (isTeacher) {
                                        Text(
                                            text = "복사",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            ),
                                            modifier = Modifier
                                                .padding(end = 12.dp)
                                                .background(Color.White.copy(alpha = 0.25f), CircleShape)
                                                .clickable {
                                                    copyToClipboard(classroom.code)
                                                    Toast.makeText(context, "학급 코드를 복사했어요.", Toast.LENGTH_SHORT).show()
                                                }
                                                .semantics {
                                                    contentDescription = "${classroom.name} 학급 코드 복사"
                                                    role = Role.Button
                                                }
                                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            )
                        }

                        item {
                            ClassroomAddCard(
                                title = if (isTeacher) "새로운 학급 만들기" else "새로운 학급 가입하기",
                                onClick = { if (isTeacher) isCreateSheet = true else isJoinSheet = true }
                            )
                        }
                    }
                }
            }
        }
    }

    // 학급 만들기 시트 (선생님)
    if (isCreateSheet) {
        ClassroomInputSheet(
            title = "학급 만들기",
            description = "학급 이름을 입력해 주세요.\n만들고 나면 학급 코드가 발급돼요.",
            label = "학급 이름",
            confirmText = "만들기",
            maxLength = 20,
            onDismiss = { isCreateSheet = false },
            onConfirm = { name -> vm.createClassroom(name) }
        )
    }

    // 학급 가입하기 시트 (학생)
    if (isJoinSheet) {
        ClassroomInputSheet(
            title = "학급 가입하기",
            description = "선생님께 받은 8자리 학급 코드를 입력해 주세요.\n선생님이 승인하면 학급에서 활동할 수 있어요.",
            label = "학급 코드",
            confirmText = "가입 신청하기",
            maxLength = 8,
            capitalization = KeyboardCapitalization.Characters,
            onDismiss = { isJoinSheet = false },
            onConfirm = { code -> vm.joinClassroom(code) }
        )
    }

    // 학급 생성 직후 학급 코드 안내
    createdCode?.let { code ->
        ClassroomCodeSheet(
            code = code,
            onCopy = { text ->
                copyToClipboard(text)
                Toast.makeText(context, "학급 코드를 복사했어요.", Toast.LENGTH_SHORT).show()
            },
            onDismiss = { vm.hideCreatedCode() }
        )
    }

    // 경고창
    warning?.let { text ->
        WarningSheet(
            warningText = text,
            onDismiss = { vm.hideWarning() }
        )
    }
}
