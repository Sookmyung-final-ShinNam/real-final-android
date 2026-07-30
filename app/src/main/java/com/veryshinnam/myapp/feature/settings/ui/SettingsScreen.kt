package com.veryshinnam.myapp.feature.settings.ui

import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.veryshinnam.myapp.R
import com.veryshinnam.myapp.common.component.LogoBar
import com.veryshinnam.myapp.common.component.BackButton
import com.veryshinnam.myapp.common.component.CircleButton
import com.veryshinnam.myapp.common.component.InstructionText
import com.veryshinnam.myapp.common.component.WarningConfirmSheet
import com.veryshinnam.myapp.common.component.WarningSheet
import com.veryshinnam.myapp.core.orientation.OrientationManager
import com.veryshinnam.myapp.core.session.ReviewToken
import com.veryshinnam.myapp.feature.settings.content.SettingsPrivacyContent
import kotlinx.coroutines.delay
import org.threeten.bp.LocalDateTime

/**
 * 설정 화면
 * : 로그아웃, 회원탈퇴, 앱 사용 매뉴얼 제공
 *
 * - onBack: 단순 뒤로 가기
 * - onLogoClick: 버튼 클릭 시, 홈 화면(HomeScreen)으로 이동
 * - horizontalPadding: 화면 가로 패딩
 * - buttonTextStyle: 버튼 공통 텍스트 스타일
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit, // 뒤로, 홈으로
    onLogoClick: () -> Unit,
    horizontalPadding: Dp = 16.dp,
    verticalPadding: Dp = 24.dp,
    footerTextStyle: TextStyle =  MaterialTheme.typography.bodySmall,
    vm: SettingsViewModel = hiltViewModel()
) {
    // 메일 관련 변수
    val context = LocalContext.current
    val email = "veryshinnam@gmail.com"
    val subjectText = "[문의합니다]"
    val bodyText = "문의 내용을 작성해 주세요."

    // 상태 구독
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val confirmWarningState by vm.confirmWarningState.collectAsStateWithLifecycle()
    val warningState by vm.warningState.collectAsStateWithLifecycle()

    val isEnabled =
        LocalDateTime.now().isAfter(
            LocalDateTime.parse(ReviewToken.REVIEW_EXPIRE_AT)
        )

    var isPrivacy by remember { mutableStateOf(false) } // 개인정보처리 방침 웹뷰 띄우기

    // 세로 모드 고정
    SideEffect {
        OrientationManager.setOrientation?.invoke(
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        )
    }

    // 에러 처리
    LaunchedEffect(uiState) {
        if (uiState is SettingsUiState.Error) {
            vm.showWarning("요청에 실패하셨습니다.")
        }
    }

    // 뒤로 가기
    BackHandler {
        when {
            isPrivacy -> isPrivacy = false // 개인정보처리 방침 화면인 경우
            else -> onBack() // 환경설정 화면인 경우
        }
    }

    // 설정 화면 UI
    when {
        isPrivacy -> {
            SettingsPrivacyContent() // 개인정보처리 방침 화면
        }
        else -> {
            Scaffold( // 환경설정 화면
                containerColor = colorResource(id = R.color.background_yellow),
                topBar = {
                    // 상태바 만큼 여백 & 상단 로고
                    Column {
                        Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
                        LogoBar(onLogoClick = onLogoClick)
                    }
                },
                bottomBar = {
                    // 네비게이션바 만큼 여백
                    Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
                }
            ) { innerPadding ->
                // --- 설정 화면 UI
                Column(
                    modifier = Modifier
                        .semantics {
                            traversalIndex = 0f
                        }
                        .fillMaxWidth()
                        .padding(innerPadding),
                    verticalArrangement = Arrangement.Center
                ) {
                    // 뒤로 가기 버튼
                    BackButton(
                        modifier = Modifier.zIndex(1f),
                        onBackClick = onBack
                    )

                    // --- 버튼 영역
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.66f)
                            .padding(horizontalPadding),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(4.dp, colorResource(R.color.main_orange)),
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(verticalPadding),
                            verticalArrangement = Arrangement.SpaceAround
                        ) {
                            // 로그아웃 버튼
                            CircleButton(
                                enabled = isEnabled,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .alpha(if (isEnabled) 1f else 0f),
                                onClick =  {
                                    vm.showConfirmWarning(
                                        warningText = "정말 로그아웃 하시겠어요?",
                                        confirmText = "로그아웃 하기",
                                        onConfirm = {
                                            vm.logout()
                                            vm.showWarning("로그아웃이 완료되었습니다.")
                                        }
                                    )
                                },
                                text = "로그아웃",
                                contentPadding = PaddingValues(vertical = horizontalPadding)
                            )

                            // 회원 탈퇴 버튼
                            CircleButton(
                                enabled = isEnabled,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .alpha(if (isEnabled) 1f else 0f),
                                onClick =  {
                                    vm.showConfirmWarning(
                                        warningText =
                                            "정말 탈퇴하시겠어요?\n\n" +
                                                    "동화/캐릭터 정보를 제외한 사용자 관련 모든 정보가 삭제되며,\n" +
                                                    "탈퇴 후 24시간 내에 재로그인시\n회원 탈퇴가 취소됩니다.",
                                        confirmText = "회원 탈퇴 하기",
                                        onConfirm = {
                                            vm.withdraw()
                                            vm.showWarning("회원 탈퇴가 완료되었습니다.")
                                        }
                                    )
                                },
                                text = "회원 탈퇴",
                                contentPadding = PaddingValues(vertical = horizontalPadding)
                            )

                            // 앱 사용 매뉴얼 버튼
                            CircleButton(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    vm.startManual()
                                    onBack()
                                },
                                text = "앱 사용 설명 다시 보기",
                                contentPadding = PaddingValues(vertical = horizontalPadding)
                            )

                            // 개인정보방침
                            CircleButton(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    isPrivacy = true
                                },
                                text = "개인정보처리 방침",
                                contentPadding = PaddingValues(vertical = horizontalPadding)
                            )

                            // 문의하기
                            CircleButton(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    try {
                                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                                            data = Uri.parse("mailto:")
                                            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
                                            putExtra(Intent.EXTRA_SUBJECT, subjectText)
                                            putExtra(Intent.EXTRA_TEXT, bodyText)
                                        }

                                        // resolveActivity: 사용 가능한 메일 앱 체크
                                        if (intent.resolveActivity(context.packageManager) != null) {
                                            context.startActivity(Intent.createChooser(intent, "문의 메일 보내기"))
                                        }
                                    } catch (_: Exception) {
                                        Toast.makeText(context, "메일을 보낼 수 있는 앱이 설치되어 있지 않습니다.", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                text = "메일 보내기",
                                contentPadding = PaddingValues(vertical = horizontalPadding)
                            )
                        }
                    }

                    // --- 다람쥐 이미지
                    Image(
                        painter = painterResource(R.drawable.img_llm_question),
                        contentDescription = null, // 장식용 이미지
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f) // 남은 공간 차지
                            .offset(y = (-20).dp)
                            .align(Alignment.CenterHorizontally), // 가로 정렬
                        contentScale = ContentScale.Fit
                    )

                    InstructionText(
                        text = "앱 사용 중 불편한 점을 발견하셨다면\n언제든지 저희에게 연락주세요!",
                        textStyle = footerTextStyle,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = horizontalPadding / 2)
                    )
                }
            }
        }
    }

    // 확인 버튼이 있는 경고창
    if (confirmWarningState.isVisible) {
        WarningConfirmSheet(
            warningText = confirmWarningState.warningText,
            confirmText = confirmWarningState.confirmText,
            onDismiss = { vm.hideConfirmWarning() },
            onConfirm = {
                confirmWarningState.onConfirm()
                vm.hideConfirmWarning()
            }
        )
    }

    // 단순 경고창
    if (warningState.isVisible) {

        // 3초 카운트다운
        var countdown by remember { mutableIntStateOf(3) }
        LaunchedEffect(warningState.isVisible) {
            if (warningState.isVisible) {
                for (i in 3 downTo 1) {
                    countdown = i
                    delay(1000)
                }
                vm.hideWarning()
            }
        }

        // 검은색 오버레이 + 경고창
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .pointerInput(Unit) {}
        ){
            WarningSheet(
                warningText = buildString {
                    append(warningState.warningText)
                    append("\n${countdown}초 뒤 시작 화면으로 이동합니다.")
                },
                onDismiss = { },    // 닫기 방지
                dismissible = false // 시트 닫힘 방지
            )
        }
    }
}