package com.veryshinnam.myapp.feature.collection.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.veryshinnam.myapp.R
import com.veryshinnam.myapp.feature.collection.model.CollectionData

@Composable
fun CollectionCharacterGrid(
    data: List<CollectionData>,
    onFavoriteClick: (Long) -> Unit,
    onItemClick: (Long) -> Unit,
    cellPadding: Dp = 6.dp,
    modifier: Modifier,
    onCharacterRect: (Rect) -> Unit,
    listState: LazyGridState = rememberLazyGridState()
) {

    // 스크롤이 맨 위가 아닌지 체크 > 스크롤 시작 시 페이드 효과 시작
    val isTop by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset == 0
        }
    }

    Box {
        LazyVerticalGrid(
            modifier = modifier
                .semantics {
                    traversalIndex = 1f
                },
            columns = GridCells.Fixed(3),
            state = listState,
            verticalArrangement = Arrangement.spacedBy(cellPadding),
            horizontalArrangement = Arrangement.spacedBy(cellPadding),
            content = {
                itemsIndexed(data) { index, item ->
                    CollectionCharacterItem(
                        cId = item.id,
                        cName = item.name,
                        cImage = item.image,
                        isFavorite = item.isFavorite,
                        creatorName = item.creatorName,
                        onFavoriteClick = onFavoriteClick,
                        onItemClick = onItemClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(3f / 4f)
                            .then(
                                if (index == 4) {
                                    Modifier.onGloballyPositioned {
                                        onCharacterRect(it.boundsInRoot())
                                    }
                                } else Modifier
                            )
                    )
                }
            }
        )

        if (!isTop) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.08f)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                colorResource(R.color.background_yellow),
                                Color.Transparent
                            )
                        )
                    )
                    .align(Alignment.TopCenter)
            )
        }
    }
}