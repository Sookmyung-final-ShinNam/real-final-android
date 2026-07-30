package com.veryshinnam.myapp.feature.attendance.ui

import android.content.pm.ActivityInfo
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
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
import com.veryshinnam.myapp.common.component.LoadErrorView
import com.veryshinnam.myapp.common.component.ManualStopButton
import com.veryshinnam.myapp.common.component.TargetImage
import com.veryshinnam.myapp.common.component.TargetItem
import com.veryshinnam.myapp.common.component.TargetMessage
import com.veryshinnam.myapp.common.component.UserInfo
import com.veryshinnam.myapp.common.model.ManualState
import com.veryshinnam.myapp.core.orientation.OrientationManager
import com.veryshinnam.myapp.feature.attendance.component.AttendanceCalender
import com.veryshinnam.myapp.feature.attendance.component.AttendanceReward
import org.threeten.bp.YearMonth

@Composable
fun AttendanceScreen(
    onBack: () -> Unit,
    onLogoClick: () -> Unit,
    goToNextManual: () -> Unit,
    onManualStop: () -> Unit,
    spacerPadding: Dp = 10.dp,
    horizontalPadding: Dp = 16.dp,
    vm: AttendanceViewModel = hiltViewModel()
) {
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current

    // 상태 구독
    val uiState by vm.attendanceUiState.collectAsStateWithLifecycle()
    val isLoading by vm.isLoading.collectAsStateWithLifecycle()
    val manualState by vm.manualState.collectAsStateWithLifecycle()
    val manualMessage by vm.manualMessage.collectAsStateWithLifecycle()
    val manualStep by vm.manualStep.collectAsStateWithLifecycle()
    val manualIndex by vm.manualIndex.collectAsStateWithLifecycle()

    // ui 변수
    var isTodayStamp by remember { mutableStateOf(false) }
    var isExchangeable by remember { mutableStateOf(false) }

    // 매뉴얼 > 강조할 좌표
    val isManual = manualState != ManualState.NONE
    val onStopManual: () -> Unit = { vm.clearManual(); onManualStop() }
    var pigRect by remember { mutableStateOf<Rect?>(null) } // 돼지 이미지
    var messageRect by remember { mutableStateOf<Rect?>(null) }  // 메세지 박스
    var itemRect by remember { mutableStateOf<Rect?>(null) } // 메세지 박스
    var calendarRect by remember { mutableStateOf<Rect?>(null) } // 메세지 박스

    // 세로 모드 고정
    SideEffect {
        OrientationManager.setOrientation?.invoke(
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        )
    }

    // 캐릭터 id 바뀌면 재로딩
    LaunchedEffect(manualState) {
        when (manualState) {
            ManualState.NONE -> {
                val now = YearMonth.now()
                vm.fetchAttendance(yearMonth = now)
            }
            ManualState.START -> { vm.startManual() } // 매뉴얼 시작일 때만 더미 데이터
            else -> { }
        }
    }

    // 화면 가로 > 세로 이동 후, 매뉴얼 변수 초기화
    LaunchedEffect(configuration.orientation) {
        pigRect = null
        messageRect = null
        itemRect = null
        calendarRect = null
    }

    LaunchedEffect(manualIndex) {
        if (manualIndex == vm.manuals.size) {
            vm.nextManualStep()
            goToNextManual()
        }
    }

    // -- 백핸들러 설정
    BackHandler {
        // 매뉴얼: 뒤로가기 차단
        if (isManual) {
            return@BackHandler
        }

        onBack()
    }

    Box {
        Scaffold(
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
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                // 뒤로 가기 버튼
                BackButton(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .zIndex(1f),
                    onBackClick = onBack
                )

                when (val state = uiState) {
                    // 조회 로딩 중
                    is AttendanceUiState.Idle -> {
                        CircularProgressIndicator(
                            color = colorResource(id = R.color.main_orange), // 주황색
                            trackColor = Color.Gray.copy(alpha = 0.5f),
                            strokeWidth = 4.dp
                        )
                    }

                    // 조회 오류
                    is AttendanceUiState.Error -> {
                        LoadErrorView(
                            message = state.message,
                            onRetry = { }
                        )
                    }

                    // 조회 성공
                    is AttendanceUiState.Success -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = horizontalPadding)
                        ) {
                            // 오늘 출첵 여부 확인
                            LaunchedEffect(state.attendanceData.isTodayAttendance) {
                                isTodayStamp = !state.attendanceData.isTodayAttendance
                            }

                            // 스탬프 수 확인
                            LaunchedEffect(state.attendanceData.stamps, isTodayStamp) {
                                if (!isTodayStamp && state.attendanceData.stamps == 10 && !isExchangeable) {
                                    vm.exchangeAttendance()
                                    isExchangeable = true
                                }
                            }

                            val month = if (state.yearMonth == YearMonth.now()) "이번 달"
                            else "지난 ${state.yearMonth.monthValue}월"

                            UserInfo(
                                modifier = Modifier,
                                isItem = true, // 아이템 설명 존재
                                itemCount = state.attendanceData.stamps,
                                itemImage =  painterResource(R.drawable.ic_stamp),
                                itemDescription = "모은 도장 수",
                                screenText = "출석체크 설명:",
                                animalImage = painterResource(R.drawable.img_pig_cut),
                                cardColor = colorResource(R.color.deep_pink),
                                cardText = "${month}은 총 ${state.attendanceData.attendanceCounts}번 출석했어요!\n" +
                                        "도장 10 개당 도토리 1 개라는 걸 잊지 마세요~!",
                                spanText = "${state.attendanceData.attendanceCounts}번",
                                spanColor = colorResource(R.color.light_pink),
                                onItemRect = { rect ->
                                    if (manualState == ManualState.START && itemRect == null) {
                                        itemRect = rect
                                    }
                                },
                                onAnimalRect = { rect ->
                                    if (manualState == ManualState.START  && pigRect == null) {
                                        pigRect = rect
                                    }
                                },
                                onMessageRect = { rect ->
                                    if (manualState == ManualState.START && messageRect == null) {
                                        messageRect = rect
                                    }
                                }
                            )

                            Spacer(Modifier.height(spacerPadding))

                            // 출첵 달력
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxSize()
                                    .zIndex(2f),
                                contentAlignment = Alignment.Center
                            ) {
                                AttendanceCalender(
                                    attendances = state.attendanceData.attendanceDates,
                                    yearMonth = state.yearMonth,
                                    lastExchangeDate = state.attendanceData.lastExchangeDate,
                                    onPrevMonth = { vm.fetchPreviousMonth() },
                                    onNextMonth = { vm.fetchNextMonth() },
                                    modifier = Modifier.fillMaxSize()
                                        .onGloballyPositioned { it ->
                                            if (manualState == ManualState.START && calendarRect == null) {
                                                calendarRect = it.boundsInRoot()
                                            }
                                        }
                                )

                                if (isLoading) {
                                    CircularProgressIndicator(
                                        color = colorResource(id = R.color.deep_pink),
                                        trackColor = Color.Gray.copy(alpha = 0.5f),
                                        strokeWidth = 4.dp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

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
                        contentDescription = "\n아무 곳을 터치하세요. 현재 출석체크 화면 매뉴얼 진행" +
                                if (manualState != ManualState.STOP) "중. 전체 49 단계 중 $manualStep 단계."
                                else "중단."
                        stateDescription = manualMessage
                    }
            ) {
                pigRect?.let { rect ->
                    TargetImage(
                        rect,
                        painterResource(R.drawable.img_pig_cut)
                    )
                }

                messageRect?.let { rect ->
                    TargetMessage(
                        rect = rect,
                        message = manualMessage,
                        messageStyle = MaterialTheme.typography.titleMedium,
                        messagePadding = 16.dp,
                        boxColor = colorResource(R.color.deep_pink),
                    )
                }

                if (manualIndex >= 3) {
                    itemRect?.let {
                        TargetItem(
                            it, density,
                            image = painterResource(R.drawable.ic_stamp),
                            imageDescription = "도장 수",
                            value = 5,
                            boxColor = colorResource(R.color.deep_pink)
                        )
                    }
                    if (manualIndex >= 5) {
                        calendarRect?.let { rect ->
                            Box(
                                modifier = Modifier
                                    .absoluteOffset(
                                        x = with(density) { rect.left.toDp() },
                                        y = with(density) { rect.top.toDp() }
                                    )
                                    .size(
                                        with(density) { rect.width.toDp() },
                                        with(density) { rect.height.toDp() }
                                    )
                                    .zIndex(20f)
                            ) {
                                AttendanceCalender(
                                    attendances = vm.manualAttendances,
                                    yearMonth =YearMonth.from(vm.today),
                                    lastExchangeDate = vm.manualDate,
                                    onPrevMonth = {
                                        when (manualState) {
                                            ManualState.START -> { vm.nextManualIndex() }
                                            ManualState.STOP -> { onStopManual() }
                                            else -> {}
                                        }
                                    },
                                    onNextMonth = { },
                                    isManual = true,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                }
                when (manualIndex) {
                    2 -> {
                        calendarRect?.let { rect ->
                            Box(
                                modifier = Modifier
                                    .absoluteOffset(
                                        x = with(density) { rect.left.toDp() },
                                        y = with(density) { rect.top.toDp() }
                                    )
                                    .size(
                                        with(density) { rect.width.toDp() },
                                        with(density) { rect.height.toDp() }
                                    )
                                    .zIndex(20f)
                            ) {
                                AttendanceCalender(
                                    attendances = vm.manualAttendances,
                                    yearMonth =YearMonth.from(vm.today),
                                    lastExchangeDate = vm.manualDate,
                                    onPrevMonth = {
                                        when (manualState) {
                                            ManualState.START -> { vm.nextManualIndex() }
                                            ManualState.STOP -> { onStopManual() }
                                            else -> {}
                                        }
                                    },
                                    onNextMonth = { },
                                    isManual = true,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
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

    // 오늘 출첵 화면
    if (isTodayStamp) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(2f)
                .background(Color.Black.copy(alpha = 0.5f))
                .pointerInput(Unit) { } // 터치 빼앗음
                .semantics {
                    contentDescription = "오늘의 출석 체크 완료"
                }
        )

        AttendanceReward(
            painter = painterResource(R.drawable.img_stamp_shining_blue),
            text = "오늘의\n출석 체크 완료",
            buttonText = "확인",
            onReceiveClick = {
                vm.addAttendance()
                isTodayStamp = false
            },
            modifier = Modifier
                .fillMaxSize()
                .zIndex(3f),
        )
    }

    // 출첵 보상 완료 화면
    if (isExchangeable) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(2f)
                .background(Color.Black.copy(alpha = 0.5f))
                .pointerInput(Unit) { } // 터치 빼앗음
                .semantics {
                    contentDescription = "출석 체크 보상 도토리 1개"
                }
        )

        AttendanceReward(
            painter = painterResource(R.drawable.img_dotory_shining),
            text = "출석 체크 보상\n도토리 1개",
            onReceiveClick = {
                isExchangeable = false // 닫기
            },
            modifier = Modifier
                .fillMaxSize()
                .zIndex(2f)
        )
    }
}