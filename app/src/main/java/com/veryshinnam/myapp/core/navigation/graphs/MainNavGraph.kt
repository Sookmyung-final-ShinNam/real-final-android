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
import com.veryshinnam.myapp.feature.classroom.model.AssignmentTarget
import com.veryshinnam.myapp.feature.classroom.ui.AssignmentCreateScreen
import com.veryshinnam.myapp.feature.classroom.ui.AssignmentListScreen
import com.veryshinnam.myapp.feature.classroom.ui.ClassroomCharacterScreen
import com.veryshinnam.myapp.feature.classroom.ui.ClassroomDetailScreen
import com.veryshinnam.myapp.feature.classroom.ui.ClassroomListScreen
import com.veryshinnam.myapp.feature.classroom.ui.ClassroomStoriesScreen
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
                onLogoClick = { navController.navigateToHome() },
                onClassroomClick = { navController.navigate(MainRoutes.CLASSROOMS) }
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

        // 나의 학급 화면 (학급 목록)
        composable(MainRoutes.CLASSROOMS) {
            ClassroomListScreen(
                onBack = { navController.popBackStack() },
                onLogoClick = { navController.navigateToHome() },
                onClassroomClick = { classroomId, isTeacher ->
                    navController.navigate("classroom_detail/$classroomId/$isTeacher")
                }
            )
        }

        // 학급 상세 화면
        composable(
            MainRoutes.CLASSROOM_DETAIL,
            arguments = listOf(
                navArgument("id") { type = NavType.LongType },
                navArgument("isTeacher") { type = NavType.BoolType }
            )
        ) { backStackEntry ->
            val classroomId = backStackEntry.arguments?.getLong("id") ?: return@composable
            val isTeacher = backStackEntry.arguments?.getBoolean("isTeacher") ?: false

            ClassroomDetailScreen(
                classroomId = classroomId,
                isTeacher = isTeacher,
                onBack = { navController.popBackStack() },
                onLogoClick = { navController.navigateToHome() },
                onAssignmentsClick = { navController.navigate("classroom_assignments/$classroomId/$isTeacher") },
                onStoriesClick = { navController.navigate("classroom_stories/$classroomId") },
                onMyStoriesClick = { navController.navigate(MainRoutes.COLLECTION) } // 내 동화 = 기존 보관함
            )
        }

        // 학급 과제 목록 화면
        composable(
            MainRoutes.CLASSROOM_ASSIGNMENTS,
            arguments = listOf(
                navArgument("id") { type = NavType.LongType },
                navArgument("isTeacher") { type = NavType.BoolType }
            )
        ) { backStackEntry ->
            val classroomId = backStackEntry.arguments?.getLong("id") ?: return@composable
            val isTeacher = backStackEntry.arguments?.getBoolean("isTeacher") ?: false

            AssignmentListScreen(
                classroomId = classroomId,
                isTeacher = isTeacher,
                onBack = { navController.popBackStack() },
                onLogoClick = { navController.navigateToHome() },
                onCreateAssignment = { navController.navigate("classroom_assignment_new/$classroomId") },
                onStartAssignment = { assignmentId ->
                    // 생성 플로우(선택 화면)가 이 값을 읽어 과제로 동화를 시작
                    navController.currentBackStackEntry
                        ?.savedStateHandle
                        ?.set("assignmentTarget", AssignmentTarget(classroomId, assignmentId))

                    navController.navigate(NavGraphs.CREATION)
                }
            )
        }

        // 과제 만들기 화면 (선생님)
        composable(
            MainRoutes.CLASSROOM_ASSIGNMENT_NEW,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->
            val classroomId = backStackEntry.arguments?.getLong("id") ?: return@composable

            AssignmentCreateScreen(
                classroomId = classroomId,
                onBack = { navController.popBackStack() },
                onLogoClick = { navController.navigateToHome() }
            )
        }

        // 학급 동화 모두 보기 화면 (보관함 화면 재사용)
        composable(
            MainRoutes.CLASSROOM_STORIES,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->
            val classroomId = backStackEntry.arguments?.getLong("id") ?: return@composable

            ClassroomStoriesScreen(
                classroomId = classroomId,
                onBack = { navController.popBackStack() },
                onLogoClick = { navController.navigateToHome() },
                onItemClick = { characterId ->
                    navController.navigate("classroom_character/$characterId")
                }
            )
        }

        // 학급 동화 캐릭터 화면 (캐릭터 화면 재사용, 탭 없는 단일 화면)
        composable(
            MainRoutes.CLASSROOM_CHARACTER,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("id") ?: return@composable

            ClassroomCharacterScreen(
                id = id,
                onBack = { navController.popBackStack() },
                onLogoClick = { navController.navigateToHome() },
                onStoryClick = { storyId, type ->
                    navController.navigateTo("story/$storyId/${type.name}")
                }
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