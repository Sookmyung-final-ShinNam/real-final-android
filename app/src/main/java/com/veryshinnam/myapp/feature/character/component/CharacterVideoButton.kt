package com.veryshinnam.myapp.feature.character.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.veryshinnam.myapp.R
import com.veryshinnam.myapp.common.component.VideoPlayer
import com.veryshinnam.myapp.feature.character.model.VideoStatus
import com.veryshinnam.myapp.feature.story.model.StoryType

@Composable
fun CharacterVideoButton(
    storyId: Long,          // 동화 아이디
    title: String,
    videoUrl: String? = null,  // 영상 표지
    videoStatus: VideoStatus,
    youTubeLink: String?,    // 유튜브 링크
    infoText: String,  // 동화 타입 텍스트
    storySize: Float = 0.7f,
    storyRadius: Dp = 16.dp,
    kakaoSize: Float = 0.35f,
    kakaoRadius: Dp = 12.dp,
    onStoryClick: (Long, StoryType) -> Unit,
    onLockerClick: (Long) -> Unit = {}, // 잠금 클릭
    onMakingClick: () -> Unit = {},     // 제작 중 클릭
    onKakaoClick: (String?) -> Unit,    // 공유 클릭
    modifier: Modifier
) {
    // 동화 버튼
    Box(
        modifier = modifier.fillMaxHeight(storySize),
        contentAlignment = Alignment.TopEnd
    ) {
        Button(
            onClick = {
                when (videoStatus) {
                    VideoStatus.NONE -> onLockerClick(storyId)  // 잠금 > 제작
                    VideoStatus.MAKING -> onMakingClick()       // 제작 중 > 경고 시트
                    VideoStatus.COMPLETED -> {
                        if (videoUrl.isNullOrBlank()) onMakingClick() // 서버 문제
                        else onStoryClick(storyId, StoryType.VIDEO)   // 표지 있으면 이동
                    }
                }
            },
            modifier = Modifier.aspectRatio(1f)
                .semantics{
                    contentDescription = when (videoStatus) {
                        VideoStatus.NONE -> "$title $infoText 잠금 해제"
                        VideoStatus.MAKING -> "$title $infoText 동화 제작 중"
                        VideoStatus.COMPLETED -> {
                            if (videoUrl.isNullOrBlank()) "$title $infoText 제작 중"
                            else "$title $infoText 보러 가기"
                        }
                    }
                },
            shape = RoundedCornerShape(storyRadius),
            contentPadding = PaddingValues(0.dp) // 패딩 제거
        ) {

            // 동화가 비디오일 경우
            when (videoStatus) {
                // 잠금 상태
                VideoStatus.NONE -> {
                    Image(
                        painter = painterResource(R.drawable.img_locker),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }

                // 제작 중 상태
                VideoStatus.MAKING -> {
                    Image(
                        painter = painterResource(R.drawable.img_unlocker),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }

                // 완성된 상태
                VideoStatus.COMPLETED -> {
                    // 서버 문제
                    if (videoUrl.isNullOrBlank()) {
                        Image(
                            painter = painterResource(R.drawable.img_unlocker),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    } else {
                        // 영상 재생
                        VideoPlayer(
                            videoUrl = videoUrl,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }

        // 표지 있으면 카카오 버튼
        if (!videoUrl.isNullOrBlank()) {
            Button(
                onClick = { onKakaoClick(youTubeLink) },
                modifier = Modifier
                    .fillMaxHeight(kakaoSize)
                    .aspectRatio(1f)
                    .zIndex(1f),
                shape = RoundedCornerShape(kakaoRadius),
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
            ) {
                Image(
                    painter = painterResource(R.drawable.img_kakao),
                    contentDescription = "움직이는 동화 카카오톡 공유",
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}