package com.veryshinnam.myapp.core.navigation.graphs

import android.util.Log
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.veryshinnam.myapp.core.navigation.routes.CreationRoutes
import com.veryshinnam.myapp.core.navigation.routes.MainRoutes
import com.veryshinnam.myapp.feature.classroom.model.AssignmentTarget
import com.veryshinnam.myapp.feature.creation.ui.conversation.ConversationScreen
import com.veryshinnam.myapp.feature.creation.ui.conversation.ConversationViewModel
import com.veryshinnam.myapp.feature.creation.data.dto.StartRequest
import com.veryshinnam.myapp.feature.creation.data.dto.toStartRequest
import com.veryshinnam.myapp.feature.creation.model.ResumeRequest
import com.veryshinnam.myapp.feature.creation.ui.selection.SelectionScreen

// 캐릭터 및 동화 생성 플로우 네비게이션 그래프
fun NavGraphBuilder.creationNavGraph(
    navController: NavController
) {
    navigation(
        route = NavGraphs.CREATION,
        startDestination = CreationRoutes.SELECTION
    ) {
        // 선택 화면
        composable(CreationRoutes.SELECTION) {
            SelectionScreen(
                onHome = {
                    navController.navigate(MainRoutes.HOME) {
                        popUpTo(NavGraphs.MAIN) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onFinish = { data ->
                    val req = data.toStartRequest()

                    // 학급 과제에서 진입한 경우, 이전 화면(학급 상세)이 남긴 과제 대상을 함께 전달
                    val target = navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.get<AssignmentTarget>("assignmentTarget")

                    navController.currentBackStackEntry
                        ?.savedStateHandle
                        ?.apply {
                            set("request", req)
                            set("assignmentTarget", target)
                        }

                    navController.navigate(CreationRoutes.CONVERSATION) {
                        launchSingleTop = true
                    }
                },
                goToNextManual = {
                    navController.navigate(CreationRoutes.CONVERSATION) {
                        launchSingleTop = true
                    }
                }
            )
        }

        // llm 대화 화면
        composable(CreationRoutes.CONVERSATION) { backStackEntry ->
            val vm: ConversationViewModel = hiltViewModel()

            // 이어하기: 이전 화면(홈, 학급 과제 목록)이 남긴 이어하기 요청
            // (선택 화면을 거친 일반 생성 흐름에서는 이전 화면이 선택 화면이라 값이 없음)
            val resume = remember(backStackEntry) {
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.get<ResumeRequest>("resumeSession")
            }

            // 선택 화면이 백스택에 없으면(이어하기로 바로 진입) 값 없음
            val req = remember(backStackEntry) {
                runCatching {
                    navController.getBackStackEntry(CreationRoutes.SELECTION)
                        .savedStateHandle
                        .get<StartRequest>("request")
                }.getOrNull()
            }

            val target = remember(backStackEntry) {
                runCatching {
                    navController.getBackStackEntry(CreationRoutes.SELECTION)
                        .savedStateHandle
                        .get<AssignmentTarget>("assignmentTarget")
                }.getOrNull()
            }

            Log.d("conversation", "$req, $target, $resume")
            LaunchedEffect(req, resume) {
                if (resume != null) {
                    vm.resumeConversation(resume) // 끝내지 않은 세션을 이어서 진행
                }
                else if (req != null) {
                    vm.startConversation(req, target) // api 호출, 동화 세선 시작 (target이 있으면 과제)
                }
                else {
                    vm.startManual()            // 매뉴얼 모드 시작
                }
            }

            ConversationScreen(
                onHome = {
                    // 선택 화면 스택 모두 제거
                    navController.navigate(MainRoutes.HOME) {
                        popUpTo(NavGraphs.MAIN) {
                            inclusive = false
                        }
                        launchSingleTop = true
                    }
                },
                goToNextManual = {
                    navController.navigate(MainRoutes.COLLECTION) {
                        launchSingleTop = true
                    }
                },
                vm = vm
            )
        }
    }
}