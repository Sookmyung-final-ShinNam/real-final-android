package com.veryshinnam.myapp.feature.classroom.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import com.veryshinnam.myapp.R
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneOffset

// --- 공통 틀: 주황 바텀시트 (경고창 시트와 같은 색) ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SheetFrame(
    title: String,
    onDismiss: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
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
                .verticalScroll(rememberScrollState())
                .imePadding()
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
                    text = title,
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

            content()
        }
    }
}

// 시트 안 입력창 (흰 배경)
@Composable
private fun SheetTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    maxLength: Int,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    minLines: Int = 1,
    capitalization: KeyboardCapitalization = KeyboardCapitalization.None
) {
    OutlinedTextField(
        value = value,
        onValueChange = { if (it.length <= maxLength) onValueChange(it) },
        label = { Text(label) },
        singleLine = singleLine,
        minLines = minLines,
        keyboardOptions = KeyboardOptions(capitalization = capitalization),
        supportingText = {
            Text(
                text = "${value.length}/$maxLength",
                color = Color.White,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End
            )
        },
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedBorderColor = colorResource(R.color.main_orange_shade),
            unfocusedBorderColor = colorResource(R.color.main_orange_50),
            focusedLabelColor = colorResource(R.color.main_orange_shade),
            cursorColor = colorResource(R.color.main_orange_shade)
        ),
        modifier = modifier.fillMaxWidth()
    )
}

// 시트 확인 버튼 (경고창 시트의 확인 버튼과 같은 모양)
@Composable
private fun SheetConfirmButton(
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

/**
 * 한 줄 입력 시트
 * : 학급 만들기(학급명), 학급 가입하기(학급 코드)에서 사용
 *
 * - description: 입력창 위 안내 문구
 * - capitalization: 학급 코드처럼 대문자 입력이 필요한 경우 Characters
 * - onConfirm: 확인 버튼 클릭 시, 입력값 전달 후 시트 닫힘
 */
@Composable
fun ClassroomInputSheet(
    title: String,
    description: String,
    label: String,
    confirmText: String,
    maxLength: Int,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
    capitalization: KeyboardCapitalization = KeyboardCapitalization.None
) {
    var text by remember { mutableStateOf("") }

    SheetFrame(title = title, onDismiss = onDismiss) {
        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge.copy(color = Color.White)
        )

        SheetTextField(
            value = text,
            onValueChange = { text = if (capitalization == KeyboardCapitalization.Characters) it.uppercase() else it },
            label = label,
            maxLength = maxLength,
            capitalization = capitalization
        )

        SheetConfirmButton(
            text = confirmText,
            enabled = text.isNotBlank(),
            onClick = {
                onConfirm(text)
                onDismiss()
            }
        )
    }
}

/**
 * 학급 코드 안내 시트
 * : 학급 생성 직후 발급된 코드를 보여주고 복사할 수 있게 함
 */
@Composable
fun ClassroomCodeSheet(
    code: String,
    onCopy: (String) -> Unit,
    onDismiss: () -> Unit
) {
    SheetFrame(title = "학급을 만들었어요!", onDismiss = onDismiss) {
        Text(
            text = "아래 학급 코드를 학생들에게 알려 주세요.\n학생이 코드를 입력하면 가입 신청이 들어와요.",
            style = MaterialTheme.typography.bodyLarge.copy(color = Color.White)
        )

        // 학급 코드
        Text(
            text = code,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Black,
                color = colorResource(R.color.main_orange_shade),
                textAlign = TextAlign.Center,
                letterSpacing = MaterialTheme.typography.titleLarge.fontSize * 0.2f
            ),
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(16.dp))
                .padding(vertical = 16.dp)
                .semantics { contentDescription = "학급 코드 ${code.toCharArray().joinToString(" ")}" }
        )

        SheetConfirmButton(
            text = "코드 복사하기",
            enabled = true,
            onClick = {
                onCopy(code)
                onDismiss()
            }
        )
    }
}

// 클립보드 복사 헬퍼 (학급 코드)
@Composable
fun rememberCopyToClipboard(): (String) -> Unit {
    val clipboard = LocalClipboardManager.current
    return { text -> clipboard.setText(AnnotatedString(text)) }
}
