package com.veryshinnam.myapp.feature.home.ui

import android.content.pm.ActivityInfo
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.veryshinnam.myapp.R
import com.veryshinnam.myapp.common.component.CircleButton
import com.veryshinnam.myapp.common.component.InstructionText
import com.veryshinnam.myapp.common.component.TargetImage
import com.veryshinnam.myapp.common.component.TargetMessage
import com.veryshinnam.myapp.common.component.LogoBar
import com.veryshinnam.myapp.common.component.LoadErrorView
import com.veryshinnam.myapp.feature.home.component.HomeBottomButtons
import com.veryshinnam.myapp.feature.home.component.HomeFavoriteCarousel
import com.veryshinnam.myapp.common.component.UserItem
import com.veryshinnam.myapp.common.component.WarningSheet
import com.veryshinnam.myapp.common.model.ManualState
import com.veryshinnam.myapp.core.orientation.OrientationManager
import com.veryshinnam.myapp.feature.attendance.component.AttendanceReward

/**
 * 홈 화면
 * : 유저 정보와 즐찾 캐릭터 정보 조회
 *
 * - onSettingsClick: 버튼 클릭 시, 설정 화면(SettingsScreen)으로 이동
 * - onAttendanceClick: 버튼 클릭 시, 출석체크 화면(AttendanceScreen)으로 이동
 * - onCharacterClick: 버튼 클릭 시, 캐릭터 상세 화면(CharacterScreen)으로 이동
 * - onDashboardClick: 버튼 클릭 시, 대시보드 화면(DashboardScreen)으로 이동
 * - onCreationClick: 버튼 클릭 시, 캐릭터 및 동화 생성 플로우 진행
 * - onCollectionClick: 버튼 클릭 시, 보관함 화면(CollectionScreen)으로 이동
 */
@Composable
fun HomeScreen(
    onSettingsClick: () -> Unit,
    onAttendanceClick: () -> Unit,
    onCharacterClick: (Long) -> Unit,   // 캐릭터 아이디 전달
    onDashboardClick: () -> Unit,       // 바텀 버튼
    onCreationClick: () -> Unit,        // 바텀 버튼
    onCollectionClick: () -> Unit,      // 바텀 버튼
    messageBorder: Dp = 4.dp,
    messageCorner: Dp = 16.dp,
    messagePadding: Dp = 20.dp,
    horizontalPadding: Dp = 16.dp,
    bottomPadding: Dp = 10.dp,
    textStyle: TextStyle = MaterialTheme.typography.titleLarge.copy(fontWeight = Bold),
    settingsTextStyle: TextStyle =  MaterialTheme.typography.labelSmall.copy(color = colorResource(id = R.color.main_orange)),
    vm: HomeViewModel =  hiltViewModel()
) {
    // 상태 구독
    val uiState by vm.homeUiState.collectAsStateWithLifecycle() // 화면 전체 ui
    val isNewUser by vm.isNewUser.collectAsStateWithLifecycle() // 신규 유저 여부
    val warningState by vm.warningState.collectAsStateWithLifecycle() // 단순 경고
    val manualState by vm.manualState.collectAsStateWithLifecycle()
    val manualMessage by vm.manualMessage.collectAsStateWithLifecycle()
    val manualStep by vm.manualStep.collectAsStateWithLifecycle()
    val manualIndex by vm.manualIndex.collectAsStateWithLifecycle()
    val username by vm.username.collectAsStateWithLifecycle()

    // -- ui 변수
    // 이미지 높이: 기기 높이 * 0.12
    val imageHeight = (LocalConfiguration.current.screenHeightDp * 0.12f).dp

    // 매뉴얼 > 강조할 좌표
    val isManual = manualState != ManualState.NONE
    var squirrelRect by remember { mutableStateOf<Rect?>(null) } // 다람쥐 이미지
    var messageRect by remember { mutableStateOf<Rect?>(null) }  // 메세지 박스
    var creationRect by remember { mutableStateOf<Rect?>(null) }  // 생성
    var collectionRect by remember { mutableStateOf<Rect?>(null) }  // 보관함
    var dashboardRect by remember { mutableStateOf<Rect?>(null) }  // 대시보드
    var attendanceRect by remember { mutableStateOf<Rect?>(null) }  // 출석체크

    // 세로 모드 고정
    SideEffect {
        OrientationManager.setOrientation?.invoke(
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        )
    }

    // HomeScreen 진입 시 한번만 실행
    LaunchedEffect(Unit) {
        vm.reload()             // 홈 데이터 다시 불러오기
        vm.changeMessage()      // 랜덤 메시지도 갱신
    }

    // 매뉴얼 상태 잡기
    LaunchedEffect(manualState) {
        if (manualState == ManualState.START) {
            vm.loadFirstManual()
        }
    }

    // 다음 매뉴얼로 이동
    LaunchedEffect(manualIndex) {
        if (manualIndex == vm.firstManuals.size) {
            vm.nextManualStep()
            onCreationClick()
        }
    }

    // -- 백핸들러 설정
    // 매뉴얼 중이면 뒤로가기 차단
    if (isManual) {
        BackHandler {
            return@BackHandler
        }
    }

    Box {
        Scaffold(
            containerColor = colorResource(id = R.color.background_yellow),
            topBar = {
                // 상태바 만큼 여백 & 상단 로고
                Column {
                    Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
                    LogoBar(
                        modifier = Modifier.clearAndSetSemantics { } // 홈은 로고 대체 텍스트 제거
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
                contentAlignment = Alignment.Center
            ) {
                when (val state = uiState) {
                    // 조회 로딩
                    is HomeUiState.Loading -> {
                        CircularProgressIndicator(
                            color = colorResource(id = R.color.main_orange), // 주황색
                            trackColor = Color.Gray.copy(alpha = 0.5f),
                            strokeWidth = 4.dp
                        )
                    }

                    // 조회 오류
                    is HomeUiState.Error -> {
                        LoadErrorView(
                            message = state.message,
                            onRetry = { vm.reload() }
                        )
                    }

                    // 조회 성공
                    is HomeUiState.Success -> {
                        val (username, points, favorites) = state.homeData
                        val message = "반가워요, ${username}!\n${state.randomMessage}"

                        Column(
                            modifier = Modifier.fillMaxSize()
                                .semantics {
                                    isTraversalGroup = true
                                }
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(horizontal = horizontalPadding)
                                    .semantics {
                                        traversalIndex = 0f
                                        isTraversalGroup = true
                                    }
                            ) {
                                // 다람쥐 이미지 + 도토리 수
                                Box(
                                    modifier = Modifier.fillMaxWidth().height(imageHeight)
                                ) {
                                    // 다람쥐 이미지
                                    Image(
                                        painter = painterResource(R.drawable.img_home_squirrel),
                                        contentDescription = null, // 장식용 - 대체 텍스트 제거
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .align(Alignment.BottomStart)
                                            .padding(start = 26.dp)
                                            .onGloballyPositioned {
                                                if ((manualState == ManualState.REQUEST || manualState == ManualState.START || manualState == ManualState.FINISH)
                                                    && squirrelRect == null) {
                                                    squirrelRect = it.boundsInRoot()
                                                }
                                            },
                                        contentScale = ContentScale.Fit
                                    )

                                    // 도토리 수
                                    UserItem(
                                        painter = painterResource(R.drawable.ic_dotory),
                                        contentDesc = "모은 도토리 수",
                                        value = "$points",
                                        color = colorResource(R.color.main_orange),
                                        modifier = Modifier
                                            .padding(bottom = bottomPadding)
                                            .align(Alignment.BottomCenter)
                                            .fillMaxWidth(0.3f)
                                            .fillMaxHeight(0.6f)
                                    )
                                }

                                // 메시지 박스
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .wrapContentHeight()
                                        .background(Color.White, shape = RoundedCornerShape(messageCorner))
                                        .border(
                                            messageBorder,
                                            colorResource(R.color.main_orange),
                                            RoundedCornerShape(messageCorner))
                                        .onGloballyPositioned {
                                            if ((manualState == ManualState.REQUEST || manualState == ManualState.START || manualState == ManualState.FINISH)
                                                && messageRect == null) {
                                                messageRect = it.boundsInRoot()
                                            }
                                        }
                                        .semantics {
                                            traversalIndex = 1f
                                            isTraversalGroup = true
                                        }
                                        .padding(all = messagePadding),
                                    contentAlignment = Alignment.TopStart
                                ) {
                                    // 닉네임 + 랜덤 메시지
                                    Text(
                                        text = message,
                                        style = textStyle,
                                        modifier = Modifier.semantics {
                                            traversalIndex = 0f
                                            contentDescription = "사용자 메시지, $message"
                                        }
                                    )

                                    // 설정 버튼
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .height(IntrinsicSize.Min)
                                            .clickable(onClick = onSettingsClick)
                                            .align(Alignment.TopEnd)
                                            .semantics(true) {
                                                traversalIndex = 1f
                                                contentDescription = "설정"      // 자식 묶어서 대체 텍스트 설정
                                                role = Role.Button              // 버튼으로 인식
                                            }
                                    ) {
                                        Text(
                                            text = "설정",
                                            style = settingsTextStyle,
                                            modifier = Modifier.clearAndSetSemantics { } // 대체 텍스트 제거
                                        )

                                        Icon(
                                            imageVector = Icons.Default.Settings,
                                            contentDescription = null,  // 대체 텍스트 제거
                                            tint = colorResource(id = R.color.main_orange),
                                            modifier = Modifier
                                                .padding(start = 2.dp)
                                                .fillMaxHeight()
                                        )
                                    }
                                }
                            }

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .semantics {
                                        traversalIndex = 1f
                                        isTraversalGroup = true
                                    }
                                    .weight(1f, fill = true)
                            ) {
                                // 캐러셀 (남은 공간의 80%)
                                HomeFavoriteCarousel(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .semantics {
                                            traversalIndex = 0f
                                        }
                                        .weight(78f),
                                    characters = favorites,
                                    lastSelectedId = state.lastSelectedCharacter,
                                    onCharacterClick = { id ->
                                        vm.updateLastSelected(id)
                                        onCharacterClick(id)
                                    },
                                    onCharacterChanged = { id ->
                                        vm.updateLastSelected(id)
                                    }
                                )

                                // 바텀 버튼 (남은 공간의 22%)
                                HomeBottomButtons(
                                    onDashboardClick = onDashboardClick,
                                    onCreationClick = {
                                        if (points > 0) onCreationClick()
                                        else vm.showWarning("도토리가 부족해요.\n출석을 통해 도토리를 모아 볼까요?")
                                    },
                                    onCollectionClick = onCollectionClick,
                                    onDashboardRect = { rect ->
                                        if ((manualState == ManualState.START || manualState == ManualState.REQUEST) && dashboardRect == null) {
                                            dashboardRect = rect
                                        }
                                    },
                                    onCreationRect = { rect ->
                                        if ((manualState == ManualState.START || manualState == ManualState.REQUEST) && creationRect == null) {
                                            creationRect = rect
                                        }
                                    },
                                    onCollectionRect = { rect ->
                                        if ((manualState == ManualState.START || manualState == ManualState.REQUEST) && collectionRect == null) {
                                            collectionRect = rect
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(22f)
                                        .padding(vertical = 8.dp)
                                        .semantics {
                                            traversalIndex = 1f
                                        }
                                )
                            }
                        }

                        // 출석체크 버튼
                        Image(
                            painter = painterResource(R.drawable.img_home_check),
                            contentDescription = null, // 이미지 설명 제거
                            modifier = Modifier
                                .fillMaxHeight(0.094f)
                                .padding(horizontal = 16.dp)
                                .align(Alignment.TopEnd)
                                .onGloballyPositioned {
                                    if ((manualState == ManualState.START || manualState == ManualState.REQUEST) && attendanceRect == null) {
                                        attendanceRect = it.boundsInRoot()
                                    }
                                }
                                .clickable { onAttendanceClick() }
                                .semantics {            // 새 대체 텍스트 추가
                                    contentDescription = "출석 체크"
                                    role = Role.Button  // 버튼으로 인식
                                }
                        )
                    }
                }
            }
        }

        // 매뉴얼 진행
        if (isManual) {
            val displayStep =
                if (manualState == ManualState.FINISH) vm.getFinishStep()
                else manualStep

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(20f)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .then(
                        when (manualState) {
                            ManualState.REQUEST -> Modifier.pointerInput(Unit) {}
                            ManualState.START, ManualState.FINISH -> Modifier.pointerInput(Unit) {
                                detectTapGestures { vm.nextManualIndex() }
                            }
                            ManualState.STOP -> Modifier.pointerInput(Unit) {
                                detectTapGestures { vm.clearManual() }
                            }
                            else -> Modifier
                        }
                    )
                    .clearAndSetSemantics {
                        when (manualState) {
                            ManualState.START, ManualState.FINISH -> {
                                contentDescription =
                                    "아무 곳을 터치하세요. 현재 홈 화면 매뉴얼 진행 중. 전체 49단계 중 $displayStep 단계."
                                stateDescription =
                                    manualMessage.replace("{username}", username)
                            }

                            ManualState.REQUEST -> {
                                stateDescription = manualMessage
                            }

                            ManualState.STOP -> {
                                contentDescription = "아무 곳을 터치하세요."
                                stateDescription = manualMessage
                            }

                            else -> {}
                        }
                    }
            ) {
                squirrelRect?.let { rect ->
                    TargetImage(
                        rect,
                        painterResource(R.drawable.img_home_squirrel)
                    )  // zIndex 20
                }

                messageRect?.let { rect ->
                    TargetMessage(
                        rect = rect,
                        message = if (manualState == ManualState.STOP) "사용 방법은 홈 화면의 설정에서 언제든 다시 볼 수 있어요!"
                            else manualMessage.replace("{username}", username),
                        messageStyle = MaterialTheme.typography.titleSmall,
                    )
                }

                if (manualState == ManualState.START || manualState == ManualState.FINISH) {
                    // 전역 매뉴얼 진행 단계
                    InstructionText(
                        text = "- $displayStep / 50 -",
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

                if (manualState == ManualState.START) {
                    when (manualIndex) {
                        1, 2 -> creationRect?.let { TargetImage(it, painterResource(R.drawable.img_bottom_creation)) }
                        3 -> collectionRect?.let { TargetImage(it, painterResource(R.drawable.img_bottom_collection)) }
                        4, 5 -> dashboardRect?.let { TargetImage(it, painterResource(R.drawable.img_bottom_dashboard)) }
                        6 -> attendanceRect?.let { TargetImage(it, painterResource(R.drawable.img_home_check)) }
                    }
                }
            }

            if (manualState == ManualState.REQUEST) {
                Column(
                    modifier = Modifier.fillMaxSize()
                        .zIndex(30f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Spacer(modifier = Modifier.fillMaxHeight(0.7f))
                    Row(
                        modifier = Modifier.fillMaxWidth(0.85f),
                        Arrangement.SpaceBetween
                    ) {
                        CircleButton(
                            modifier = Modifier.weight(7f)
                                .clearAndSetSemantics {
                                    contentDescription = "매뉴얼 듣기"
                                    role = Role.Button
                                },
                            onClick = { vm.startManual() },
                            text = "좋아요!",
                            contentPadding = PaddingValues(vertical = horizontalPadding)
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        CircleButton(
                            modifier = Modifier.weight(7f)
                                .clearAndSetSemantics {
                                    contentDescription = "매뉴얼 생략"
                                    role = Role.Button
                                },
                            onClick = { vm.stopManual() },
                            text = "괜찮아요.",
                            contentPadding = PaddingValues(vertical = horizontalPadding)
                        )
                    }
                }
            }
        }
    }

    if (warningState.isVisible) {
        WarningSheet(
            warningText = warningState.warningText,
            onDismiss = { vm.hideWarning() }
        )
    }

    // 신규 유저 보상창
    if (isNewUser) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(2f)
                .background(Color.Black.copy(alpha = 0.5f))
                .pointerInput(Unit) {}
                .semantics {
                    contentDescription = "신규 유저 보상 도토리 5개"
                }
        )

        AttendanceReward(
            painter = painterResource(R.drawable.img_dotory_shining),
            text = "신규 유저 보상\n도토리 5개",
            onReceiveClick = {
                vm.updateNewUser()
                vm.requestManual() // 매뉴얼 진행
            },
            modifier = Modifier
                .fillMaxSize()
                .zIndex(30f)
        )
    }
}