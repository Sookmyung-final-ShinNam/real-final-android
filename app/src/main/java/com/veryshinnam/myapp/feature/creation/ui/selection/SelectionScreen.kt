package com.veryshinnam.myapp.feature.creation.ui.selection

import android.content.pm.ActivityInfo
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.veryshinnam.myapp.R
import com.veryshinnam.myapp.common.component.LogoBar
import com.veryshinnam.myapp.common.component.BackButton
import com.veryshinnam.myapp.common.component.InstructionText
import com.veryshinnam.myapp.common.component.ManualStopButton
import com.veryshinnam.myapp.common.component.StepProgressBar
import com.veryshinnam.myapp.common.component.TargetButton
import com.veryshinnam.myapp.common.component.TargetCustom
import com.veryshinnam.myapp.common.component.TargetImage
import com.veryshinnam.myapp.common.component.TargetMessage
import com.veryshinnam.myapp.common.component.TargetProgressBar
import com.veryshinnam.myapp.common.component.UserInfo
import com.veryshinnam.myapp.common.component.WarningConfirmSheet
import com.veryshinnam.myapp.common.component.WarningSheet
import com.veryshinnam.myapp.common.model.ManualState
import com.veryshinnam.myapp.core.orientation.OrientationManager
import com.veryshinnam.myapp.feature.creation.model.SelectionData
import com.veryshinnam.myapp.feature.creation.model.SelectionStep
import com.veryshinnam.myapp.feature.creation.content.selection.SelectionAgeContent
import com.veryshinnam.myapp.feature.creation.content.selection.SelectionBackgroundContent
import com.veryshinnam.myapp.feature.creation.content.selection.SelectionFaceContent
import com.veryshinnam.myapp.feature.creation.content.selection.SelectionGenderContent
import com.veryshinnam.myapp.feature.creation.content.selection.SelectionNameContent
import com.veryshinnam.myapp.feature.creation.content.selection.SelectionThemeContent

// 캐릭터 생성 > 선택 진입점
@Composable
fun SelectionScreen(
    onHome: () -> Unit,
    onFinish: (SelectionData) -> Unit,
    goToNextManual: () -> Unit,
    vm: SelectViewModel = hiltViewModel(),
    horizontalPadding: Dp = 16.dp,
    topPadding: Dp = 24.dp
) {
    // 상태 구독
    val uiState by vm.selectUiState.collectAsState()             // 선택 화면 상태 관리
    val selectionStep = uiState.selectionStep                    // 현재 선택 단계

    var isInputMode by remember { mutableStateOf(false) } // 테마+배경 직접추가 입력 모드
    var isWarning by remember { mutableStateOf(false) }   // 경고창
    var warningText by remember { mutableStateOf("") }
    var confirmText by remember { mutableStateOf("") }
    var confirmAction by remember { mutableStateOf<() -> Unit>({}) }
    var isSimpleWarning by remember { mutableStateOf(false) } // 단순 경고창
    var simpleWarningText by remember { mutableStateOf("") }

    val onWarning: () -> Unit = {
        warningText = "홈으로 돌아갈까요?\n지금까지 선택한 내역은 저장되지 않아요!"
        confirmText = "홈으로"
        confirmAction = { onHome() }
        isWarning = true
    }

    val manualState by vm.manualState.collectAsStateWithLifecycle()
    val manualMessage by vm.manualMessage.collectAsStateWithLifecycle()
    val manualStep by vm.manualStep.collectAsStateWithLifecycle()
    val manualIndex by vm.manualIndex.collectAsStateWithLifecycle()

    // 매뉴얼 > 강조할 좌표
    val isManual = manualState != ManualState.NONE
    val onStopManual: () -> Unit = { vm.clearManual(); onHome() }
    var squirrelRect by remember { mutableStateOf<Rect?>(null) } // 다람쥐 이미지
    var messageRect by remember { mutableStateOf<Rect?>(null) }  // 메세지 박스
    var progressRect by remember { mutableStateOf<Rect?>(null) } // 진행바
    var firstBRect by remember { mutableStateOf<Rect?>(null) } // 첫 테마 버튼
    var customBRect by remember { mutableStateOf<Rect?>(null) } // 직접 추가 버튼

    // 뒤로 가기 로직
    fun handleBack() {
        if (isInputMode) {
            isInputMode = false // 입력모드 해제
        } else {
            if (uiState.selectionStep == SelectionStep.THEME) {
                onWarning()
            } else {
                vm.goToPrevStep()
            }
        }
    }

    // 설명 텍스트 로직
    fun getInfoText(step: SelectionStep, isInputMode: Boolean): String {
        return if (isInputMode) {
            when (step) {
                SelectionStep.THEME -> "원하는 주제를 입력해 주세요!\n"
                SelectionStep.BACKGROUND -> "원하는 배경을 입력해 주세요!"
                else -> ""
            }
        } else {
            when (step) {
                SelectionStep.THEME -> "만들고 싶은 동화의 주제를 3개까지 골라 주세요!"
                SelectionStep.BACKGROUND -> "만들고 싶은 동화의 배경을 골라 주세요!"
                SelectionStep.GENDER -> "동화 속 주인공의 성별은 어떻게 할까요?"
                SelectionStep.AGE -> "동화 속 주인공의 나이는 어떻게 할까요?"
                SelectionStep.NAME -> "동화 속 주인공의 이름은 어떻게 할까요?"
                SelectionStep.FACE -> "동화 속 주인공의 모습은 어떻게 할까요?"
            }
        }
    }

    LaunchedEffect(uiState.selectionData.themes) {
        Log.d("SelectScreen", "현재 선택된 테마: ${uiState.selectionData.themes}")
    }
    LaunchedEffect(uiState.selectionData.background) {
        Log.d("SelectScreen", "현재 선택된 배경: ${uiState.selectionData.background}")
    }
    LaunchedEffect(uiState.selectionData.gender) {
        Log.d("SelectScreen", "현재 선택된 성별: ${uiState.selectionData.gender}")
    }
    LaunchedEffect(uiState.selectionData.age) {
        Log.d("SelectScreen", "현재 선택된 나이: ${uiState.selectionData.age}")
    }
    LaunchedEffect(uiState.selectionData.name) {
        Log.d("SelectScreen", "현재 선택된 이름: ${uiState.selectionData.name}")
    }
    LaunchedEffect(uiState.selectionData.hairStyle) {
        Log.d("SelectScreen", "현재 선택된 눈색: ${uiState.selectionData.eyeColor}\n현재 선택된 머리색: ${uiState.selectionData.hairColor}\n현재 선택된 머리 스타일: ${uiState.selectionData.hairStyle}")
    }

    // 세로 모드
    SideEffect {
        OrientationManager.setOrientation?.invoke(
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        )
    }

    LaunchedEffect(manualState) {
        if (manualState == ManualState.START && manualIndex == 0) {
            vm.startManual()
        }
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

        handleBack() // 그 외
    }

    // ui 화면
    Box {
        Scaffold(
            containerColor = colorResource(id = R.color.background_yellow),
            topBar = {
                // 상태바 만큼 여백 & 상단 로고
                Column {
                    Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
                    LogoBar(
                        onLogoClick = {
                            if (!isManual) { onWarning() } // 경고창
                        }
                    )
                }
            },
            bottomBar = {
                // 네비게이션바 만큼 여백
                Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.TopStart
            ) {
                // 뒤로 가기 버튼
                BackButton(
                    onBackClick = { handleBack() },
                    modifier = Modifier.align(Alignment.TopStart)
                )

                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(horizontal = horizontalPadding),
                ) {
                    Box{
                        // 진행바
                        StepProgressBar(
                            steps = 6,
                            currentStep = uiState.currentStep,
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .fillMaxWidth(0.7f)
                                .fillMaxHeight(0.2f)
                                .offset(x = 12.dp, y = (-20).dp)
                                .onGloballyPositioned {
                                    if (manualState == ManualState.START  && progressRect == null) {
                                        progressRect = it.boundsInRoot()
                                    }
                                }
                                .zIndex(1f)
                        )

                        UserInfo(
                            modifier = Modifier.align(Alignment.TopStart),
                            animalImage = painterResource(R.drawable.img_squirrel_cut),
                            screenText = "단계 설명:",
                            cardColor = colorResource(R.color.main_orange),
                            cardText =  getInfoText(selectionStep, isInputMode),
                            onAnimalRect = { rect ->
                                if (manualState == ManualState.START && squirrelRect == null) {
                                    squirrelRect = rect
                                }
                            },
                            onMessageRect = { rect ->
                                if (manualState == ManualState.START && messageRect == null) {
                                    messageRect = rect
                                }
                            }
                        )
                    }

                    // 단계별 Content 분기
                    when (selectionStep) {
                        SelectionStep.THEME -> {
                            SelectionThemeContent(
                                themes = uiState.selectionData.themes,
                                customTheme = uiState.selectionData.customTheme,
                                isInputMode = isInputMode,
                                onInputModeChange = { isInputMode = it },
                                onCustomThemeInput = { vm.addCustomTheme(it) },
                                onThemeSelect = { vm.selectTheme(it) },
                                onNextClick = { vm.goToNextStep() },
                                onSimpleWarning = { text ->  // 경고 문구
                                    simpleWarningText = text
                                    isSimpleWarning = true
                                },
                                onFirstBRect = { rect ->
                                    if (manualState == ManualState.START && firstBRect == null) {
                                        firstBRect = rect
                                    }
                                },
                                onCustomBRect = { rect ->
                                    if (manualState == ManualState.START && customBRect == null) {
                                        customBRect = rect
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = topPadding)
                                    .weight(1f)
                            )
                        }

                        SelectionStep.BACKGROUND -> {
                            SelectionBackgroundContent(
                                background = uiState.selectionData.background,
                                customBackground = uiState.selectionData.customBackground,
                                isInputMode = isInputMode,
                                onInputModeChange = { isInputMode = it },
                                onCustomBackgroundInput = { vm.addCustomBackground(it) },
                                onBackgroundSelect = { vm.selectBackground(it) },
                                onPrevClick = { vm.goToPrevStep() },
                                onNextClick = { vm.goToNextStep() },
                                onSimpleWarning = { text ->  // 경고 문구
                                    simpleWarningText = text
                                    isSimpleWarning = true
                                },
                                modifier = Modifier.fillMaxWidth()
                                    .padding(top = topPadding)
                                    .weight(1f)
                            )
                        }

                        SelectionStep.GENDER -> {
                            SelectionGenderContent(
                                gender = uiState.selectionData.gender,
                                onSelectGender = { vm.selectGender(it) },
                                onPrevClick = { vm.goToPrevStep() },
                                onNextClick = { vm.goToNextStep() },
                                onSimpleWarning = { text ->  // 경고 문구
                                    simpleWarningText = text
                                    isSimpleWarning = true
                                },
                                modifier = Modifier.fillMaxWidth()
                                    .padding(top = topPadding)
                                    .weight(1f)
                            )
                        }

                        SelectionStep.AGE -> {
                            SelectionAgeContent(
                                age = uiState.selectionData.age,
                                listState = vm.ageListState,
                                flingBehavior = rememberSnapFlingBehavior(vm.ageListState),
                                onIncreaseAge = { vm.increaseAge() },
                                onDecreaseAge = { vm.decreaseAge() },
                                onSelectAge = { vm.selectAge(it) },
                                onPrevClick = { vm.goToPrevStep() },
                                onNextClick = { vm.goToNextStep() },
                                onSimpleWarning = { text ->  // 경고 문구
                                    simpleWarningText = text
                                    isSimpleWarning = true
                                },
                                modifier = Modifier.fillMaxWidth()
                                    .padding(top = topPadding)
                                    .weight(1f)
                            )
                        }

                        SelectionStep.NAME -> {
                            SelectionNameContent(
                                name = uiState.selectionData.name,
                                onNameChange = { vm.selectName(it) },
                                onPrevClick = { vm.goToPrevStep() },
                                onNextClick = { vm.goToNextStep() },
                                onSimpleWarning = { text ->  // 경고 문구
                                    simpleWarningText = text
                                    isSimpleWarning = true
                                },
                                modifier = Modifier.fillMaxWidth()
                                    .padding(top = topPadding)
                                    .weight(1f)
                            )
                        }

                        SelectionStep.FACE -> {
                            SelectionFaceContent(
                                eyeColorIndex = uiState.eyeColorIndex,
                                eyeColorPage = uiState.eyeColorPage,
                                hairColorIndex = uiState.hairColorIndex,
                                hairColorPage = uiState.hairColorPage,
                                hairStyle = uiState.selectionData.hairStyle,
                                onSelectEyeColor = { value, index, page ->
                                    vm.selectEyeColor(value, index, page) },
                                onSelectHairColor = { value, index, page ->
                                    vm.selectHairColor(value, index, page) },
                                onSelectHairStyle = { vm.selectHairStyle(it) },
                                onPrevClick = { vm.goToPrevStep() },
                                onSimpleWarning = { text ->  // 경고 문구
                                    simpleWarningText = text
                                    isSimpleWarning = true
                                },
                                onWarning = {  wText, cText -> // 이야기 시작 직전
                                    warningText = wText
                                    confirmText = cText
                                    confirmAction = { onFinish(uiState.selectionData) }
                                    isWarning = true
                                },
                                modifier = Modifier.fillMaxWidth()
                                    .padding(top = topPadding)
                                    .weight(1f)
                            )
                        }
                    }
                }
            }
        }

        if (isWarning) {
            WarningConfirmSheet(
                warningText  = warningText,
                confirmText = confirmText,
                onDismiss = { isWarning = false },
                onConfirm = {
                    isWarning = false
                    confirmAction()
                }
            )
        }

        if (isSimpleWarning) {
            WarningSheet(
                warningText = simpleWarningText,
                onDismiss = { isSimpleWarning = false }
            )
        }

        // 매뉴얼 진행
        if (isManual) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(2f)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .then(
                        when (manualState) {
                            ManualState.START -> Modifier.pointerInput(Unit) {
                                detectTapGestures { vm.nextManualIndex() }
                            }

                            ManualState.STOP -> Modifier.pointerInput(Unit) {
                                detectTapGestures { onStopManual() }
                            }

                            else -> Modifier
                        }
                    )
                    .clearAndSetSemantics {
                        contentDescription = "\n아무 곳을 터치하세요. 현재 동화와 캐릭터 선택 화면 매뉴얼 진행" +
                                if (manualState != ManualState.STOP) "중. 전체 49 단계 중 $manualStep 단계."
                                else "중단."
                        stateDescription = manualMessage
                    }
            ) {
                squirrelRect?.let { rect ->
                    TargetImage(
                        rect,
                        painterResource(R.drawable.img_squirrel_cut)
                    )
                }

                messageRect?.let { rect ->
                    TargetMessage(
                        rect = rect,
                        message = if (manualState == ManualState.START) manualMessage
                        else "사용 방법은 홈 화면의 설정에서 언제든 다시 볼 수 있어요!",
                        messageStyle = MaterialTheme.typography.titleSmall,
                        messagePadding = 16.dp
                    )
                }

                if (manualState == ManualState.START) {

                    when (manualIndex) {
                        1 -> progressRect?.let { TargetProgressBar(it, 6) }
                        2 -> firstBRect?.let { TargetButton(it, onButtonClick = {vm.nextManualIndex()}) }
                        3 -> customBRect?.let { TargetCustom(it, onCustomClick = {vm.nextManualIndex()})  }
                    }
                }
            }

            // 중단 버튼
            ManualStopButton(
                onClick = {
                    when (manualState) {
                        ManualState.START -> { vm.stopManual() }
                        ManualState.STOP -> { onStopManual() }
                        else -> {}
                    }
                },
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
}