package com.veryshinnam.myapp.feature.classroom.ui

import android.content.pm.ActivityInfo
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.veryshinnam.myapp.R
import com.veryshinnam.myapp.common.component.InstructionText
import com.veryshinnam.myapp.common.component.LoadErrorView
import com.veryshinnam.myapp.common.component.UserInfo
import com.veryshinnam.myapp.core.orientation.OrientationManager
import com.veryshinnam.myapp.feature.classroom.component.ClassroomFilterChips
import com.veryshinnam.myapp.feature.classroom.component.ClassroomLoading
import com.veryshinnam.myapp.feature.classroom.component.ClassroomScaffold
import com.veryshinnam.myapp.feature.collection.component.CollectionCharacterGrid

/**
 * 학급 동화 화면 (동화 모두 보기)
 * : 보관함 화면(CollectionScreen)과 같은 구성에 내용만 학급 동화로 채움
 *   - 상단: 학급 동화 수 + 선택한 과제의 주제 안내
 *   - 과제 필터: 전체 / 과제 1 / 과제 2 ...
 *   - 동화 캐릭터 그리드 (관심 캐릭터는 항상 해제 상태로 내려옴)
 *
 * - classroomId: 학급 아이디
 * - onBack: 뒤로 가기 버튼 클릭 시
 * - onLogoClick: 로고 클릭 시, 홈 화면으로 복구
 * - onItemClick: 캐릭터 클릭 시, 학급 캐릭터 화면으로 이동
 */
@Composable
fun ClassroomStoriesScreen(
    classroomId: Long,
    onBack: () -> Unit,
    onLogoClick: () -> Unit,
    onItemClick: (Long) -> Unit,
    spacerPadding: Dp = 10.dp,
    horizontalPadding: Dp = 16.dp,
    vm: ClassroomStoriesViewModel = hiltViewModel()
) {
    // 상태 구독
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    // 세로 모드 고정
    SideEffect {
        OrientationManager.setOrientation?.invoke(
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        )
    }

    // 화면 진입 시 조회 (캐릭터 화면에서 돌아올 때도 갱신)
    LaunchedEffect(classroomId) { vm.load(classroomId) }

    ClassroomScaffold(
        onBack = onBack,
        onLogoClick = onLogoClick
    ) {
        when (val state = uiState) {
            // 조회 로딩
            is ClassroomStoriesUiState.Loading -> ClassroomLoading()

            // 조회 오류
            is ClassroomStoriesUiState.Error -> {
                LoadErrorView(
                    message = state.message,
                    onRetry = { vm.retry() }
                )
            }

            // 조회 성공
            is ClassroomStoriesUiState.Success -> {
                val stories = state.stories

                // 선택한 과제의 주제 (과제 요약, 없으면 과제명)
                val selected = state.assignments.firstOrNull { it.id == state.selectedAssignmentId }
                val topic = selected?.let { it.description?.takeIf { d -> d.isNotBlank() } ?: it.title }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = horizontalPadding)
                ) {
                    // 상단: 동화 수 + 안내
                    UserInfo(
                        modifier = Modifier.semantics { isTraversalGroup = true },
                        isItem = true,
                        itemCount = stories?.size ?: 0,
                        itemImage = painterResource(R.drawable.img_book),
                        itemDescription = "학급 동화 수",
                        animalImage = painterResource(R.drawable.img_rabbit_cut),
                        screenText = "학급 동화 설명:",
                        cardColor = colorResource(R.color.blue_gray),
                        cardText = if (topic != null) {
                            "선택한 과제의 주제는 '$topic'입니다.\n다른 친구들의 작품도 확인해 보세요!"
                        } else {
                            "우리 반 친구들이 만든 동화예요.\n과제를 골라 모아서 볼 수도 있어요!"
                        },
                        spanText = topic ?: "우리 반 친구들",
                        spanColor = colorResource(R.color.blue_sky)
                    )

                    // 과제 필터
                    Spacer(Modifier.height(spacerPadding))
                    ClassroomFilterChips(
                        assignments = state.assignments,
                        selectedAssignmentId = state.selectedAssignmentId,
                        onSelect = { vm.selectAssignment(it) }
                    )
                    Spacer(Modifier.height(spacerPadding / 2))

                    // 동화 캐릭터 그리드 (3*3)
                    when {
                        // 선택한 과제의 동화 조회 중
                        stories == null -> {
                            Box(Modifier.weight(1f).fillMaxSize(), contentAlignment = Alignment.Center) {
                                ClassroomLoading()
                            }
                        }

                        stories.isEmpty() -> {
                            Box(Modifier.weight(1f).fillMaxSize(), contentAlignment = Alignment.Center) {
                                InstructionText(
                                    text = "아직 완성된 동화가 없어요.\n과제로 만든 동화가 여기에 모여요!",
                                    textStyle = MaterialTheme.typography.titleSmall
                                )
                            }
                        }

                        else -> {
                            CollectionCharacterGrid(
                                data = stories,
                                onFavoriteClick = { }, // 학급 동화는 관심 캐릭터 등록 불가
                                onItemClick = onItemClick,
                                cellPadding = spacerPadding / 2,
                                modifier = Modifier.weight(1f).fillMaxSize(),
                                onCharacterRect = { }
                            )
                        }
                    }
                }
            }
        }
    }
}
