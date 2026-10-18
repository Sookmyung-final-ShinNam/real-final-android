package com.veryshinnam.myapp.feature.classroom.ui

import android.content.pm.ActivityInfo
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.veryshinnam.myapp.R
import com.veryshinnam.myapp.common.component.WarningSheet
import com.veryshinnam.myapp.core.orientation.OrientationManager
import com.veryshinnam.myapp.feature.classroom.component.ClassroomActionButton
import com.veryshinnam.myapp.feature.classroom.component.ClassroomScaffold
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneOffset

// UTC 자정 millis > 날짜 (DatePicker는 UTC 기준)
private fun utcMillisToDate(millis: Long): LocalDate =
    Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate()

// 오전/오후 표시용 (오후 11:59)
private fun timeText(hour: Int, minute: Int): String {
    val period = if (hour < 12) "오전" else "오후"
    val hour12 = if (hour % 12 == 0) 12 else hour % 12
    return "$period $hour12:${"%02d".format(minute)}"
}

/**
 * 과제 만들기 화면 (선생님)
 * : 과제명, 마감일(날짜 + 시간), 과제 요약, 공통 프롬프트 입력 후 등록
 *   공통 프롬프트(주제/교훈)는 학생들의 동화 생성에 반영됨
 *
 * - classroomId: 학급 아이디
 * - onBack: 뒤로 가기 버튼 클릭 시 / 등록 완료 시
 * - onLogoClick: 로고 클릭 시, 홈 화면으로 복구
 */
@Composable
fun AssignmentCreateScreen(
    classroomId: Long,
    onBack: () -> Unit,
    onLogoClick: () -> Unit,
    horizontalPadding: Dp = 16.dp,
    vm: AssignmentCreateViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    // 상태 구독
    val isSubmitting by vm.isSubmitting.collectAsStateWithLifecycle()
    val warning by vm.warning.collectAsStateWithLifecycle()

    // 입력값
    var title by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var commonPrompt by rememberSaveable { mutableStateOf("") }
    var dueDateText by rememberSaveable { mutableStateOf<String?>(null) } // yyyy-MM-dd
    var dueHour by rememberSaveable { mutableStateOf(23) }
    var dueMinute by rememberSaveable { mutableStateOf(59) }

    var isDatePicking by remember { mutableStateOf(false) }
    var isTimePicking by remember { mutableStateOf(false) }

    val dueDate = dueDateText?.let { LocalDate.parse(it) }
    val canSubmit = title.isNotBlank() && description.isNotBlank() &&
            commonPrompt.isNotBlank() && dueDate != null && !isSubmitting

    // 세로 모드 고정
    SideEffect {
        OrientationManager.setOrientation?.invoke(
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        )
    }

    // 등록 완료 > 과제 목록으로 복귀
    LaunchedEffect(Unit) {
        vm.created.collect {
            Toast.makeText(context, "과제를 등록했어요.", Toast.LENGTH_SHORT).show()
            onBack()
        }
    }

    ClassroomScaffold(
        onBack = onBack,
        onLogoClick = onLogoClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = horizontalPadding)
                .padding(top = 40.dp) // 뒤로 가기 버튼 아래
        ) {
            // --- 입력 영역
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .border(4.dp, colorResource(R.color.main_orange), RoundedCornerShape(16.dp))
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "과제 만들기",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )

                // 과제명
                FormField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = "제목을 입력해 주세요. (30자 이하)",
                    maxLength = 30
                )

                // 마감일 설정 (날짜 + 시간)
                Spacer(Modifier.height(4.dp))
                FormLabel(text = "마감일 설정", required = true)
                PickerRow(
                    icon = Icons.Filled.CalendarMonth,
                    text = dueDate?.toString()?.replace('-', '.') ?: "과제 마감일을 선택해 주세요.",
                    isPlaceholder = dueDate == null,
                    description = "마감 날짜 선택. " + (dueDate?.let { "선택한 날짜 $it" } ?: "아직 선택하지 않았어요"),
                    onClick = { isDatePicking = true }
                )
                PickerRow(
                    icon = Icons.Filled.Schedule,
                    text = timeText(dueHour, dueMinute),
                    isPlaceholder = false,
                    description = "마감 시간 선택. 선택한 시간 ${timeText(dueHour, dueMinute)}",
                    onClick = { isTimePicking = true }
                )

                // 과제 요약
                Spacer(Modifier.height(4.dp))
                FormLabel(text = "과제 요약", required = true)
                FormField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = "학생들에게 보여줄 과제 내용을 적어 주세요.",
                    maxLength = 200,
                    singleLine = false,
                    minLines = 3
                )

                // 공통 프롬프트
                Spacer(Modifier.height(4.dp))
                FormLabel(text = "공통 프롬프트", required = true)
                Text(
                    text = "학생들이 동화를 만들 때 자동으로 반영돼요. 예) 주제: 우정, 교훈: 친구를 배려하는 마음",
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                )
                FormField(
                    value = commonPrompt,
                    onValueChange = { commonPrompt = it },
                    placeholder = "주제와 교훈을 적어 주세요.",
                    maxLength = 300,
                    singleLine = false,
                    minLines = 3
                )
            }

            // --- 등록하기
            ClassroomActionButton(
                text = "등록하기",
                enabled = canSubmit,
                onClick = {
                    val date = dueDate ?: return@ClassroomActionButton

                    // 마감 시각은 지금보다 뒤여야 함
                    if (!date.atTime(dueHour, dueMinute).isAfter(LocalDateTime.now())) {
                        vm.showWarning("마감 시각은 지금보다 뒤로 정해 주세요.")
                        return@ClassroomActionButton
                    }

                    vm.createAssignment(
                        classroomId = classroomId,
                        title = title,
                        description = description,
                        dueAt = "${date}T${"%02d".format(dueHour)}:${"%02d".format(dueMinute)}:00",
                        commonPrompt = commonPrompt
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            )
        }
    }

    // 마감 날짜 선택
    if (isDatePicking) {
        DueDatePickerDialog(
            onDismiss = { isDatePicking = false },
            onSelect = { dueDateText = it.toString() }
        )
    }

    // 마감 시간 선택
    if (isTimePicking) {
        DueTimePickerDialog(
            initialHour = dueHour,
            initialMinute = dueMinute,
            onDismiss = { isTimePicking = false },
            onConfirm = { hour, minute ->
                dueHour = hour
                dueMinute = minute
            }
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

// 항목 이름 + (필수)
@Composable
private fun FormLabel(text: String, required: Boolean) {
    Row(
        modifier = Modifier.semantics(mergeDescendants = true) {
            contentDescription = text + if (required) ", 필수" else ""
        }
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
        )
        if (required) {
            Text(
                text = " (필수)",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = colorResource(R.color.deep_green)
                )
            )
        }
    }
}

// 입력창 (흰 배경 + 주황 테두리)
@Composable
private fun FormField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    maxLength: Int,
    singleLine: Boolean = true,
    minLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = { if (it.length <= maxLength) onValueChange(it) },
        placeholder = { Text(placeholder, color = Color.Gray) },
        singleLine = singleLine,
        minLines = minLines,
        supportingText = {
            Text(
                text = "${value.length}/$maxLength",
                color = Color.Gray,
                modifier = Modifier.fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.End
            )
        },
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedBorderColor = colorResource(R.color.main_orange),
            unfocusedBorderColor = colorResource(R.color.main_orange_50),
            cursorColor = colorResource(R.color.main_orange)
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

// 날짜/시간 선택 줄 (초록 아이콘 + 값)
@Composable
private fun PickerRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    isPlaceholder: Boolean,
    description: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, colorResource(R.color.main_orange_50), RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .semantics {
                contentDescription = description
                role = Role.Button
            }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(colorResource(R.color.deep_green), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null, // 장식용
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }

        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = if (isPlaceholder) FontWeight.Normal else FontWeight.Bold,
                color = if (isPlaceholder) Color.Gray else Color.Black
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DueDatePickerDialog(
    onDismiss: () -> Unit,
    onSelect: (LocalDate) -> Unit
) {
    val today = remember { LocalDate.now() }
    val state = rememberDatePickerState(
        // 오늘 이전 날짜는 선택 불가
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean =
                !utcMillisToDate(utcTimeMillis).isBefore(today)
        }
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    state.selectedDateMillis?.let { onSelect(utcMillisToDate(it)) }
                    onDismiss()
                }
            ) { Text("확인", color = colorResource(R.color.dark_orange)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("취소", color = colorResource(R.color.dark_orange)) }
        }
    ) {
        DatePicker(state = state)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DueTimePickerDialog(
    initialHour: Int,
    initialMinute: Int,
    onDismiss: () -> Unit,
    onConfirm: (hour: Int, minute: Int) -> Unit
) {
    val state = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = false
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(state.hour, state.minute)
                    onDismiss()
                }
            ) { Text("확인", color = colorResource(R.color.dark_orange)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("취소", color = colorResource(R.color.dark_orange)) }
        },
        text = { TimePicker(state = state) }
    )
}
