package com.veryshinnam.myapp.feature.classroom.ui.create

import android.content.pm.ActivityInfo
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.veryshinnam.myapp.R
import com.veryshinnam.myapp.common.component.BackButton
import com.veryshinnam.myapp.common.component.CircleButton
import com.veryshinnam.myapp.common.component.LogoBar
import com.veryshinnam.myapp.common.component.WarningSheet
import com.veryshinnam.myapp.core.orientation.OrientationManager

@Composable
fun CreateClassroomScreen(
    onBack: () -> Unit,
    onSuccess: () -> Unit,
    vm: CreateClassroomViewModel = hiltViewModel()
) {
    val state by vm.uiState.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current

    SideEffect { OrientationManager.setOrientation?.invoke(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) }

    LaunchedEffect(state.isDone) { if (state.isDone) onSuccess() }

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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // 단계 인디케이터
                StepIndicator(currentStep = state.step)

                Spacer(Modifier.height(32.dp))

                when (state.step) {
                    CreateStep.EMAIL -> EmailStep(
                        isLoading = state.isLoading,
                        onSend = { email -> focusManager.clearFocus(); vm.sendEmailVerification(email) }
                    )
                    CreateStep.VERIFY -> VerifyStep(
                        isLoading = state.isLoading,
                        onVerify = { code -> focusManager.clearFocus(); vm.verifyEmail(code) }
                    )
                    CreateStep.NAME -> {
                        if (state.generatedCode != null) {
                            SuccessContent(code = state.generatedCode!!, onConfirm = { vm.confirmDone() })
                        } else {
                            NameStep(
                                isLoading = state.isLoading,
                                onCreate = { name -> focusManager.clearFocus(); vm.createClassroom(name) }
                            )
                        }
                    }
                }
            }

            BackButton(
                onClick = onBack,
                modifier = Modifier.align(Alignment.TopStart).padding(start = 16.dp, top = 8.dp)
            )
        }
    }

    if (state.errorMessage != null) {
        WarningSheet(warningText = state.errorMessage!!, onDismiss = { vm.clearError() })
    }
}

@Composable
private fun StepIndicator(currentStep: CreateStep) {
    val steps = listOf("이메일 인증", "코드 확인", "학급명 입력")
    val currentIndex = CreateStep.entries.indexOf(currentStep)
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        steps.forEachIndexed { i, label ->
            val active = i == currentIndex
            Box(
                modifier = Modifier
                    .background(
                        if (active) colorResource(R.color.main_orange) else colorResource(R.color.main_orange_50),
                        RoundedCornerShape(20.dp)
                    )
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    label,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = if (active) FontWeight.Bold else FontWeight.Normal,
                        color = if (active) Color.White else colorResource(R.color.main_orange)
                    )
                )
            }
            if (i < steps.lastIndex) {
                Text("→", color = colorResource(R.color.main_orange))
            }
        }
    }
}

@Composable
private fun EmailStep(isLoading: Boolean, onSend: (String) -> Unit) {
    var email by remember { mutableStateOf("") }
    StepCard(title = "선생님 이메일을 입력해주세요", description = "학급 생성을 위해 이메일 인증이 필요해요.") {
        ClassroomTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = "example@school.kr",
            keyboardType = KeyboardType.Email
        )
        Spacer(Modifier.height(16.dp))
        CircleButton(
            text = if (isLoading) "전송 중..." else "인증코드 받기",
            onClick = { if (email.isNotBlank() && !isLoading) onSend(email) },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun VerifyStep(isLoading: Boolean, onVerify: (String) -> Unit) {
    var code by remember { mutableStateOf("") }
    StepCard(title = "인증코드를 입력해주세요", description = "이메일로 받은 6자리 코드를 입력해주세요. (5분 유효)") {
        ClassroomTextField(
            value = code,
            onValueChange = { if (it.length <= 6) code = it },
            placeholder = "123456",
            keyboardType = KeyboardType.Number
        )
        Spacer(Modifier.height(16.dp))
        CircleButton(
            text = if (isLoading) "확인 중..." else "인증하기",
            onClick = { if (code.length == 6 && !isLoading) onVerify(code) },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun NameStep(isLoading: Boolean, onCreate: (String) -> Unit) {
    var name by remember { mutableStateOf("") }
    StepCard(title = "학급 이름을 지어주세요", description = "학급 코드가 자동으로 발급돼요.") {
        ClassroomTextField(
            value = name,
            onValueChange = { if (it.length <= 20) name = it },
            placeholder = "예) 3학년 2반"
        )
        Spacer(Modifier.height(16.dp))
        CircleButton(
            text = if (isLoading) "생성 중..." else "학급 만들기",
            onClick = { if (name.isNotBlank() && !isLoading) onCreate(name) },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun SuccessContent(code: String, onConfirm: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("🎉 학급이 만들어졌어요!", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold))
        Text("아이들에게 아래 코드를 알려주세요.", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        Box(
            modifier = Modifier
                .background(Color.White, RoundedCornerShape(16.dp))
                .border(3.dp, colorResource(R.color.main_orange), RoundedCornerShape(16.dp))
                .padding(horizontal = 40.dp, vertical = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = code,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = colorResource(R.color.main_orange),
                    letterSpacing = androidx.compose.ui.unit.TextUnit(4f, androidx.compose.ui.unit.TextUnitType.Sp)
                )
            )
        }
        Spacer(Modifier.height(8.dp))
        CircleButton(text = "확인", onClick = onConfirm, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun StepCard(title: String, description: String, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(20.dp))
            .border(2.dp, colorResource(R.color.main_orange), RoundedCornerShape(20.dp))
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        Text(description, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        Spacer(Modifier.height(8.dp))
        content()
    }
}

@Composable
private fun ClassroomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    val orange = colorResource(R.color.main_orange)
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = Color.LightGray) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = orange,
            unfocusedBorderColor = colorResource(R.color.main_orange_50),
            cursorColor = orange
        ),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = ImeAction.Done),
        singleLine = true
    )
}
