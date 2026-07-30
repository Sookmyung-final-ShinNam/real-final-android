package com.veryshinnam.myapp.feature.character.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.veryshinnam.myapp.common.component.Watermark
import com.veryshinnam.myapp.common.component.FavoriteButton
import com.veryshinnam.myapp.common.model.ImageType
import com.veryshinnam.myapp.feature.character.model.CharacterData

@Composable
fun CharacterCardLeft(
    character: CharacterData, // 캐릭터 정보
    onFavoriteClick: (Long) -> Unit, // 즐찾 대상 캐릭터 id
    modifier: Modifier = Modifier    // 부모가 넘겨준 크기
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {

            // 캐릭터 이미지
            when (character.image) {
                is ImageType.Url -> {
                    AsyncImage(
                        model = character.image.url,
                        contentDescription = "${character.name} 캐릭터. AI로 생성된 캐릭터입니다.",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )
                }

                is ImageType.Resource -> {
                    Image(
                        painter = painterResource(id = character.image.resId),
                        contentDescription = "매뉴얼 캐릭터",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            // 즐찾 버튼
            FavoriteButton (
                modifier = Modifier
                    .fillMaxWidth(0.32f)
                    .aspectRatio(1f)
                    .align(Alignment.TopStart)
                    .padding(5.dp),
                characterId = character.id,
                isFavorite = character.isFavorite,
                onFavoriteClick = onFavoriteClick,

            )

            // 워터마크
            Watermark(
                text = "AI로 생성된 캐릭터",
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxHeight(0.06f)
                    .align(Alignment.BottomStart)
                    .clearAndSetSemantics { }
            )
        }
    }
}