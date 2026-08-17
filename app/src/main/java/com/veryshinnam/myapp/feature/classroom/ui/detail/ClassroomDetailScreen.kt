package com.veryshinnam.myapp.feature.classroom.ui.detail

import android.content.pm.ActivityInfo
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import com.veryshinnam.myapp.feature.classroom.data.model.StudentItemData
import com.veryshinnam.myapp.core.orientation.OrientationManager

@Composable
fun ClassroomDetailScreen(
    classroomId: Long,
    onBack: () -> Unit,
    onAssignmentsClick: (isTeacher: Boolean) -> Unit,
    onStoriesClick: (Int) -> Unit,
    vm: ClassroomDetailViewModel = hiltViewModel()
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val approveResult by vm.approveResult.collectAsStateWithLifecycle()

    SideEffect { OrientationManager.setOrientation?.invoke(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) }

    LaunchedEffect(approveResult) {
        if (approveResult != null) kotlinx.coroutines.delay(1500); vm.clearApproveResult()
    }

    Scaffold(
        containerColor = colorResource(R.color.background_yellow),
        topBar = {
            Column {
                Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
                LogoBar()
            }
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when (val state = uiState) {
                is ClassroomDetailUiState.Loading -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                    CircularProgressIndicator(color = colorResource(R.color.main_orange), trackColor = Color.Gray.copy(0.3f))
                }
                is ClassroomDetailUiState.Error -> LoadErrorView(message = state.message, onRetry = { vm.reload() })
                is ClassroomDetailUiState.Success -> {
                    val detail = state.detail
                    val isTeacher = state.isTeacher
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item { Spacer(Modifier.height(8.dp)) }

                        // 학급 정보 카드
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White, RoundedCornerShape(16.dp))
                                    .border(2.dp, colorResource(R.color.main_orange), RoundedCornerShape(16.dp))
                                    .padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(detail.name, style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold))
                                Text("개설일: ${detail.createdAt.take(10)}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("학급 포인트 ", style = MaterialTheme.typography.bodyMedium)
                                    Text("${detail.points}P", style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = colorResource(R.color.main_orange)
                                    ))
                                }
                            }
                        }

                        // 버튼 행
                        item {
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                CircleButton(
                                    text = "과제 목록",
                                    onClick = { onAssignmentsClick(isTeacher) },
                                    modifier = Modifier.weight(1f)
                                )
                                CircleButton(
                                    text = "학급 동화",
                                    onClick = { onStoriesClick(1) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        // 학생 목록 헤더
                        item {
                            Text(
                                "학생 목록 (${detail.students.size}명)",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = colorResource(R.color.main_orange)
                            )
                        }

                        items(detail.students) { student ->
                            StudentRow(student = student, onApprove = { vm.approveStudent(student.studentId) })
                        }

                        item { Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars)) }
                    }
                }
            }

            BackButton(modifier = Modifier.align(Alignment.TopStart).padding(start = 16.dp, top = 8.dp), onBackClick = onBack)

            // 승인 결과 스낵바
            if (approveResult != null) {
                Box(
                    modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter).padding(16.dp)
                ) {
                    Box(
                        Modifier
                            .background(colorResource(R.color.main_orange), RoundedCornerShape(12.dp))
                            .padding(horizontal = 20.dp, vertical = 12.dp)
                            .align(Alignment.Center)
                    ) {
                        Text(approveResult!!, color = Color.White, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

@Composable
private fun StudentRow(student: StudentItemData, onApprove: () -> Unit) {
    val isPending = student.joinStatus == JoinStatus.PENDING
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(12.dp))
            .border(1.5.dp, if (isPending) Color.LightGray else colorResource(R.color.main_orange_50), RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                modifier = Modifier.size(32.dp).background(
                    if (isPending) Color.LightGray else colorResource(R.color.main_orange_50),
                    CircleShape
                ),
                contentAlignment = Alignment.Center
            ) {
                Text("${student.number}", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
            }
            Text(student.studentName, style = MaterialTheme.typography.bodyLarge)
        }

        if (isPending) {
            OutlinedButton(
                onClick = onApprove,
                shape = RoundedCornerShape(20.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.5.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = colorResource(R.color.main_orange))
            ) {
                Text("승인", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
            }
        } else {
            Box(
                modifier = Modifier
                    .background(colorResource(R.color.main_orange).copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text("승인됨", style = MaterialTheme.typography.labelSmall, color = colorResource(R.color.main_orange))
            }
        }
    }
}
