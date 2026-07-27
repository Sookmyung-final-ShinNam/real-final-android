package com.veryshinnam.myapp.feature.story.content

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.veryshinnam.myapp.R
import com.veryshinnam.myapp.feature.story.model.PageData
import com.veryshinnam.myapp.feature.story.model.StoryType
import com.veryshinnam.myapp.feature.story.components.StoryEndingPage
import com.veryshinnam.myapp.feature.story.components.StoryReaderPage
import kotlinx.coroutines.launch

@Composable
fun StoryReadingContent(
    pages: List<PageData>,
    storyType: StoryType,
    isTtsMode: Boolean,
    isReady: Boolean,
    onTtsModeChange: () -> Unit,
    onHome: () -> Unit,
    onPrologue: () -> Unit,
    onSpeakPage: (String) -> Unit,
    onStopSpeaking: () -> Unit,
    ttsTextStyle: TextStyle = MaterialTheme.typography.bodySmall.copy(color = colorResource(R.color.main_orange), fontWeight = Bold)
) {
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { pages.size + 1 } // 마지막 EndingPage 포함
    )
    val coroutineScope = rememberCoroutineScope()

    // 자동 읽기 > TTS 모드 ON + 엔진 준비 + 페이지 변경 시
    LaunchedEffect(pagerState.currentPage, isTtsMode, isReady) {
        if (pagerState.currentPage < pages.size) {
            if (isTtsMode && isReady) {
                val text = pages[pagerState.currentPage].content
                onSpeakPage(text)
            } else {
                onStopSpeaking()
            }
        } else {
            onStopSpeaking()
        }
    }

    BackHandler { onPrologue() }

    Box(Modifier.fillMaxSize()) {
        // 동화 페이지
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.matchParentSize()
        ) { page ->
            if (page < pages.size) {
                StoryReaderPage(
                    page = pages[page],
                    pageNum = page,
                    storyType = storyType,
                    pagerState = pagerState,
                    coroutineScope = coroutineScope,
                    pageCount = pages.size,
                    onEndingPage = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pages.size)
                        }
                    }
                )
            } else {
                StoryEndingPage(
                    onRestart = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(0) // 첫 페이지로 이동
                        }
                    },
                    onHome = onHome
                )
            }
        }

        if (pagerState.currentPage < pages.size) {

            // TTS 재생 버튼
            Column(
                modifier = Modifier
                    .fillMaxHeight(0.3f)
                    .systemBarsPadding()
                    .padding(end = 8.dp)
                    .clickable { onTtsModeChange() }
                    .align(Alignment.TopEnd)
                    .semantics(true) {
                        contentDescription = "동화 읽어주기"
                        stateDescription = if (isTtsMode) "소리 재생 켜짐" else "소리 재생 꺼짐"
                        role = Role.Button
                    },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = if (isTtsMode) { painterResource(R.drawable.img_speak_on) }
                            else { painterResource(R.drawable.img_speak_off) },
                    contentDescription = null,
                    modifier = Modifier.weight(1f),
                    contentScale = ContentScale.Fit
                )

                Text(
                    text = " 소리 " + if (isTtsMode) "켜짐" else "꺼짐",
                    textAlign = TextAlign.Center,
                    style = ttsTextStyle.copy(
                        lineHeight = ttsTextStyle.lineHeight * 1.2f
                    ),
                    modifier = Modifier
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    colorResource(R.color.background_yellow).copy(0.4f),
                                    Color.Transparent
                                )))
                        .clearAndSetSemantics { }
                )
            }
        }
    }
}
