package com.veryshinnam.myapp.feature.story.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.veryshinnam.myapp.R
import com.veryshinnam.myapp.common.component.CircleIconButton
import com.veryshinnam.myapp.common.component.VideoPlayer
import com.veryshinnam.myapp.common.component.Watermark
import com.veryshinnam.myapp.feature.story.model.PageData
import com.veryshinnam.myapp.feature.story.model.StoryType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun StoryReaderPage(
    page: PageData,
    pageNum: Int,
    storyType: StoryType,
    pagerState: PagerState,
    coroutineScope: CoroutineScope,
    pageCount: Int,
    horizontalPadding: Dp = 16.dp,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center),
    onEndingPage: () -> Unit
) {
    val pageDescription = if (pageNum == 3) {
        "마지막 페이지"
    } else {
        "${pageNum + 1} 페이지"
    }

    val aiDescription = when (storyType) {
        StoryType.IMAGE -> "AI로 생성된 이미지입니다."
        StoryType.VIDEO -> "AI로 생성된 영상입니다."
    }

    Box(Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .semantics(true) {
                    contentDescription = "$pageDescription. $aiDescription"
                }
        ) {
            when (storyType) {
                // 페이지 이미지
                StoryType.IMAGE -> {
                    AsyncImage(
                        model = page.url,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // 워터마크
                    Watermark(
                        text = "AI로 생성된 이미지",
                        modifier = Modifier
                            .padding(5.dp)
                            .fillMaxHeight(0.06f)
                            .align(Alignment.BottomStart)
                    )
                }

                // 페이지 영상
                StoryType.VIDEO -> {
                    VideoPlayer(
                        videoUrl = page.url,
                        modifier = Modifier.fillMaxSize().clearAndSetSemantics { }
                    )

                    // 워터마크
                    Watermark(
                        text = "AI로 생성된 영상",
                        modifier = Modifier
                            .padding(5.dp)
                            .fillMaxHeight(0.06f)
                            .align(Alignment.BottomStart)
                    )
                }
            }
        }

        // --- 페이지 하단
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp, start = horizontalPadding/2)
                .align(Alignment.BottomCenter)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(end = 8.dp)
                    .semantics {
                        isTraversalGroup = true
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(horizontalPadding/2)
            ) {
                // ◀ 이전 버튼
                CircleIconButton(
                    icon = Icons.Rounded.ChevronLeft,
                    desc = "이전",
                    onClick = {
                        if (pagerState.currentPage > 0) {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage - 1)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxHeight(0.24f)
                        .semantics {
                            traversalIndex = 1f
                            contentDescription = "이전 페이지 이동"
                            role = Role.Button
                        }
                )

                // 줄거리 내용
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            color = colorResource(R.color.background_yellow).copy(alpha = 0.5f),
                            shape = RoundedCornerShape(16.dp))
                        .border(
                            width = 2.dp,
                            color = colorResource(R.color.main_orange),
                            shape = RoundedCornerShape(16.dp))
                        .semantics {
                            traversalIndex = 0f
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        modifier = Modifier
                            .padding(horizontalPadding),
                        text = page.content,
                        style = textStyle
                    )
                }

                // ▶ 다음 버튼
                CircleIconButton(
                    icon = Icons.Rounded.ChevronRight,
                    desc = "다음",
                    onClick = {
                        if (pagerState.currentPage < pageCount - 1) {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        } else {
                            onEndingPage()
                        }
                    },
                    modifier = Modifier.fillMaxHeight(0.24f)
                        .semantics {
                            traversalIndex = 2f
                            contentDescription = "다음 페이지 이동"
                            role = Role.Button
                        }
                )
            }
        }
    }
}