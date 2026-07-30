package com.veryshinnam.myapp.feature.story.content

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.veryshinnam.myapp.R
import com.veryshinnam.myapp.common.component.VideoPlayer
import com.veryshinnam.myapp.feature.story.model.StoryData
import com.veryshinnam.myapp.feature.story.model.StoryType
import com.veryshinnam.myapp.feature.story.components.StoryReadButton
import com.veryshinnam.myapp.feature.story.components.StoryInfoFade

@Composable
fun StoryPrologueContent(
    story: StoryData,
    storyType: StoryType,
    onReadClick: () -> Unit,
) {
    // --- 동화 썸네일 이미지 (42:58)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.main_orange))
            .navigationBarsPadding()
            .semantics {
                isTraversalGroup = true
            }
    ) {
        // --- 썸네일
        Box(
            modifier = Modifier
                .semantics(true) {
                    traversalIndex = 0f
                    contentDescription = when (storyType) {
                                StoryType.IMAGE -> "동화 썸네일 이미지. AI로 생성된 이미지입니다."
                                StoryType.VIDEO -> "동화 썸네일 동영상. AI로 생성된 동영상입니다."
                            }
                }
        ) {
            when (storyType) {
                StoryType.IMAGE -> {
                    AsyncImage(
                        model = story.thumbnail,
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth(0.42f),
                        contentScale = ContentScale.FillWidth
                    )
                }

                StoryType.VIDEO -> {
                    VideoPlayer(
                        videoUrl = story.thumbnail,
                        modifier = Modifier.fillMaxWidth(0.42f)
                    )
                }
            }
        }

        // --- 동화 정보
        StoryInfoFade(
            story,
            storyType = storyType,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterEnd)
                .semantics {
                    traversalIndex = 1f
                }
        )

        // 보러가기 버튼
        Box(
            modifier = Modifier
                .fillMaxWidth(0.44f)
                .fillMaxHeight()
                .semantics {
                    traversalIndex = 2f
                }
        ) {
            StoryReadButton(
                onButtonClick = onReadClick,
                modifier = Modifier
                    .fillMaxHeight(0.4f)
                    .padding(16.dp)
                    .align(Alignment.BottomEnd)
            )
        }
    }
}