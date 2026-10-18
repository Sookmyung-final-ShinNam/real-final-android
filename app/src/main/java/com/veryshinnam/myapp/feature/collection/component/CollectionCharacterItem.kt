package com.veryshinnam.myapp.feature.collection.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.compose.ui.text.style.TextOverflow
import com.veryshinnam.myapp.R
import com.veryshinnam.myapp.common.component.FavoriteButton
import com.veryshinnam.myapp.common.component.StrokeTitle
import com.veryshinnam.myapp.common.model.ImageType

@Composable
fun CollectionCharacterItem(
    cId: Long,          // 캐릭터 아이디
    cName: String,      // 캐릭터 이름
    cImage: ImageType,  // 캐릭터 이미지
    isFavorite: Boolean, // 캐릭터 즐찾 여부
    onItemClick: (Long) -> Unit,
    onFavoriteClick: (cId: Long) -> Unit,   // 클릭 시 외부 처리
    creatorName: String? = null, // 만든 사용자 닉네임 (학급 동화에서만 표시)
    textStyle: TextStyle = MaterialTheme.typography.titleLarge,
    modifier: Modifier   // 부모가 넘겨준 크기
) {
    // 캐릭터 아이템 박스
    Box(
        modifier = modifier
            .background(Color.White, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .border(
                2.dp,
                colorResource(id = R.color.blue_gray),
                RoundedCornerShape(16.dp))
            .semantics {
               isTraversalGroup = true
            }
    ) {
        // 캐릭터 이미지
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable { onItemClick(cId) }
                .semantics {
                    traversalIndex = 0f
                    contentDescription = cName + "캐릭터 자세히 보기"
                    role = Role.Button
            }
        ) {
            when (cImage) {
                is ImageType.Url -> {
                    AsyncImage(
                        model = cImage.url,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )
                }

                is ImageType.Resource -> {
                    Image(
                        painter = painterResource(id = cImage.resId),
                        contentDescription = "매뉴얼 캐릭터 이미지",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }

        // 즐찾 버튼
        FavoriteButton (
            modifier = Modifier
                .fillMaxWidth(0.44f)
                .aspectRatio(1f)
                .align(Alignment.TopStart)
                .padding(4.dp)
                .semantics {
                    traversalIndex = 1f
                    contentDescription = "${cName}의 즐겨찾기"
                },
            characterId = cId,
            isFavorite = isFavorite,
            onFavoriteClick = onFavoriteClick,
        )

        // 캐릭터 이름 (+ 학급 동화는 만든 사용자 닉네임)
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 4.dp) // 아래 패딩
                .clearAndSetSemantics { },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            StrokeTitle(
                titleText = cName,
                titleColor = Color.White,
                strokeColor = Color.Black,
                titleTextStyle = textStyle,
                strokeWidth = 4f
            )

            if (!creatorName.isNullOrBlank()) {
                StrokeTitle(
                    titleText = creatorName,
                    titleColor = Color.White,
                    strokeColor = Color.Black,
                    titleTextStyle = MaterialTheme.typography.bodySmall,
                    strokeWidth = 3f,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}