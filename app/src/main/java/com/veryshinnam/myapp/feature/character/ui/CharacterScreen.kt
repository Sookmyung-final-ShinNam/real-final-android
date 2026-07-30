package com.veryshinnam.myapp.feature.character.ui

import android.content.Intent
import android.content.pm.ActivityInfo
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.veryshinnam.myapp.R
import com.veryshinnam.myapp.common.component.BackButton
import com.veryshinnam.myapp.common.component.InstructionText
import com.veryshinnam.myapp.common.component.LogoBar
import com.veryshinnam.myapp.common.component.LoadErrorView
import com.veryshinnam.myapp.common.component.ManualStopButton
import com.veryshinnam.myapp.common.component.ShareSheet
import com.veryshinnam.myapp.common.component.VideoPlayer
import com.veryshinnam.myapp.common.component.WarningConfirmSheet
import com.veryshinnam.myapp.common.component.WarningSheet
import com.veryshinnam.myapp.common.model.ManualState
import com.veryshinnam.myapp.core.orientation.OrientationManager
import com.veryshinnam.myapp.feature.character.component.CharacterCardLeft
import com.veryshinnam.myapp.feature.character.component.CharacterCardRight
import com.veryshinnam.myapp.feature.character.component.CharacterTabButton
import com.veryshinnam.myapp.feature.story.model.StoryType

@Composable
fun CharacterScreen(
    id: Long,
    onBack: () -> Unit,
    onLogoClick: () -> Unit,
    onStoryClick: (Long, StoryType) -> Unit,
    goToNextManual: () -> Unit,
    onManualStop: () -> Unit,
    xMoving: Dp = 60.dp,
    verticalPadding: Dp = 10.dp,
    manualTextStyle: TextStyle = MaterialTheme.typography.titleSmall.copy(fontWeight = Bold),
    vm: CharacterViewModel = hiltViewModel()
) {
    val density = LocalDensity.current

    // 상태 구독
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val manualState by vm.manualState.collectAsStateWithLifecycle()
    val manualMessage by vm.manualMessage.collectAsStateWithLifecycle()
    val manualStep by vm.manualStep.collectAsStateWithLifecycle()
    val manualIndex by vm.manualIndex.collectAsStateWithLifecycle()

    // 공유 및 클립보드
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    var warnedStoryId by remember { mutableStateOf<Long?>(null) }
    var isWarning by remember { mutableStateOf(false) }
    var isSimpleWarning by remember { mutableStateOf(false) } // 단순 경고창
    var SimpleWarningText by remember { mutableStateOf("") }

    var isVideoMaking by remember { mutableStateOf(false) }
    var isLinkNotExisting by remember { mutableStateOf(false) }
    var isLinkSharing by remember { mutableStateOf(false) }

    var youtubeLink by remember { mutableStateOf<String?>(null) }

    // ui 변수
    var isFront by rememberSaveable { mutableStateOf(true) } // 카드 앞뒷면 구분


    // 매뉴얼 변수
    val isManual = manualState != ManualState.NONE
    val onStopManual: () -> Unit = { vm.clearManual(); onManualStop() }
    var tabRect by remember { mutableStateOf<Rect?>(null) }  // 탭 버튼 위치
    var storyRect by remember { mutableStateOf<Rect?>(null) }  // 탭 버튼 위치
    var videoRect by remember { mutableStateOf<Rect?>(null) } // 움직이는 동화 잠금 위치

    // 가로 모드 고정
    SideEffect {
        OrientationManager.setOrientation?.invoke(
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        )
    }

    // 캐릭터 id 바뀌면 재로딩
    LaunchedEffect(id) {
        if (manualState == ManualState.NONE) {
            vm.fetchCharacter(id) // 실제 데이터
        }
    }

    LaunchedEffect(manualState) {
        if (manualState == ManualState.START) {
            vm.startManual() // 매뉴얼 시작일 때만 더미 데이터
        }
    }

    LaunchedEffect(manualIndex) {
        if (manualIndex == 4) {
            isFront = false
        }
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

    Box(
        modifier = Modifier
            .semantics {
                isTraversalGroup = true
            }
            .fillMaxSize()
            .background(colorResource(R.color.background_yellow)),
        contentAlignment = Alignment.Center
    ) {
        // 상단 로고
        LogoBar(
            modifier = Modifier
                .padding(WindowInsets.statusBars.asPaddingValues())
                .zIndex(2f)
                .align(Alignment.TopCenter),
            onLogoClick = onLogoClick
        )

        // 뒤로 가기 버튼
        BackButton(
            modifier = Modifier
                .padding(WindowInsets.statusBars.asPaddingValues())
                .zIndex(1f)
                .align(Alignment.TopStart)
                .semantics {
                    traversalIndex = 1f
                },
            onBackClick = onBack
        )

        when (val state = uiState) {
            // 조회 로딩 중
            is CharacterUiState.Loading -> {
                CircularProgressIndicator(
                    color = colorResource(id = R.color.main_orange), // 주황색
                    trackColor = Color.Gray.copy(alpha = 0.5f),
                    strokeWidth = 4.dp
                )
            }

            // 조회 오류
            is CharacterUiState.Error -> {
                LoadErrorView(
                    message = state.message,
                    onRetry = { vm.reload(id) }
                )
            }

            // 조회 성공
            is CharacterUiState.Success -> {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .padding(vertical = verticalPadding),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End // Row 안 가로 배치 방향
                ) {
                    // 왼쪽 캐릭터 이미지 카드
                    CharacterCardLeft(
                        character = state.characterData,
                        onFavoriteClick = { id ->
                            vm.updateFavorite(id) { text ->
                                isSimpleWarning = true
                                SimpleWarningText = text
                            }
                        },
                        modifier = Modifier
                            .aspectRatio(0.8f) // 카드 비율
                            .offset(x = xMoving)       // 오른쪽 이동
                    )

                    // 오른쪽 캐릭터 정보 카드
                    Column {
                        CharacterCardRight(
                            character = state.characterData,
                            isFront = isFront,
                            onFlip = { newFront ->
                                val prevFront = isFront // 이전 면
                                isFront = newFront      // 업데이트된 면

                                // 뒤 > 앞 때만 refresh
                                if (!prevFront && newFront) {
                                    vm.refreshStories(state.characterData.id)
                                }
                            },
                            onStoryClick = onStoryClick,
                            onLockerClick = { storyId ->
                                isWarning = true
                                warnedStoryId = storyId },
                            onMakingClick = { isVideoMaking = true },
                            onKakaoClick = { storyYLink ->
                                if (!storyYLink.isNullOrBlank()) {
                                    isLinkSharing = true
                                    youtubeLink = storyYLink
                                } else {
                                    // 링크 없음 처리
                                    isLinkNotExisting = true
                                }
                            },
                            onTabRect = { rect ->
                                if (manualState == ManualState.START && tabRect == null) {
                                    tabRect = rect
                                }
                            },
                            onStoryRect = { rect ->
                                if (manualState == ManualState.START
                                    && manualIndex == 4 && storyRect == null) {
                                    storyRect = rect
                                }
                            },
                            onVideoRect = { rect ->
                                if (manualState == ManualState.START
                                    && manualIndex == 4 && videoRect == null) {
                                    videoRect = rect
                                }
                            },
                            modifier = Modifier
                                .aspectRatio(1.78f) // 카드 비율
                                .zIndex(1f)
                        )
                        if (!isManual) {
                            InstructionText(
                                text = "카드를 눌러 보세요.\n카드가 뒤집히고 동화가 나와요!",
                                textAlign = TextAlign.End,
                                textStyle = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = 6.dp).align(Alignment.End)
                            )
                        }
                    }
                }
            }
        }

        if (manualState != ManualState.NONE) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(20f)
                    .background(
                        if (manualIndex == 2 || manualIndex == 4)
                            Color.Transparent
                        else
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
                        contentDescription = "\n아무 곳을 터치하세요. 현재 캐릭터 상세 보기 화면 매뉴얼 진행" +
                                if (manualState != ManualState.STOP) "중. 전체 49 단계 중 $manualStep 단계"
                                else "중단."
                        stateDescription = manualMessage
                    }
            ) {
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(20.dp),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    // 토끼 이미지
                    Image(
                        painter = painterResource(R.drawable.img_home_squirrel),
                        contentDescription = "다람쥐 이미지",
                        modifier = Modifier
                            .fillMaxHeight(0.22f)
                            .padding(start = 26.dp),
                        contentScale = ContentScale.Fit
                    )

                    // 메시지 박스
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .background(Color.White, shape = RoundedCornerShape(16.dp))
                            .border(
                                4.dp,
                                colorResource(R.color.blue_gray),
                                RoundedCornerShape(16.dp)
                            )
                            .padding(16.dp)
                            .zIndex(50f),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = manualMessage.replace("", "\u200B"),
                            style = manualTextStyle.copy(
                                lineHeight = manualTextStyle.fontSize * 1.2f
                            ),
                        )
                    }
                }

                // 동영상
                if (manualIndex >= 6) {
                    videoRect?.let { rect ->
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
                                .clip(RoundedCornerShape(16.dp))
                        ) {
                            VideoPlayer(
                                videoUrl = "android.resource://${context.packageName}/${R.raw.dummy_page}",
                                modifier = Modifier.fillMaxSize()
                            )

                            if (manualIndex >= 7) {
                                // 오버레이
                                Box(
                                    modifier = Modifier
                                        .matchParentSize()
                                        .background(Color.Black.copy(alpha = 0.5f))
                                )

                                // 오른쪽 카톡 버튼
                                Image(
                                    painter = painterResource(R.drawable.img_kakao), // 새 이미지
                                    contentDescription = "카카오 이미지",
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .size(
                                            with(density) { rect.width.toDp() * 0.35f },
                                            with(density) { rect.height.toDp() * 0.35f }
                                        )
                                        .clip(RoundedCornerShape(12.dp))
                                        .zIndex(1f),
                                    contentScale = ContentScale.Fit
                                )
                            }
                        }
                    }
                }

                // 왼쪽 카톡 버튼
                if (manualIndex >= 7) {
                    storyRect?.let { rect ->
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
                                .clip(RoundedCornerShape(16.dp))
                        ) {
                            Image(
                                painter = painterResource(R.drawable.img_kakao), // 새 이미지
                                contentDescription = "카카오 이미지",
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(
                                        with(density) { rect.width.toDp() * 0.35f },
                                        with(density) { rect.height.toDp() * 0.35f }
                                    )
                                    .clip(RoundedCornerShape(12.dp))
                                    .zIndex(1f),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }
                }

                // 그외 강조 요소
                when (manualIndex) {
                    3 -> { tabRect?.let { it ->
                        // 탭버튼
                        CharacterTabButton(
                            modifier = Modifier
                                .absoluteOffset(
                                    x = with(density) { it.left.toDp() },
                                    y = with(density) { it.top.toDp() }
                                )
                                .size(
                                    with(density) { it.width.toDp() },
                                    with(density) { it.height.toDp() }
                                )
                                .zIndex(10f),
                            onClick = {
                                when (manualState) {
                                    ManualState.START -> { vm.nextManualIndex() }
                                    ManualState.STOP -> { onStopManual() }
                                    else -> {}
                                }
                            },
                            alpha = 0.5f,
                        )
                    } }
                    5 -> { videoRect?.let { it ->
                        // 잠금 해제
                        Box(
                            modifier = Modifier
                                .absoluteOffset(
                                    x = with(density) { it.left.toDp() },
                                    y = with(density) { it.top.toDp() })
                                .size(
                                    with(density) { it.width.toDp() },
                                    with(density) { it.height.toDp() }
                                )
                                .clip(RoundedCornerShape(16.dp))   // 여기에 clip
                        ) {
                            Image(
                                painter = painterResource(R.drawable.img_locker),
                                contentDescription = "잠금 이미지",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )
                        }
                    } }
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
                    .padding(bottom = 6.dp)
                    .clearAndSetSemantics { }
            )
        }
    }

    // 움직이는 동화 잠금 해제
    if (isWarning && warnedStoryId != null) {
        WarningConfirmSheet(
            warningText = "움직이는 동화의 잠금을 해제할까요?\n" +
                    "도토리 1개가 소진돼요!",
            confirmText = "해제하기",
            onDismiss = { isWarning = false },
            onConfirm = {
                vm.fetchVideoStory(id, warnedStoryId!!) { text ->
                    isSimpleWarning = true
                    SimpleWarningText = text
                }
                isWarning = false
                warnedStoryId = null
            }
        )
    }

    // 움직이는 동화 제작중
    if (isVideoMaking) {
        WarningSheet(
            warningText = "아직 움직이는 동화를 만들고 있는 중이에요.\n조금만 더 기다려주세요!\n\n" +
                    "혹시 오래 기다렸는데도 동화가 안 뜨면,\n저희에게 알려주세요!",
            onDismiss = { isVideoMaking = false },
        )
    }

    // 동화 유튜브 링크 없음
    if (isLinkNotExisting) {
        WarningSheet(
            warningText = "아직 유튜브 링크가 준비되지 않았어요.\n" +
                    "조금만 기다려 주세요!\n\n" +
                    "모든 아이들에게 안전한지 확인한 뒤,\n심사를 거쳐 유튜브에 공개돼요." +
                    "\n(발생한 수익은 스토릭터를 계속 운영하기 위해 사용돼요.)",
            wtStyle = MaterialTheme.typography.bodySmall,
            onDismiss = { isLinkNotExisting = false },
        )
    }

    // 카톡으로 공유
    if (isLinkSharing && !youtubeLink.isNullOrBlank()) {
        ShareSheet(
            urlText = youtubeLink!!,
            confirmText = "카카오톡으로 공유하기",
            onDismiss = { isLinkSharing = false },
            onShare = {
                try {
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, youtubeLink)
                        setPackage("com.kakao.talk") // 카톡 패키지 지정
                    }
                    context.startActivity(intent)
                } catch (_: Exception) {
                    Toast.makeText(context, "카카오톡이 설치되어 있지 않습니다.", Toast.LENGTH_SHORT).show()
                }
            },
            onCopy = {
                clipboardManager.setText(AnnotatedString(youtubeLink!!))
            }
        )
    }

    // 즐찾 오류
    if (isSimpleWarning) {
        WarningSheet(
            warningText = SimpleWarningText,
            onDismiss = { isSimpleWarning = false }
        )
    }
}