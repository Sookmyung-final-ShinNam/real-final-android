package com.veryshinnam.myapp.feature.dashboard.ui

import android.content.pm.ActivityInfo
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.veryshinnam.myapp.R
import com.veryshinnam.myapp.common.component.BackButton
import com.veryshinnam.myapp.common.component.CircleIconButton
import com.veryshinnam.myapp.common.component.EmptyView
import com.veryshinnam.myapp.common.component.InstructionText
import com.veryshinnam.myapp.common.component.LoadErrorView
import com.veryshinnam.myapp.common.component.LogoBar
import com.veryshinnam.myapp.common.component.ManualStopButton
import com.veryshinnam.myapp.common.component.UserInfo
import com.veryshinnam.myapp.common.model.ManualState
import com.veryshinnam.myapp.core.orientation.OrientationManager
import com.veryshinnam.myapp.feature.dashboard.component.DashBoardStaticsCard
import com.veryshinnam.myapp.feature.dashboard.component.DashboardHelpButton
import com.veryshinnam.myapp.feature.dashboard.component.DashboardStoryCard
import com.veryshinnam.myapp.feature.dashboard.component.DashboardParentCard

/** 메인 대시보드 화면 */
@Composable
fun DashboardScreen(
    onBack: () -> Unit,
    onLogoClick: () -> Unit,
    goToCharacter: (Long) -> Unit,
    goToCreation: () -> Unit,
    onManualStop: () -> Unit,
    spacer: Dp = 6.dp,
    horizontalPadding: Dp = 16.dp,
    greenColor: Color = colorResource(R.color.deep_green),
    vm: DashboardViewModel = hiltViewModel(),
) {
    // 화면 밀도 정보
    val density = LocalDensity.current

    // -- 상태 구독
    val uiState by vm.uiState.collectAsStateWithLifecycle() // 화면 전체 ui
    val isEmpty by vm.isEmpty.collectAsStateWithLifecycle()
    val manualState by vm.manualState.collectAsStateWithLifecycle()
    val manualMessage by vm.manualMessage.collectAsStateWithLifecycle()
    val manualStep by vm.manualStep.collectAsStateWithLifecycle()
    val manualIndex by vm.manualIndex.collectAsStateWithLifecycle()

    // -- ui 변수
    val scrollState = rememberScrollState() // 대시보드 스크롤
    val logoAlpha by animateFloatAsState(
        targetValue = if (scrollState.value > 8) 0f else 1f,
        label = "logoAlpha"
    )   // 로고 애니메이션
    var logoHeight by remember { mutableStateOf(0f) }
    var bBTop by remember { mutableStateOf(0.dp) }   // 백버튼 높이 위치


    // 매뉴얼 > 강조할 좌표
    val isManual = manualState != ManualState.NONE
    val onStopManual: () -> Unit = { vm.clearManual(); onManualStop() }
    val onFinishManual: () -> Unit = { vm.finishManual(); onManualStop() }
    var tHelpRect by remember { mutableStateOf<Rect?>(null) }   // 테마 도움말 위치
    var bHelpRect by remember { mutableStateOf<Rect?>(null) }   // 배경 도움말 위치
    var sHelpRect by remember { mutableStateOf<Rect?>(null) }   // 동화 도움말 위치

    // -- 세로 모드 고정
    SideEffect {
        OrientationManager.setOrientation?.invoke(
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        )
    }

    // -- 초기화 설정
    LaunchedEffect(manualState) {
        when (manualState) {
            ManualState.NONE -> {
                vm.fetchDashboard()
            } // 실제 데이터
            ManualState.START -> {
                vm.startManual()
            } // 매뉴얼 시작일 때만 더미 데이터
            else -> {}
        }
    }

    LaunchedEffect(manualIndex) {
        if (manualIndex == vm.manuals.size) {
            vm.nextManualStep()
            onFinishManual()
        }
    }

    // -- 백핸들러 설정
    BackHandler {
        // 매뉴얼: 뒤로가기 차단
        if (isManual) {
            return@BackHandler
        }

        onBack() // 나머지
    }

    // 대시보드 ui
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.background_yellow))
    ) {
        // 로고 + 백버튼 ui
        Column (
            modifier = Modifier
                .fillMaxSize()
                .zIndex(if (isManual) 0f else 20f)
                .alpha(logoAlpha)
        ) {
            Column {
                Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
                LogoBar(
                    onLogoClick = onLogoClick,
                    modifier = Modifier.onGloballyPositioned {
                        logoHeight = it.size.height.toFloat()
                    }
                )
            }

            Box {
                // 뒤로 가기 버튼
                BackButton(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .zIndex(1f)
                        .onGloballyPositioned {
                            // px > dp
                            bBTop = with(density) { it.boundsInRoot().top.toDp() }
                        },
                    onBackClick = onBack
                )
            }

            if (isEmpty) {
                EmptyView(
                    emptyText = "대시보드가 비어 있네요!\n같이 대시보드를 채우러 가볼까요?",
                    buttonText = "동화 만들러 가기",
                    onButtonClick = goToCreation,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Column (
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (val state = uiState) {
                // 조회 로딩
                is DashboardUiState.Loading -> {
                    CircularProgressIndicator(
                        color = colorResource(id = R.color.main_orange), // 주황색
                        trackColor = Color.Gray.copy(alpha = 0.5f),
                        strokeWidth = 4.dp
                    )
                }

                // 조회 오류
                is DashboardUiState.Error -> {
                    LoadErrorView(
                        message ="대시보드를 불러올 수 없어요: ${state.message}",
                        onRetry = {  }
                    )
                }

                // 조회 성공
                is DashboardUiState.Success -> {
                    Column(
                        modifier = Modifier.fillMaxSize()
                            // 매뉴얼일 때 스크롤 제한
                            .verticalScroll(scrollState, enabled = !isManual),
                        verticalArrangement = Arrangement.Top
                    ) {
                        // 섹션 0: 대시보드 상단
                        Box(
                            modifier = Modifier.fillMaxWidth()
                                .then(
                                    // 매뉴얼일 때만 어두운 배경
                                    if (isManual)
                                        Modifier.background(Color.Black.copy(alpha = 0.5f))
                                    else Modifier
                                ),
                        ) {
                            UserInfo(
                                modifier = Modifier
                                    .padding(
                                        top = bBTop,
                                        start = horizontalPadding, end = horizontalPadding),
                                isItem = false, // 아이템 설명 존재
                                animalImage = painterResource(R.drawable.img_fox_cut),
                                screenText = "대시보드 설명:",
                                cardColor = greenColor,
                                cardText =  if (isManual) manualMessage
                                    else if (isEmpty) "아직 ${state.username}의\n최대 관심사를 찾을 수 없어요!"
                                    else "${state.username}의 최대 관심사예요! \n#${state.maxTheme} #${state.maxBackground}",
                                spanText = "#${state.maxTheme} #${state.maxBackground}",
                                spanColor = greenColor
                            )
                        }

                         if (!isEmpty) {
                            // 섹션 1: 테마 + 배경 통계 분석
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .then(
                                        // 매뉴얼일 때, 1, 2, 3 강조
                                        if (isManual && 0 < manualIndex && manualIndex < 4)
                                            Modifier.background(Color.Black.copy(alpha = 0.5f))
                                        else Modifier
                                    )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = spacer, start = horizontalPadding, end = horizontalPadding)
                                        .semantics{
                                            isTraversalGroup = true
                                        },
                                    horizontalArrangement = Arrangement.spacedBy(spacer)
                                ) {
                                    // 왼쪽 테마
                                    DashBoardStaticsCard(
                                        title = "주제",
                                        chartStats = state.themeChart,
                                        chartText = state.themeChartText,
                                        listStats = state.themeList,
                                        onHelpRect = {
                                            if (manualState == ManualState.START && tHelpRect == null) {
                                                tHelpRect = it } },
                                        modifier = Modifier
                                            .weight(1f)
                                            .semantics{
                                                isTraversalGroup = true
                                                traversalIndex = 0f
                                            }
                                    )

                                    // 오른쪽 배경
                                    DashBoardStaticsCard(
                                        title = "배경",
                                        chartStats = state.backgroundChart,
                                        chartText = state.backgroundChartText,
                                        listStats = state.backgroundList,
                                        onHelpRect = {
                                            if (manualState == ManualState.START && bHelpRect == null) {
                                                bHelpRect = it }},
                                        modifier = Modifier.weight(1f)
                                            .semantics{
                                                isTraversalGroup = true
                                                traversalIndex = 1f
                                            }
                                    )
                                }

                                // 매뉴얼일 때, 0, 3이상만 어두운 배경
                                if (isManual && (manualIndex == 0 || manualIndex >= 4)) {
                                    Box(
                                        modifier = Modifier
                                            .matchParentSize()
                                            .background(Color.Black.copy(alpha = 0.5f))
                                            .zIndex(1f)
                                    )
                                }
                            }

                            // 섹션 2: 스토리별 언어 + 단어 리스트 + 감정 분석
                            Box(
                                modifier = Modifier.fillMaxWidth()
                                    .then(
                                        // 매뉴얼일 때, 1, 2, 3 강조
                                        if (isManual && 0 < manualIndex && manualIndex < 4)
                                            Modifier.background(Color.Black.copy(alpha = 0.5f))
                                        else Modifier
                                    )
                                    .semantics{
                                        isTraversalGroup = true
                                    }
                            ){
                                // 이동 버튼
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 2.dp)
                                        .matchParentSize()
                                        .zIndex(10f)
                                        .semantics{
                                            traversalIndex = 0f
                                        },
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // 이전 버튼
                                    CircleIconButton(
                                        icon = Icons.Rounded.ChevronLeft,
                                        desc = "이전\n동화 분석",
                                        onClick = { vm.prevStory() },
                                        modifier = Modifier.height(88.dp)
                                            .semantics(true){
                                                contentDescription = "이전 동화 분석"
                                                role = Role.Button
                                                stateDescription = "현재 전체 ${state.storyAnalysis.size}개 중 ${state.storyIndex +1}번째 분석."
                                            },
                                        containerColor = colorResource(R.color.main_orange).copy(
                                            0.5f
                                        )
                                    )

                                    // 다음 버튼
                                    CircleIconButton(
                                        icon = Icons.Rounded.ChevronRight,
                                        desc = "다음\n동화 분석",
                                        onClick = { vm.nextStory() },
                                        modifier = Modifier.height(88.dp)
                                            .semantics(true){
                                                contentDescription = "다음 동화 분석"
                                                role = Role.Button
                                                stateDescription = "현재 전체 ${state.storyAnalysis.size}개 중 ${state.storyIndex +1}번째 분석."
                                            },
                                        containerColor = colorResource(R.color.main_orange).copy(
                                            0.5f
                                        )
                                    )
                                }

                                DashboardStoryCard(
                                    story = state.storyAnalysis[state.storyIndex],
                                    total = state.storyAnalysis.size,
                                    index = state.storyIndex +1,
                                    onStoryClick = { storyId ->
                                        goToCharacter(storyId)
                                    },
                                    onHelpRect = {
                                        if (manualState == ManualState.START && sHelpRect == null) {
                                            sHelpRect = it }},
                                    modifier = Modifier.semantics{
                                        traversalIndex = 1f
                                        isTraversalGroup = true
                                    },
                                )

                                // 매뉴얼일 때, 1, 2 강조
                                if (isManual && (manualIndex == 0 || manualIndex >= 4)) {
                                    Box(
                                        modifier = Modifier
                                            .matchParentSize()
                                            .background(Color.Black.copy(alpha = 0.5f))
                                            .zIndex(1f)
                                    )
                                }
                            }

                            // 섹션 3: 부모 조언 분석
                            DashboardParentCard(
                                username = state.username,
                                advice = state.advice,
                                modifier = Modifier
                            )

                            // 네비게이션바 만큼 여백
                            Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
                        }
                    }
                }
            }
        }

        // 매뉴얼 창 > 터치 가로챔
        if (isManual) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .then(
                        when (manualState) {
                            ManualState.START -> Modifier.pointerInput(Unit) {
                                detectTapGestures { vm.nextManualIndex() }
                            }
                            ManualState.STOP -> Modifier.pointerInput(Unit) {
                                detectTapGestures { onStopManual() }
                            }
                            else -> Modifier.pointerInput(Unit) {
                            }
                        }
                    )
                    .zIndex(10f)
                    .clearAndSetSemantics {
                        contentDescription = "\n아무 곳을 터치하세요. 현재 대시보드 화면 매뉴얼 진행" +
                                if (manualState != ManualState.STOP) "중. 전체 49 단계 중 $manualStep 단계."
                                else "중단."
                        stateDescription = manualMessage
                    }
            ) {
                // 다시 그릴 매뉴얼 강조 요소
                if (manualIndex == 4) {
                    tHelpRect?.let { rect ->
                        DashboardHelpButton(
                            onPress = { },
                            modifier = Modifier
                                .absoluteOffset(
                                    x = with(density) { rect.left.toDp() },
                                    y = with(density) { (rect.top + logoHeight).toDp()}
                                )
                                .zIndex(2f)
                        )
                    }

                    bHelpRect?.let { rect ->
                        DashboardHelpButton(
                            onPress = { },
                            modifier = Modifier
                                .absoluteOffset(
                                    x = with(density) { rect.left.toDp() },
                                    y = with(density) { (rect.top + logoHeight).toDp()}
                                )
                                .zIndex(2f)
                        )
                    }

                    sHelpRect?.let { rect ->
                        DashboardHelpButton(
                            onPress = { },
                            modifier = Modifier
                                .absoluteOffset(
                                    x = with(density) { rect.left.toDp() },
                                    y = with(density) { (rect.top + logoHeight).toDp()}
                                )
                                .zIndex(2f)
                        )
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