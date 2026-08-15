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
import com.veryshinnam.myapp.feature.classroom.data.model.JoinStatus
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
                        item { Spacer(Modifier.height(8.dp)) }

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
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                CircleButton(
                                    modifier = Modifier.weight(1f),
                                    onClick = onCreateClick,
                                    text = "학급 만들기"
                                )
                                CircleButton(
                                    modifier = Modifier.weight(1f),
                                    onClick = onJoinClick,
                                    text = "학급 가입"
                                )
                            }
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
