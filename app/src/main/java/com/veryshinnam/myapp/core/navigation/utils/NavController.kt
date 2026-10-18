package com.veryshinnam.myapp.core.navigation.utils

import androidx.navigation.NavController
import com.veryshinnam.myapp.core.navigation.graphs.NavGraphs
import com.veryshinnam.myapp.core.navigation.routes.CreationRoutes
import com.veryshinnam.myapp.core.navigation.routes.MainRoutes
import com.veryshinnam.myapp.feature.creation.model.ResumeRequest

// route로 이동
fun NavController.navigateTo(route: String) {
    navigate(route) {
        launchSingleTop = true // 백스택에 있으면 재사용
    }
}

// 홈으로 이동
fun NavController.navigateToHome() {
    navigate(MainRoutes.HOME) {
        popUpTo(NavGraphs.MAIN) {
            inclusive = false   // 그래프 안 홈 위 백스택 비우기
        }
        launchSingleTop = true  // 백스택에 있으면 재사용
    }
}

// 끝내지 않은 동화 이어하기: 선택 화면 없이 대화 화면으로 바로 이동
// (대화 화면이 이전 화면(현재 화면)에 남긴 요청을 읽어 해당 세션을 이어서 진행)
fun NavController.navigateToResume(request: ResumeRequest) {
    currentBackStackEntry
        ?.savedStateHandle
        ?.set("resumeSession", request)

    navigate(CreationRoutes.CONVERSATION)
}

// 홈으로 강제 리셋
fun NavController.resetToHome() {
    navigate(MainRoutes.HOME) {
        popUpTo(0) // 백스택 모두 비우기
        launchSingleTop = true // 있으면 재사용
    }
}