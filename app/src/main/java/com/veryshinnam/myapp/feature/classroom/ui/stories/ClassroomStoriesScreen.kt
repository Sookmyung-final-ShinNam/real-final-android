package com.veryshinnam.myapp.feature.classroom.ui.stories

import android.content.pm.ActivityInfo
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.veryshinnam.myapp.R
import com.veryshinnam.myapp.common.component.BackButton
import com.veryshinnam.myapp.common.component.LoadErrorView
import com.veryshinnam.myapp.common.component.LogoBar
import com.veryshinnam.myapp.feature.classroom.data.model.ClassroomCharacterData
import com.veryshinnam.myapp.core.orientation.OrientationManager

@Composable
fun ClassroomStoriesScreen(
    classroomId: Long,
    week: Int,
    onBack: () -> Unit,
    vm: ClassroomStoriesViewModel = hiltViewModel()
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    SideEffect { OrientationManager.setOrientation?.invoke(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) }

    Scaffold(
        containerColor = colorResource(R.color.background_yellow),
        topBar = {
            Column {
                Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
                LogoBar()
            }
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when (val state = uiState) {
                is ClassroomStoriesUiState.Loading -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                    CircularProgressIndicator(color = colorResource(R.color.main_orange), trackColor = Color.Gray.copy(0.3f))
                }
                is ClassroomStoriesUiState.Error -> LoadErrorView(message = state.message, onRetry = { vm.reload() })
                is ClassroomStoriesUiState.Success -> {
                    Column(Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
                        Spacer(Modifier.height(8.dp))

                        // 헤더
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.padding(bottom = 12.dp)
                        ) {
                            Box(
                                Modifier
                                    .background(colorResource(R.color.main_orange), RoundedCornerShape(20.dp))
                                    .padding(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    "${state.week}주차 과제",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = Color.White, fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                            Text(
                                "우리 반 동화 보관함",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        if (state.characters.isEmpty()) {
                            Box(Modifier.fillMaxSize(), Alignment.Center) {
                                Text(
                                    "아직 완성된 동화가 없어요.\n친구들의 동화를 기다려보세요!",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color.Gray,
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(3),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                contentPadding = PaddingValues(bottom = 24.dp)
                            ) {
                                items(state.characters) { character ->
                                    ClassroomCharacterCard(character = character)
                                }
                            }
                        }
                    }
                }
            }

            BackButton(modifier = Modifier.align(Alignment.TopStart).padding(start = 16.dp, top = 8.dp), onBackClick = onBack)
        }
    }
}

@Composable
private fun ClassroomCharacterCard(character: ClassroomCharacterData) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(12.dp))
            .border(1.5.dp, colorResource(R.color.main_orange_50), RoundedCornerShape(12.dp))
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(colorResource(R.color.background_yellow))
        ) {
            if (!character.imageUrl.isNullOrEmpty()) {
                AsyncImage(
                    model = character.imageUrl,
                    contentDescription = character.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Text("🎨", style = MaterialTheme.typography.headlineMedium)
                }
            }
        }
        Text(
            text = character.name,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}
