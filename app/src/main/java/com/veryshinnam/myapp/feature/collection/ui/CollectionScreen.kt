package com.veryshinnam.myapp.feature.collection.ui

import android.content.pm.ActivityInfo
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.isTraversalGroup
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
import com.veryshinnam.myapp.common.component.EmptyView
import com.veryshinnam.myapp.common.component.FavoriteButton
import com.veryshinnam.myapp.common.component.InstructionText
import com.veryshinnam.myapp.common.component.LoadErrorView
import com.veryshinnam.myapp.common.component.ManualStopButton
import com.veryshinnam.myapp.common.component.StrokeTitle
import com.veryshinnam.myapp.common.component.TargetImage
import com.veryshinnam.myapp.common.component.TargetItem
import com.veryshinnam.myapp.common.component.TargetMessage
import com.veryshinnam.myapp.common.component.UserInfo
import com.veryshinnam.myapp.common.component.WarningSheet
import com.veryshinnam.myapp.common.model.Gender
import com.veryshinnam.myapp.common.model.ManualState
import com.veryshinnam.myapp.core.orientation.OrientationManager
import com.veryshinnam.myapp.feature.collection.component.CollectionCharacterGrid
import com.veryshinnam.myapp.feature.collection.component.CollectionFilterButtons

@Composable
fun CollectionScreen(
    onBack: () -> Unit,
    onItemClick: (Long) -> Unit,
    onLogoClick: () -> Unit,
    goToCreation: () -> Unit,
    goToNextManual: () -> Unit,
    onManualStop: () -> Unit,
    spacerPadding: Dp = 10.dp,
    horizontalPadding: Dp = 16.dp,
    vm: CollectionViewModel = hiltViewModel()
) {
    val density = LocalDensity.current

    // 상태 구독
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val isEmpty by vm.isEmpty.collectAsStateWithLifecycle()
    val manualState by vm.manualState.collectAsStateWithLifecycle()
    val manualMessage by vm.manualMessage.collectAsStateWithLifecycle()
    val manualStep by vm.manualStep.collectAsStateWithLifecycle()
    val manualIndex by vm.manualIndex.collectAsStateWithLifecycle()

    // 매뉴얼
    val isManual = manualState != ManualState.NONE
    val onStopManual: () -> Unit = { vm.clearManual(); onManualStop() }

    // 강조할 좌표
    var rabbitRect by remember { mutableStateOf<Rect?>(null) }      // 토끼 이미지
    var messageRect by remember { mutableStateOf<Rect?>(null) }     // 메세지 박스
    var itemRect by remember { mutableStateOf<Rect?>(null) }        // 아이템 박스
    var characterRect by remember { mutableStateOf<Rect?>(null) }   // 캐릭터 박스

    var isSimpleWarning by remember { mutableStateOf(false) } // 단순 경고창
    var SimpleWarningText by remember { mutableStateOf("") }

    // 세로 모드 고정
    SideEffect {
        OrientationManager.setOrientation?.invoke(
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        )
    }

    LaunchedEffect(manualState) {
        when (manualState) {
            ManualState.NONE -> {
                vm.fetchCollection(Gender.ALL)
            } // 실제 데이터
            ManualState.START -> {
                vm.startManual()
            } // 매뉴얼 시작일 때만 더미 데이터
            else -> {}
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

        // 경고창: 뒤로가기 차단
        if (isSimpleWarning) {
            return@BackHandler
        }

        onBack()
    }

    // 보관함 ui
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
                    // 조회 로딩
                    is CollectionUiState.Loading -> {
                        CircularProgressIndicator(
                            color = colorResource(id = R.color.main_orange), // 주황색
                            trackColor = Color.Gray.copy(alpha = 0.5f),
                            strokeWidth = 4.dp
                        )
                    }

                    // 조회 오류
                    is CollectionUiState.Error -> {
                        LoadErrorView(
                            message = state.message,
                            onRetry = {
                                val lastFilter = (vm.selectedFilter.value)
                                vm.selectFilter(lastFilter)
                            }
                        )
                    }

                    // 조회 성공
                    is CollectionUiState.Success -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = horizontalPadding)
                        ) {
                            // 보관함 상단
                            UserInfo(
                                modifier = Modifier.semantics {
                                    isTraversalGroup = true
                                },
                                isItem = true, // 아이템 설명 존재
                                itemCount = state.collectionDataList.size,
                                itemImage = painterResource(R.drawable.ic_character),
                                itemDescription = "보관함 캐릭터 수",
                                isCollection = true,
                                screenText = "보관함 설명:",
                                animalImage = painterResource(R.drawable.img_rabbit_cut),
                                cardColor = colorResource(R.color.blue_gray),
                                cardText = "지금까지 만든 캐릭터들이에요.\n좋아하는 캐릭터 5명을 표시해 주세요. 그러면 홈 화면에서 바로 만나 볼 수 있어요!",
                                spanText = "캐릭터 5명",
                                spanColor = colorResource(R.color.blue_sky),
                                onItemRect = { rect ->
                                    if (manualState == ManualState.START && itemRect == null) {
                                        itemRect = rect
                                    }
                                },
                                onAnimalRect = { rect ->
                                    if (manualState == ManualState.START && rabbitRect == null) {
                                        rabbitRect = rect
                                    }
                                },
                                onMessageRect = { rect ->
                                    if (manualState == ManualState.START && messageRect == null) {
                                        messageRect = rect
                                    }
                                }
                            )

                            // 캐릭터 성별 필터 버튼
                            Spacer(Modifier.height(spacerPadding))
                            CollectionFilterButtons(
                                selectedFilter = state.selectedFilter,
                                onFilterClick = { filter -> vm.selectFilter(filter) },
                                modifier = Modifier.align(Alignment.Start)
                            )

                            // 캐릭터 아이템 그리드 (3*3)
                            Spacer(Modifier.height(spacerPadding / 2))

                            if (isEmpty) { // 비어있는 경우 > 캐릭터 생성으로
                                EmptyView(
                                    emptyText = "보관함이 비어 있네요!\n같이 보관함을 채우러 가볼까요?",
                                    buttonText = "캐릭터 만들러 가기",
                                    onButtonClick = goToCreation,
                                    modifier = Modifier
                                )
                            } else { // 존재하는 경우
                                CollectionCharacterGrid(
                                    data = state.collectionDataList,
                                    onFavoriteClick = { id ->
                                        vm.updateFavorite(id) { text ->
                                            isSimpleWarning = true
                                            SimpleWarningText = text
                                        }
                                    },
                                    onItemClick = { id ->
                                        onItemClick(id)
                                    },
                                    cellPadding = spacerPadding / 2,
                                    modifier = Modifier.fillMaxSize(),
                                    onCharacterRect = { rect ->
                                        if (manualState == ManualState.START && characterRect == null) {
                                            characterRect = rect
                                        }
                                    },
                                )
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
                    .background(
                        Color.Black.copy(alpha = 0.5f)
                    )
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
                        contentDescription = "\n아무 곳을 터치하세요. 현재 보관함 화면 매뉴얼 진행" +
                                if (manualState != ManualState.STOP) "중. 전체 49 단계 중 $manualStep 단계."
                                else "중단."
                        stateDescription =
                            if (manualState != ManualState.STOP) " $manualMessage"
                            else manualMessage
                    }
            ) {
                rabbitRect?.let { rect ->
                    TargetImage(
                        rect,
                        painterResource(R.drawable.img_rabbit_cut)
                    )
                }

                messageRect?.let { rect ->
                    TargetMessage(
                        rect = rect,
                        message = manualMessage,
                        messageStyle = MaterialTheme.typography.titleMedium,
                        messagePadding = 16.dp,
                        boxColor = colorResource(R.color.blue_gray),
                    )
                }

                if (manualIndex >= 3) {
                    characterRect?.let { it ->
                        Box(
                            modifier = Modifier
                                .absoluteOffset(
                                    x = with(density) { it.left.toDp() },
                                    y = with(density) { it.top.toDp() }
                                )
                                .size(
                                    with(density) { it.width.toDp() },
                                    with(density) { it.height.toDp() }
                                )
                                .clip(RoundedCornerShape(16.dp))
                                .border(
                                    2.dp,
                                    colorResource(id = R.color.blue_gray),
                                    RoundedCornerShape(16.dp)
                                )
                        ) {
                            Image(
                                painter = painterResource(R.drawable.img_character_5),
                                contentDescription = "캐릭터 이미지",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop,
                            )
                            // 즐찾 버튼
                            FavoriteButton(
                                modifier = Modifier
                                    .fillMaxWidth(0.44f)
                                    .aspectRatio(1f)
                                    .align(Alignment.TopStart)
                                    .padding(4.dp),
                                characterId = -1,
                                isFavorite = true,
                                onFavoriteClick = {
                                    when (manualState) {
                                        ManualState.START -> { vm.nextManualIndex() }
                                        ManualState.STOP -> { onStopManual() }
                                        else -> {}
                                    }
                                }
                            )
                            // 캐릭터 이름
                            StrokeTitle(
                                titleText = "짱신남",
                                titleColor = Color.White,
                                strokeColor = Color.Black,
                                titleTextStyle = MaterialTheme.typography.titleLarge,
                                strokeWidth = 4f,
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 8.dp) // 아래 패딩
                            )
                        }
                    }
                }

                when (manualIndex) {
                    1 -> {
                        itemRect?.let {
                            TargetItem(
                                it, density,
                                image = painterResource(R.drawable.ic_character),
                                imageDescription = "캐릭터 수",
                                value = 5,
                                boxColor = colorResource(R.color.blue_gray)
                            )
                        }
                    }

                    2 -> {
                        characterRect?.let { it ->
                            Box(
                                modifier = Modifier
                                    .absoluteOffset(
                                        x = with(density) { it.left.toDp() },
                                        y = with(density) { it.top.toDp() }
                                    )
                                    .size(
                                        with(density) { it.width.toDp() },
                                        with(density) { it.height.toDp() }
                                    )
                                    .clip(RoundedCornerShape(16.dp))
                            ) {
                                // 즐찾 버튼
                                FavoriteButton(
                                    modifier = Modifier
                                        .fillMaxWidth(0.44f)
                                        .aspectRatio(1f)
                                        .align(Alignment.TopStart)
                                        .padding(4.dp)
                                        .zIndex(20f),
                                    characterId = -1,
                                    isFavorite = true,
                                    onFavoriteClick = {
                                        when (manualState) {
                                            ManualState.START -> { vm.nextManualIndex() }
                                            ManualState.STOP -> { onStopManual() }
                                            else -> {}
                                        }
                                    }
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

    if (isSimpleWarning) {
        WarningSheet(
            warningText = SimpleWarningText,
            onDismiss = { isSimpleWarning = false }
        )
    }
}