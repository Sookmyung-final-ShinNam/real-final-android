package com.veryshinnam.myapp.feature.classroom.ui.home

import android.content.pm.ActivityInfo
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.veryshinnam.myapp.R
import com.veryshinnam.myapp.common.component.BackButton
import com.veryshinnam.myapp.common.component.CircleButton
import com.veryshinnam.myapp.common.component.LoadErrorView
import com.veryshinnam.myapp.common.component.LogoBar
import com.veryshinnam.myapp.common.component.WarningSheet
import com.veryshinnam.myapp.feature.classroom.data.model.JoinStatus
import androidx.compose.foundation.clickable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import com.veryshinnam.myapp.core.orientation.OrientationManager

@Composable
fun ClassroomHomeScreen(
    onBack: () -> Unit,
    onLogoClick: () -> Unit,
    onCreateClick: () -> Unit,
    onJoinClick: () -> Unit,
    onClassroomClick: (Long) -> Unit,
    vm: ClassroomHomeViewModel = hiltViewModel()
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val acornState by vm.acornState.collectAsStateWithLifecycle()
    var showRoleSheet by remember { mutableStateOf(false) }

    SideEffect { OrientationManager.setOrientation?.invoke(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) }

    Scaffold(
        containerColor = colorResource(R.color.background_yellow),
        topBar = {
            Column {
                Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
                LogoBar(onLogoClick = onLogoClick)
            }
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            when (val state = uiState) {
                is ClassroomHomeUiState.Loading -> CircularProgressIndicator(
                    color = colorResource(R.color.main_orange),
                    trackColor = Color.Gray.copy(alpha = 0.3f)
                )
                is ClassroomHomeUiState.Error -> LoadErrorView(
                    message = state.message,
                    onRetry = { vm.reload() }
                )
                is ClassroomHomeUiState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item { Spacer(Modifier.height(48.dp)) }

                        // 선생님 학급 섹션
                        if (!state.teacherData?.classrooms.isNullOrEmpty()) {
                            item {
                                SectionHeader("내가 만든 학급")
                            }
                            items(state.teacherData!!.classrooms) { classroom ->
                                ClassroomCard(
                                    name = classroom.name,
                                    studentCount = classroom.studentCount,
                                    code = classroom.code,
                                    badge = "선생님",
                                    badgeColor = colorResource(R.color.main_orange),
                                    onClick = { onClassroomClick(classroom.classroomId) }
                                )
                            }
                        }

                        // 학생 학급 섹션
                        if (!state.studentData?.classrooms.isNullOrEmpty()) {
                            item { SectionHeader("내가 가입한 학급") }
                            items(state.studentData!!.classrooms) { classroom ->
                                val isPending = classroom.joinStatus == JoinStatus.PENDING
                                ClassroomCard(
                                    name = classroom.name,
                                    studentCount = classroom.studentCount,
                                    code = classroom.code,
                                    badge = if (isPending) "승인 대기" else "학생",
                                    badgeColor = if (isPending) Color.Gray else colorResource(R.color.main_orange),
                                    onClick = { if (!isPending) onClassroomClick(classroom.classroomId) }
                                )
                            }
                        }

                        // 학급 없을 때
                        if (state.teacherData?.classrooms.isNullOrEmpty() &&
                            state.studentData?.classrooms.isNullOrEmpty()
                        ) {
                            item {
                                Box(Modifier.fillMaxWidth().padding(vertical = 40.dp), Alignment.Center) {
                                    Text(
                                        "아직 학급이 없어요.\n학급을 만들거나 가입해보세요!",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = Color.Gray,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                            }
                        }

                        item { Spacer(Modifier.height(16.dp)) }

                        // 하단 버튼
                        item {
                            CircleButton(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = { showRoleSheet = true },
                                text = "학급 참여하기"
                            )
                        }

                        item {
                            CircleButton(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = { vm.chargeAcorn() },
                                text = if (acornState.isLoading) "충전 중..." else "🌰 도토리 충전 (+5)"
                            )
                        }

                        item { Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars)) }
                    }
                }
            }

            BackButton(
                modifier = Modifier.align(Alignment.TopStart).padding(start = 16.dp, top = 8.dp),
                onBackClick = onBack
            )
        }
    }

    if (acornState.message != null) {
        WarningSheet(warningText = acornState.message!!, onDismiss = { vm.clearAcornMessage() })
    }

    // 역할 선택 바텀시트
    if (showRoleSheet) {
        RoleSelectSheet(
            onDismiss = { showRoleSheet = false },
            onTeacherClick = { showRoleSheet = false; onCreateClick() },
            onStudentClick = { showRoleSheet = false; onJoinClick() }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RoleSelectSheet(
    onDismiss: () -> Unit,
    onTeacherClick: () -> Unit,
    onStudentClick: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "어떤 역할로 참여하시나요?",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = colorResource(R.color.main_orange)
            )
            Spacer(Modifier.height(4.dp))

            // 선생님 카드
            RoleCard(
                emoji = "👩‍🏫",
                title = "선생님",
                description = "이메일 인증 후 새 학급을 만들어요",
                onClick = onTeacherClick
            )

            // 학생 카드
            RoleCard(
                emoji = "👨‍🎓",
                title = "학생",
                description = "선생님께 받은 코드로 학급에 가입해요",
                onClick = onStudentClick
            )
        }
    }
}

@Composable
private fun RoleCard(
    emoji: String,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(colorResource(R.color.background_yellow), RoundedCornerShape(16.dp))
            .border(2.dp, colorResource(R.color.main_orange), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(emoji, style = MaterialTheme.typography.headlineMedium)
        Column {
            Text(title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            Text(description, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = colorResource(R.color.main_orange),
        modifier = Modifier.padding(vertical = 4.dp)
    )
}

@Composable
private fun ClassroomCard(
    name: String,
    studentCount: Int,
    code: String,
    badge: String,
    badgeColor: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(16.dp))
            .border(2.dp, colorResource(R.color.main_orange), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = "학생 ${studentCount}명  ·  코드: $code",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
        Box(
            modifier = Modifier
                .background(badgeColor.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(text = badge, style = MaterialTheme.typography.labelSmall, color = badgeColor)
        }
    }
}
