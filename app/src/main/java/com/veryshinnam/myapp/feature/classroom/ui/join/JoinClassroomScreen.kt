package com.veryshinnam.myapp.feature.classroom.ui.join

import android.content.pm.ActivityInfo
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.input.KeyboardCapitalization
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
fun JoinClassroomScreen(
    onBack: () -> Unit,
    onSuccess: () -> Unit,
    vm: JoinClassroomViewModel = hiltViewModel()
) {
    val state by vm.uiState.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current
    var code by remember { mutableStateOf("") }

    SideEffect { OrientationManager.setOrientation?.invoke(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) }

    LaunchedEffect(state.isSuccess) { if (state.isSuccess) onSuccess() }

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
                modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(20.dp))
                        .border(2.dp, colorResource(R.color.main_orange), RoundedCornerShape(20.dp))
                        .padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "학급 코드를 입력해주세요",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        "선생님께 학급 코드를 받아서 입력하면\n선생님 승인 후 학급에 참여할 수 있어요!",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(4.dp))
                    OutlinedTextField(
                        value = code,
                        onValueChange = { if (it.length <= 8) code = it.uppercase() },
                        placeholder = { Text("코드 8자리", color = Color.LightGray) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        textStyle = MaterialTheme.typography.headlineSmall.copy(
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            color = colorResource(R.color.main_orange),
                            letterSpacing = androidx.compose.ui.unit.TextUnit(4f, androidx.compose.ui.unit.TextUnitType.Sp)
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colorResource(R.color.main_orange),
                            unfocusedBorderColor = colorResource(R.color.main_orange_50),
                            cursorColor = colorResource(R.color.main_orange)
                        ),
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Characters,
                            imeAction = ImeAction.Done
                        ),
                        singleLine = true
                    )
                    Spacer(Modifier.height(4.dp))
                    CircleButton(
                        text = if (state.isLoading) "요청 중..." else "가입 신청하기",
                        onClick = {
                            focusManager.clearFocus()
                            if (code.length == 8 && !state.isLoading) vm.joinClassroom(code)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            BackButton(
                modifier = Modifier.align(Alignment.TopStart).padding(start = 16.dp, top = 8.dp),
                onBackClick = onBack
            )
        }
    }

    if (state.errorMessage != null) {
        WarningSheet(warningText = state.errorMessage!!, onDismiss = { vm.clearError() })
    }
}
