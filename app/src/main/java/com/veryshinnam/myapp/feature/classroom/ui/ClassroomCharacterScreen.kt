package com.veryshinnam.myapp.feature.classroom.ui

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.veryshinnam.myapp.R
import com.veryshinnam.myapp.common.component.BackButton
import com.veryshinnam.myapp.common.component.LoadErrorView
import com.veryshinnam.myapp.common.component.LogoBar
import com.veryshinnam.myapp.common.component.WarningSheet
import com.veryshinnam.myapp.core.orientation.OrientationManager
import com.veryshinnam.myapp.feature.character.component.CharacterCardLeft
import com.veryshinnam.myapp.feature.character.component.CharacterRightFront
import com.veryshinnam.myapp.feature.character.component.CharacterStoryButton
import com.veryshinnam.myapp.feature.character.ui.CharacterUiState
import com.veryshinnam.myapp.feature.character.ui.CharacterViewModel
import com.veryshinnam.myapp.feature.story.model.StoryType

/**
 * 학급 동화 캐릭터 화면
 * : 캐릭터 화면(CharacterScreen)을 재사용하되 탭(카드 뒤집기) 없이 단일 화면으로 구성
 *   왼쪽 캐릭터 이미지 + 오른쪽 캐릭터 정보 / 동화 보러 가기
 *   (다른 친구의 동화이므로 관심 캐릭터 등록과 움직이는 동화 만들기는 제공하지 않음)
 *
 * - id: 캐릭터 아이디
 * - onBack: 뒤로 가기 버튼 클릭 시
 * - onLogoClick: 로고 클릭 시, 홈 화면으로 복구
 * - onStoryClick: 동화 보러 가기 클릭 시, 동화 보기 화면으로 이동
 */
@Composable
fun ClassroomCharacterScreen(
    id: Long,
    onBack: () -> Unit,
    onLogoClick: () -> Unit,
    onStoryClick: (Long, StoryType) -> Unit,
    xMoving: Dp = 60.dp,
    verticalPadding: Dp = 10.dp,
    vm: CharacterViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    // 상태 구독
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    var isLinkNotExisting by remember { mutableStateOf(false) } // 유튜브 링크 준비 안 됨 경고

    // 가로 모드 고정
    SideEffect {
        OrientationManager.setOrientation?.invoke(
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        )
    }

    // 캐릭터 id 바뀌면 재로딩
    LaunchedEffect(id) { vm.fetchCharacter(id) }

    BackHandler { onBack() }

    Box(
        modifier = Modifier
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
                .align(Alignment.TopStart),
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
                val character = state.characterData

                Row(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .padding(vertical = verticalPadding),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    // 왼쪽 캐릭터 이미지 카드
                    CharacterCardLeft(
                        character = character,
                        onFavoriteClick = { }, // 학급 동화는 관심 캐릭터 등록 불가
                        modifier = Modifier
                            .aspectRatio(0.8f) // 카드 비율
                            .offset(x = xMoving)
                    )

                    // 오른쪽 캐릭터 정보 + 동화 보러 가기 (단일 카드)
                    Card(
                        modifier = Modifier
                            .aspectRatio(1.78f) // 카드 비율
                            .zIndex(1f),
                        colors = CardDefaults.cardColors(
                            containerColor = colorResource(R.color.card_orange_90)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(vertical = 24.dp, horizontal = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // 캐릭터 이름 + 기본 정보
                            CharacterRightFront(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight(),
                                character = character
                            )

                            // 동화 보러 가기
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight(),
                                verticalArrangement = Arrangement.spacedBy(5.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CharacterStoryButton(
                                    storyId = character.stories.storyId,
                                    title = character.stories.title,
                                    imageUrl = character.stories.imageUrl,
                                    youTubeLink = character.stories.imageYLink,
                                    infoText = "동화",
                                    onStoryClick = onStoryClick,
                                    onKakaoClick = { link ->
                                        if (link.isNullOrBlank()) {
                                            isLinkNotExisting = true
                                        } else {
                                            // 링크 공유
                                            val intent = Intent(Intent.ACTION_SEND).apply {
                                                type = "text/plain"
                                                putExtra(Intent.EXTRA_TEXT, link)
                                            }
                                            context.startActivity(Intent.createChooser(intent, "동화 공유하기"))
                                        }
                                    },
                                    modifier = Modifier.weight(1f)
                                )

                                Text(
                                    text = "동화 보러 가기",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center
                                    ),
                                    maxLines = 1,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clearAndSetSemantics { }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // 유튜브 링크 준비 안 됨
    if (isLinkNotExisting) {
        WarningSheet(
            warningText = "아직 유튜브 링크가 준비되지 않았어요.\n조금만 기다려 주세요!",
            wtStyle = MaterialTheme.typography.bodySmall,
            onDismiss = { isLinkNotExisting = false }
        )
    }
}
