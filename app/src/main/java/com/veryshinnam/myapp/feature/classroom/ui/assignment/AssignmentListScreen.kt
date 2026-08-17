package com.veryshinnam.myapp.feature.classroom.ui.assignment

import android.content.pm.ActivityInfo
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.veryshinnam.myapp.R
import com.veryshinnam.myapp.common.component.BackButton
import com.veryshinnam.myapp.common.component.CircleButton
import com.veryshinnam.myapp.common.component.LoadErrorView
import com.veryshinnam.myapp.common.component.LogoBar
import com.veryshinnam.myapp.common.component.WarningSheet
import com.veryshinnam.myapp.feature.classroom.data.model.AssignmentData
import com.veryshinnam.myapp.core.orientation.OrientationManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignmentListScreen(
    classroomId: Long,
    isTeacher: Boolean = false,
    onBack: () -> Unit,
    onStartAssignment: (Long) -> Unit,
    vm: AssignmentListViewModel = hiltViewModel()
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val createState by vm.createState.collectAsStateWithLifecycle()
    var showCreateSheet by remember { mutableStateOf(false) }

    SideEffect { OrientationManager.setOrientation?.invoke(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) }

    // 등록 완료 시 시트 닫기
    LaunchedEffect(createState.isDone) {
        if (createState.isDone) {
            showCreateSheet = false
            vm.resetCreateState()
        }
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
                is AssignmentListUiState.Loading -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                    CircularProgressIndicator(color = colorResource(R.color.main_orange), trackColor = Color.Gray.copy(0.3f))
                }
                is AssignmentListUiState.Error -> LoadErrorView(message = state.message, onRetry = { vm.reload() })
                is AssignmentListUiState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item { Spacer(Modifier.height(8.dp)) }

                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "과제 목록",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    color = colorResource(R.color.main_orange)
                                )
                                // 과제 등록 버튼 (선생님만)
                                if (isTeacher) {
                                    OutlinedButton(
                                        onClick = { showCreateSheet = true },
                                        shape = RoundedCornerShape(20.dp),
                                        border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.5.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            contentColor = colorResource(R.color.main_orange)
                                        ),
                                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                                    ) {
                                        Text("+ 과제 등록", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                                    }
                                }
                            }
                        }

                        if (state.assignments.isEmpty()) {
                            item {
                                Box(Modifier.fillMaxWidth().padding(vertical = 40.dp), Alignment.Center) {
                                    Text(
                                        "아직 등록된 과제가 없어요.",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = Color.Gray,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        } else {
                            itemsIndexed(state.assignments) { index, item ->
                                AssignmentCard(
                                    week = index + 1,
                                    assignment = item.data,
                                    isTeacher = isTeacher,
                                    submittedCount = item.submittedCount,
                                    notSubmittedCount = item.notSubmittedCount,
                                    onStart = { onStartAssignment(item.data.assignmentId) }
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

    // 과제 등록 바텀시트
    if (showCreateSheet) {
        CreateAssignmentSheet(
            isLoading = createState.isLoading,
            onDismiss = { showCreateSheet = false; vm.resetCreateState() },
            onCreate = { title, desc, dueAt, prompt ->
                vm.createAssignment(title, desc, dueAt, prompt)
            }
        )
    }

    if (createState.errorMessage != null) {
        WarningSheet(warningText = createState.errorMessage!!, onDismiss = { vm.resetCreateState() })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateAssignmentSheet(
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onCreate: (String, String, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var dueAt by remember { mutableStateOf("") }
    var commonPrompt by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val orange = colorResource(R.color.main_orange)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                "과제 등록",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = orange
            )

            AssignmentTextField(value = title, onValueChange = { title = it }, label = "과제명 *", placeholder = "예) 1주차 - 우정 이야기")
            AssignmentTextField(value = description, onValueChange = { description = it }, label = "과제 설명", placeholder = "선택사항", singleLine = false)
            AssignmentTextField(
                value = dueAt,
                onValueChange = { dueAt = it },
                label = "마감기한 *",
                placeholder = "예) 2025-09-01T23:59:00"
            )
            AssignmentTextField(
                value = commonPrompt,
                onValueChange = { commonPrompt = it },
                label = "공통 프롬프트 *",
                placeholder = "예) 우정과 협동을 주제로, 친구를 도와주는 교훈을 담아주세요.",
                singleLine = false
            )

            Spacer(Modifier.height(4.dp))

            CircleButton(
                text = if (isLoading) "등록 중..." else "과제 등록하기",
                onClick = {
                    focusManager.clearFocus()
                    if (title.isNotBlank() && dueAt.isNotBlank() && commonPrompt.isNotBlank() && !isLoading) {
                        onCreate(title, description, dueAt, commonPrompt)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun AssignmentTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    singleLine: Boolean = true
) {
    val orange = colorResource(R.color.main_orange)
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = orange))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color.LightGray, style = MaterialTheme.typography.bodySmall) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            singleLine = singleLine,
            minLines = if (singleLine) 1 else 3,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = orange,
                unfocusedBorderColor = colorResource(R.color.main_orange_50),
                cursorColor = orange
            )
        )
    }
}

@Composable
private fun AssignmentCard(
    week: Int,
    assignment: AssignmentData,
    isTeacher: Boolean = false,
    submittedCount: Long = 0,
    notSubmittedCount: Long = 0,
    onStart: () -> Unit
) {
    val dDay = assignment.dDay
    val dDayText = when {
        dDay > 0 -> "D-$dDay"
        dDay == 0L -> "D-Day"
        else -> "마감됨"
    }
    val dDayColor = when {
        dDay > 3 -> colorResource(R.color.main_orange)
        dDay >= 0 -> Color(0xFFE53935)
        else -> Color.Gray
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(16.dp))
            .border(2.dp, colorResource(R.color.main_orange), RoundedCornerShape(16.dp))
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .background(colorResource(R.color.main_orange_50), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text("${week}주차", style = MaterialTheme.typography.labelSmall.copy(color = colorResource(R.color.main_orange)))
                }
                Text(assignment.title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            }
            Text(dDayText, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = dDayColor))
        }

        if (!assignment.description.isNullOrBlank()) {
            Text(assignment.description, style = MaterialTheme.typography.bodySmall, color = Color.DarkGray, maxLines = 2)
        }

        Text("마감: ${assignment.dueAt.take(10)}", style = MaterialTheme.typography.labelSmall, color = Color.Gray)

        Spacer(Modifier.height(4.dp))

        if (isTeacher) {
            // 선생님: 제출 현황
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colorResource(R.color.main_orange_50), RoundedCornerShape(10.dp))
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("제출", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Text("$submittedCount 명", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = colorResource(R.color.main_orange)))
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("미제출", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Text("$notSubmittedCount 명", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFFE53935)))
                }
            }
        } else {
            // 학생: 동화 만들기 버튼
            CircleButton(
                text = if (dDay < 0) "마감된 과제" else "동화 만들기 시작",
                onClick = { if (dDay >= 0) onStart() },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
