package com.veryshinnam.myapp.feature.creation.ui.conversation

import android.Manifest
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.view.accessibility.AccessibilityManager
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.veryshinnam.myapp.R
import com.veryshinnam.myapp.common.component.InstructionText
import com.veryshinnam.myapp.common.component.LogoBar
import com.veryshinnam.myapp.common.component.LoadErrorView
import com.veryshinnam.myapp.common.component.ManualStopButton
import com.veryshinnam.myapp.common.component.StepProgressBar
import com.veryshinnam.myapp.common.component.WarningConfirmSheet
import com.veryshinnam.myapp.common.model.ManualState
import com.veryshinnam.myapp.core.orientation.OrientationManager
import com.veryshinnam.myapp.feature.creation.content.conversation.ConversationEndContent
import com.veryshinnam.myapp.feature.creation.content.conversation.ConversationAnswerContent
import com.veryshinnam.myapp.feature.creation.content.conversation.ConversationFeedbackContent
import com.veryshinnam.myapp.feature.creation.content.conversation.ConversationQuestionContent
import com.veryshinnam.myapp.feature.creation.content.conversation.ConversationStoryContent
import com.veryshinnam.myapp.feature.creation.model.ConversationStep

// 캐릭터 생성 > 대화 진입점
@Composable
fun ConversationScreen(
    onHome: () -> Unit,
    goToNextManual: () -> Unit,
    horizontalPadding: Dp = 16.dp,
    vm: ConversationViewModel
) {
    val density = LocalDensity.current
    val context = LocalContext.current

    val recordAudioPermission = Manifest.permission.RECORD_AUDIO

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            Toast.makeText(context, "마이크 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
        }
    }

    // talkback 확인
    val accessibilityManager = remember {
            context.getSystemService(Context.ACCESSIBILITY_SERVICE)
                    as AccessibilityManager
        }
    val isTalkBack = accessibilityManager.isTouchExplorationEnabled

    // 상태 구독
    val uiState by vm.conversationUiState.collectAsStateWithLifecycle() // 대화 화면 상태 관리
    val isTtsSpeaking by vm.isTtsSpeaking.collectAsStateWithLifecycle() // tts 재생 상태 관리
    val manualState by vm.manualState.collectAsStateWithLifecycle()
    val manualMessage by vm.manualMessage.collectAsStateWithLifecycle()
    val manualStep by vm.manualStep.collectAsStateWithLifecycle()
    val manualIndex by vm.manualIndex.collectAsStateWithLifecycle()

    //  매뉴얼
    val isManual = manualState != ManualState.NONE
    val isManualStart = manualState == ManualState.START
    val isManualStop = manualState == ManualState.STOP
    val onStopManual: () -> Unit = { vm.clearManual(); onHome() }
    var logoHeight by remember { mutableStateOf(0.dp) }   // 로고바 높이

    // -- ui 변수
    var isWarning by remember { mutableStateOf(false) }   // 경고창
    val onReplayClick: () -> Unit = {
        when {
            isManualStart -> { vm.nextManualIndex() }
            isManualStop -> { onStopManual() }
            else -> { vm.startTts() }
        }
    }

    val onNextClick: () -> Unit = {
        when {
            isManualStart -> { vm.nextManualIndex() }
            isManualStop -> { onStopManual() }
            else -> { vm.goToNextStep() }
        }
    }

    val onRecordClick: () -> Unit = {   // 녹음 권한 여부

        // 녹음 권한 확인
        val granted = ContextCompat.checkSelfPermission(
            context,
            recordAudioPermission
        ) == PackageManager.PERMISSION_GRANTED

        when {
            isManualStop -> { onStopManual() }

            manualIndex == 5 -> {
                // manualStep 5 전용
                if (granted) vm.nextManualIndex()
                else launcher.launch(recordAudioPermission)
            }

            isManualStart -> { vm.nextManualIndex() }

            else -> {
                if (granted) vm.goToNextStep()
                else launcher.launch(recordAudioPermission)
            }
        }
    }

    // 세로 모드 고정
    SideEffect {
        OrientationManager.setOrientation?.invoke(
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        )
    }

    LaunchedEffect(manualIndex) {
        if (manualIndex == vm.manuals.size) {
            vm.nextManualStep() // 전역 단계 증가
            goToNextManual()
        }
    }

    // -- 백핸들러 설정
    BackHandler {
        // 매뉴얼: 뒤로가기 차단
        if (isManual) {
            return@BackHandler
        }

        // 대화 단계별 뒤로가기 처리
        when (uiState) {
            is ConversationUiState.Success -> {
                when ((uiState as ConversationUiState.Success).conversationStep) {
                    // 이야기 진행
                    ConversationStep.START,
                    ConversationStep.STORY -> {
                        isWarning = true // 경고창 on
                    }

                    // llm 질문, 사용자 답변
                    ConversationStep.QUESTION,
                    ConversationStep.ANSWER -> {
                        vm.goToPreviousStep() // 이전 단계
                    }

                    // llm 피드백
                    ConversationStep.FEEDBACK -> {
                        val feedback = (uiState as ConversationUiState.Success).feedbackData
                        if (feedback.isPositive) {
                            isWarning = true  // 긍정: 경고창 on
                        } else {
                            vm.goToPreviousStep()  // 부정: 이전 단계
                        }
                    }

                    // 대화 끝
                    ConversationStep.END -> {
                        onHome() // 홈으로
                    }
                }
            }

            else -> {
                onHome()
            }
        }
    }

    // screen ui
    Box(
        modifier = Modifier.fillMaxSize()
            .background(colorResource(R.color.background_yellow))
    ) {
        Column {
            // 1. 상태바 만큼 여백 & 상단 로고
            Box(
                Modifier.fillMaxWidth()
                    .onGloballyPositioned {
                        // px > dp 변환
                        logoHeight = with(density) { it.boundsInRoot().top.toDp() }
                    }
            ) {
                Column {
                    Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
                    LogoBar(
                        onLogoClick = {
                            if (!isManual) isWarning = true // 경고창
                        },
                        modifier = Modifier
                    )
                }

                if (isManual && (manualIndex == 0 || manualIndex == 1 || manualIndex == 5)) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(Color.Black.copy(alpha = 0.5f))
                            .zIndex(1f)
                    )
                }
            }

            // 2. content 별 화면
            Column(
                modifier = Modifier
                    .weight(1f) // 1, 3 제외 나머지 공간 차지
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (val state = uiState) {
                    // 조회 로딩 중
                    is ConversationUiState.Loading -> {
                        CircularProgressIndicator(
                            color = colorResource(id = R.color.main_orange), // 주황색
                            trackColor = Color.Gray.copy(alpha = 0.5f),
                            strokeWidth = 4.dp
                        )
                    }
                    // 조회 오류
                    is ConversationUiState.Error -> {
                        LoadErrorView(
                            message = state.message,
                            onRetry = { }
                        )
                    }
                    // 조회 성공
                    is ConversationUiState.Success -> {
                        LaunchedEffect(state.conversationStep) {
                            if (!isManual && !isTalkBack) {
                                vm.startTts() // isTalkBack 사용자는 자동 tts 재생 X
                            }
                        }

                        Column {
                            Box(modifier = Modifier.fillMaxWidth()) {
                                // 진행바 (START, END는 공간만 차지)
                                StepProgressBar(
                                    radius = 0.068f,
                                    line = 24f,
                                    steps = 4,                        // 총 반복 횟수
                                    currentStep = state.loopStep,     // 현재 진행 단계
                                    modifier = Modifier
                                        .padding(top= logoHeight)
                                        .fillMaxWidth(0.75f)  // 진행 바 길이
                                        .fillMaxHeight(0.15f)
                                        .zIndex(2f)
                                        .align(Alignment.TopCenter)
                                        .alpha(
                                            if (
                                                state.conversationStep != ConversationStep.START &&
                                                state.conversationStep != ConversationStep.END
                                            ) 1f else 0f
                                        )
                                )

                                if (isManual && (manualIndex == 0 || manualIndex == 1 || manualIndex == 5)) {
                                    Box(
                                        modifier = Modifier
                                            .matchParentSize()
                                            .background(Color.Black.copy(alpha = 0.5f))
                                            .zIndex(10f)
                                    )
                                }
                            }

                            Box (
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .then(
                                        // 매뉴얼일 때, 5 강조
                                        if (isManual && manualIndex == 0 || manualIndex == 1 || manualIndex == 5)
                                            Modifier.background(Color.Black.copy(alpha = 0.5f))
                                        else Modifier
                                    )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = horizontalPadding)
                                ) {
                                    when (state.conversationStep) {
                                        ConversationStep.START -> { // 대화 시작 (다음 이야기)
                                            ConversationStoryContent(
                                                nextStory = if (isManual) manualMessage else state.story,
                                                step = ConversationStep.START,
                                                isTtsSpeaking = isTtsSpeaking,
                                                onReplayClick = onReplayClick,
                                                onNextClick = onNextClick,
                                                nextEnabled = !isTtsSpeaking,
                                                modifier = Modifier
                                            )
                                        }

                                        ConversationStep.STORY -> { // 다음 이야기
                                            ConversationStoryContent(
                                                nextStory = if (isManual) manualMessage else state.story,
                                                step = ConversationStep.STORY,
                                                isTtsSpeaking = isTtsSpeaking,
                                                onReplayClick = onReplayClick,
                                                onNextClick = onNextClick,
                                                nextEnabled = !isTtsSpeaking,
                                                modifier = Modifier
                                            )
                                        }

                                        // llm 질문 (STORY 단계에서 이동 가능)
                                        ConversationStep.QUESTION -> {
                                            ConversationQuestionContent(
                                                question = if (isManual) manualMessage else state.questionData.question,
                                                isTtsSpeaking = isTtsSpeaking,
                                                onReplayClick = onReplayClick,
                                                onRecordClick = onRecordClick,
                                                nextEnabled = !isTtsSpeaking,
                                                modifier = Modifier
                                            )
                                        }

                                        ConversationStep.ANSWER -> { // 사용자 대답 (QUESTION 단계 이동 가능)
                                            LaunchedEffect(state.conversationStep) {
                                                vm.startStt(context)
                                            }

                                            ConversationAnswerContent(
                                                answerData = state.answerData,
                                                onRecordStop = { vm.stopStt() },
                                                onFeedback = onNextClick,
                                                modifier = Modifier.clearAndSetSemantics {
                                                    traversalIndex = 0f
                                                    contentDescription = "대답해 주세요!"
                                                }
                                            )
                                        }

                                        ConversationStep.FEEDBACK -> { // llm 피드백 (QUESTION 단계 이동 가능)
                                            ConversationFeedbackContent(
                                                feedback =  if (isManual) state.feedbackData.copy(text = manualMessage) else state.feedbackData,
                                                isTtsSpeaking = isTtsSpeaking,
                                                onReplayClick = onReplayClick,
                                                onButtonClick = {
                                                    when {
                                                        isManualStart -> { vm.nextManualIndex() }
                                                        isManualStop -> { onStopManual() }
                                                        else -> { vm.goFromFeedback() }
                                                    }
                                                }, // Answer 또는 Next Story(또는 종료)
                                                loopStep = state.loopStep,
                                                nextEnabled = !isTtsSpeaking,
                                                modifier = Modifier
                                            )
                                        }

                                        ConversationStep.END -> { // 대화 종료
                                            ConversationEndContent(
                                                { onHome() }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 3. 네비게이션바 만큼 여백
            Box(modifier = Modifier.fillMaxWidth()) {
                Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
                if (isManual && (manualIndex == 0 || manualIndex == 1 || manualIndex == 5)) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(Color.Black.copy(alpha = 0.5f))
                            .zIndex(1f)
                    )
                }
            }
        }

        // 매뉴얼일 때
        if (isManual) {
            Box(modifier = Modifier
                .fillMaxSize()
                .zIndex(10f)
                .then(
                    when {
                        // 사용자 답변: 대기
                        manualIndex == 6 -> Modifier.pointerInput(Unit) {
                            detectTapGestures { }
                        }

                        // 매뉴얼 진행 중
                        isManualStart -> Modifier.pointerInput(Unit) {
                            detectTapGestures { onRecordClick() }
                        }

                        isManualStop -> Modifier.pointerInput(Unit) {
                            detectTapGestures { onStopManual() }
                        }

                        else -> Modifier
                    }
                )
                .clearAndSetSemantics {
                    stateDescription = manualMessage
                    contentDescription = "\n아무 곳을 터치하세요. 현재 동화 생성 화면 매뉴얼 진행" +
                            if (manualState != ManualState.STOP) "중. 전체 49 단계 중 $manualStep 단계."
                            else "중단."
                }
            )

            // 중단 버튼
            ManualStopButton(
                onClick = {
                    when {
                        isManualStart -> { vm.stopManual() }
                        isManualStop -> { onStopManual() }
                        else -> {}
                    }
                },
                enabled = manualIndex != 6, // 사용자 답변: 비활성
                modifier = Modifier.zIndex(20f).align(Alignment.TopEnd)
            )

            // 전역 매뉴얼 진행 단계
            InstructionText(
                text = "- $manualStep / 50 -",
                textStyle = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .navigationBarsPadding()
                    .zIndex(50f)
                    .alpha(0.8f)
                     .align(Alignment.BottomCenter)
                    .padding(bottom = 2.dp)
                    .clearAndSetSemantics { }
            )
        }
    }

    if (isWarning) {
        WarningConfirmSheet(
            warningText  = "대화를 그만 진행할까요?\n지금까지 이야기한 내용은 저장되지 않아요!",
            confirmText = "그만하기",
            onDismiss = { isWarning = false },
            onConfirm = {
                isWarning = false
                onHome()
            }
        )
    }
}