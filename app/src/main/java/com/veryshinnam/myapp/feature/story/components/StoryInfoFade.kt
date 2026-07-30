package com.veryshinnam.myapp.feature.story.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.veryshinnam.myapp.R
import com.veryshinnam.myapp.feature.story.model.StoryData
import com.veryshinnam.myapp.feature.story.model.StoryType

@Composable
fun StoryInfoFade(
    story: StoryData,
    storyType: StoryType,
    titleTextStyle: TextStyle = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
    tagTextStyle: TextStyle = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
    descTextStyle: TextStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
    modifier: Modifier
) {
    var size by remember { mutableStateOf(IntSize.Zero) }
    val aiNoticeText = when (storyType) {
        StoryType.IMAGE -> "해당 동화의 글과 이미지는 생성형 AI를 통해 만들어졌습니다."
        StoryType.VIDEO -> "해당 동화의 글과 영상은 생성형 AI를 통해 만들어졌습니다."
    }

    Row(modifier = modifier.fillMaxSize()) {
        // 페이드 부분
        Box(
            modifier = Modifier
                .fillMaxWidth(0.42f)
                .fillMaxHeight()
                .onSizeChanged { size = it }
        ) {
            if (size != IntSize.Zero) {
                // 페이드 오버레이
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            brush = Brush.radialGradient(
                                colorStops = arrayOf(
                                    0.0f to Color.Transparent,
                                    0.7f to Color.Transparent,
                                    1.0f to colorResource(R.color.main_orange)
                                ),
                                center = Offset.Zero,
                                radius = size.width.toFloat()
                            )
                        )
                )
            }
        }

        // 동화 정보
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize()
                .background(color = colorResource(R.color.main_orange))
                .padding(vertical = 20.dp, horizontal = 16.dp)
                .semantics {
                    contentDescription = when (storyType) {
                        StoryType.IMAGE -> "AI 생성 안내. 해당 동화의 글과 이미지는 생성형 AI를 통해 만들어졌습니다."
                        StoryType.VIDEO -> "AI 생성 안내. 해당 동화의 글과 영상은 생성형 AI를 통해 만들어졌습니다."
                    }
                }
        ) {
            Spacer(modifier = Modifier.fillMaxHeight(.14f)) // 로고 고려 여백

            // -- 제목, 태그, 줄거리, ai 생성 문구
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .semantics {
                        isTraversalGroup = true
                    }
            ) {
                Text( // 제목
                    text = story.title,
                    style = titleTextStyle.copy(
                        lineHeight = titleTextStyle.lineHeight * 1.2f
                    ),
                    modifier = Modifier.semantics {
                        traversalIndex = 0f
                        contentDescription = "동화 제목: ${story.title}"
                    },
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text( // 태그
                    text = story.hashtags,
                    style = tagTextStyle.copy(
                        color = Color.White,
                        lineHeight = titleTextStyle.lineHeight * 1.1f
                    ),
                    modifier = Modifier
                        .semantics {
                            traversalIndex = 0f
                            contentDescription = "동화 해시태그: ${story.tags}"
                        }
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text( // 줄거리
                    text = story.description.replace("", "\u200B"),
                    style = descTextStyle,
                    modifier = Modifier
                        .semantics {
                            traversalIndex = 0f
                            contentDescription = "동화 줄거리: ${story.description}"
                        },
                    maxLines = 6,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(12.dp))

                // -- ai 생성물 표시 문구
                Text(
                    text = aiNoticeText,
                    style = tagTextStyle.copy(
                        lineHeight = titleTextStyle.lineHeight * 1.08f
                    ),
                    modifier = Modifier.clearAndSetSemantics { }
                )
            }
        }
    }
}