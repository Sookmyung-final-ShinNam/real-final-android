package com.veryshinnam.myapp.feature.admin.ui

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.veryshinnam.myapp.feature.story.model.StoryType

@Composable
fun AdminScreen(
    onStoryClick: (Long, StoryType) -> Unit,
    viewModel: AdminViewModel = hiltViewModel()
) {
    val stories by viewModel.stories.collectAsState()
    val toast by viewModel.toastMessage.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.loadStories()
    }

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { stories.size }
    )

    // 모달 상태
    var showConfirmDialog by remember { mutableStateOf(false) }
    var pendingUploadStoryId: Long by remember { mutableStateOf(0) }
    var pendingUploadLink by remember { mutableStateOf("") }
    var pendingUploadType by remember { mutableStateOf("") } // "IMAGE" or "VIDEO"

    Box(modifier = Modifier.fillMaxSize()) {
        if (stories.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("불러올 동화가 없습니다.")
            }
        } else {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                val story = stories[page]

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 이미지용 카드
                    if (story.needsImageLink) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onStoryClick(story.id, StoryType.IMAGE)
                                },
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${story.title} (이미지용)",
                                    style = MaterialTheme.typography.titleLarge
                                )
                            }
                        }
                    }

                    // 동영상용 카드
                    if (story.needsVideoLink) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onStoryClick(story.id, StoryType.VIDEO)
                                },
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${story.title} (동영상용)",
                                    style = MaterialTheme.typography.titleLarge
                                )
                            }
                        }
                    }

                    // 업로드 UI - 이미지
                    if (story.needsImageLink) {
                        var imageLink by remember { mutableStateOf(TextFieldValue("")) }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = imageLink,
                                onValueChange = { imageLink = it },
                                label = { Text("이미지 유튜브 링크 입력") },
                                modifier = Modifier.weight(1f)
                            )

                            Button(
                                onClick = {
                                    if (imageLink.text.isNotBlank()) {
                                        pendingUploadStoryId = story.id
                                        pendingUploadLink = imageLink.text
                                        pendingUploadType = "IMAGE"
                                        showConfirmDialog = true
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "1글자 이상 입력해주세요.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            ) {
                                Text("업로드")
                            }
                        }
                    }

                    // 업로드 UI - 동영상
                    if (story.needsVideoLink) {
                        var videoLink by remember { mutableStateOf(TextFieldValue("")) }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = videoLink,
                                onValueChange = { videoLink = it },
                                label = { Text("동영상 유튜브 링크 입력") },
                                modifier = Modifier.weight(1f)
                            )

                            Button(
                                onClick = {
                                    if (videoLink.text.isNotBlank()) {
                                        pendingUploadStoryId = story.id
                                        pendingUploadLink = videoLink.text
                                        pendingUploadType = "VIDEO"
                                        showConfirmDialog = true
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "1글자 이상 입력해주세요.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            ) {
                                Text("업로드")
                            }
                        }
                    }
                }
            }
        }

        // 업로드 확인 모달
        if (showConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showConfirmDialog = false },
                title = { Text("업로드 확인") },
                text = { Text("정말 업로드 하시겠습니까?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (pendingUploadType == "IMAGE") {
                                viewModel.uploadImageLink(pendingUploadStoryId, pendingUploadLink)
                            } else if (pendingUploadType == "VIDEO") {
                                viewModel.uploadVideoLink(pendingUploadStoryId, pendingUploadLink)
                            }
                            pendingUploadLink = ""
                            showConfirmDialog = false
                        }
                    ) {
                        Text("확인")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showConfirmDialog = false }
                    ) {
                        Text("취소")
                    }
                }
            )
        }
    }

    // 토스트 메시지
    toast?.let {
        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        viewModel.clearToast()
    }
}