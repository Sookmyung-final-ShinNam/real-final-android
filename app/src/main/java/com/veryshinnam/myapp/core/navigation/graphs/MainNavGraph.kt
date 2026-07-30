package com.veryshinnam.myapp.core.navigation.graphs

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.veryshinnam.myapp.core.navigation.routes.MainRoutes
import com.veryshinnam.myapp.core.navigation.utils.navigateTo
import com.veryshinnam.myapp.core.navigation.utils.navigateToHome
import com.veryshinnam.myapp.core.navigation.utils.resetToHome
import com.veryshinnam.myapp.feature.admin.ui.AdminScreen
import com.veryshinnam.myapp.feature.attendance.ui.AttendanceScreen
import com.veryshinnam.myapp.feature.character.ui.CharacterScreen
import com.veryshinnam.myapp.feature.collection.ui.CollectionScreen
import com.veryshinnam.myapp.feature.dashboard.ui.DashboardScreen
import com.veryshinnam.myapp.feature.home.ui.HomeScreen
import com.veryshinnam.myapp.feature.settings.ui.SettingsScreen
import com.veryshinnam.myapp.feature.story.model.StoryType
import com.veryshinnam.myapp.feature.story.ui.StoryScreen

fun NavGraphBuilder.mainNavGraph(
    navController: NavController
) {
    navigation(
        startDestination = MainRoutes.HOME,
        route = NavGraphs.MAIN
    ) {
        // 홈 화면
        composable(MainRoutes.HOME) {
            HomeScreen(
                onSettingsClick = { navController.navigate(MainRoutes.SETTINGS) },
                onAttendanceClick = { navController.navigate(MainRoutes.ATTENDANCE) },
                onDashboardClick = { navController.navigate(MainRoutes.DASHBOARD) },
                onCreationClick = { navController.navigate(NavGraphs.CREATION) },
                onCollectionClick = { navController.navigate(MainRoutes.COLLECTION) },
                onCharacterClick = { charId ->
                    navController.navigate("character/$charId")
                }
            )
        }

        // 환경설정 화면
        composable(MainRoutes.SETTINGS) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onLogoClick = { navController.navigateToHome() }
            )
        }

        // 출석체크 화면
        composable(MainRoutes.ATTENDANCE) {
            AttendanceScreen(
                onBack = { navController.popBackStack() },
                onLogoClick = { navController.navigateToHome() },
                goToNextManual = { navController.navigateTo(MainRoutes.DASHBOARD) },
                onManualStop = { navController.resetToHome() }
            )
        }

        // 대시보드 화면
        composable(MainRoutes.DASHBOARD) {
            DashboardScreen(
                onBack = { navController.popBackStack() },
                onLogoClick = { navController.navigateToHome() },
                goToCharacter = { characterId ->
                    navController.navigateTo("character/$characterId")
                },
                goToCreation = {
                    navController.navigate(NavGraphs.CREATION) {
                        popUpTo(NavGraphs.MAIN) { inclusive = false }
                    }
                },
                onManualStop = { navController.resetToHome() }
            )
        }

        // 보관함 화면
        composable(MainRoutes.COLLECTION) {
            CollectionScreen(
                onBack = { navController.popBackStack() },
                onLogoClick = { navController.navigateToHome() },
                onItemClick = { id -> navController.navigate("character/$id") },
                goToCreation = {
                    navController.navigate(NavGraphs.CREATION) {
                        popUpTo(NavGraphs.MAIN) { inclusive = false }
                    }
                },
                goToNextManual = { navController.navigateTo("character/-1") },
                onManualStop = { navController.resetToHome() }
            )
        }

        // 캐릭터 상세 화면
        composable(
            MainRoutes.CHARACTER,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("id") ?: return@composable
            CharacterScreen(
                id = id,
                onBack = { navController.popBackStack() },
                onStoryClick = { storyId, type ->
                    navController.navigateTo("story/$storyId/${type.name}")
                },
                onLogoClick = { navController.navigateToHome() },
                goToNextManual = { navController.navigateTo(MainRoutes.ATTENDANCE) },
                onManualStop = { navController.resetToHome() }
            )
        }

        // 동화 보기 화면
        composable(
            MainRoutes.STORY,
            arguments = listOf(
                navArgument("id") { type = NavType.LongType },
                navArgument("type") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val storyId = backStackEntry.arguments?.getLong("id") ?: return@composable
            val storyType = backStackEntry.arguments?.getString("type")
                ?.let { StoryType.valueOf(it) }
                ?: StoryType.IMAGE

            StoryScreen(
                storyId = storyId,
                storyType = storyType,
                onBack = { navController.popBackStack() },
                onLogoClick = { navController.navigateToHome() }
            )
        }

        // 관리자 화면
        composable(MainRoutes.ADMIN) {
            AdminScreen(
                onStoryClick = { storyId, type ->
                    navController.navigateTo("story/$storyId/${type.name}")
                }
            )
        }
    }
}