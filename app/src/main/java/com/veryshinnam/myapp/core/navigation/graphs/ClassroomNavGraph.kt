package com.veryshinnam.myapp.core.navigation.graphs

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.veryshinnam.myapp.core.navigation.routes.ClassroomRoutes
import com.veryshinnam.myapp.core.navigation.utils.navigateTo
import com.veryshinnam.myapp.core.navigation.utils.navigateToHome
import com.veryshinnam.myapp.feature.classroom.ui.create.CreateClassroomScreen
import com.veryshinnam.myapp.feature.classroom.ui.detail.ClassroomDetailScreen
import com.veryshinnam.myapp.feature.classroom.ui.assignment.AssignmentListScreen
import com.veryshinnam.myapp.feature.classroom.ui.home.ClassroomHomeScreen
import com.veryshinnam.myapp.feature.classroom.ui.join.JoinClassroomScreen
import com.veryshinnam.myapp.feature.classroom.ui.stories.ClassroomStoriesScreen

fun NavGraphBuilder.classroomNavGraph(navController: NavController) {

    composable(ClassroomRoutes.CLASSROOM_HOME) {
        ClassroomHomeScreen(
            onBack = { navController.popBackStack() },
            onLogoClick = { navController.navigateToHome() },
            onCreateClick = { navController.navigateTo(ClassroomRoutes.CREATE_CLASSROOM) },
            onJoinClick = { navController.navigateTo(ClassroomRoutes.JOIN_CLASSROOM) },
            onClassroomClick = { classroomId ->
                navController.navigateTo(ClassroomRoutes.classroomDetail(classroomId))
            }
        )
    }

    composable(ClassroomRoutes.CREATE_CLASSROOM) {
        CreateClassroomScreen(
            onBack = { navController.popBackStack() },
            onSuccess = { navController.popBackStack() }
        )
    }

    composable(ClassroomRoutes.JOIN_CLASSROOM) {
        JoinClassroomScreen(
            onBack = { navController.popBackStack() },
            onSuccess = { navController.popBackStack() }
        )
    }

    composable(
        ClassroomRoutes.CLASSROOM_DETAIL,
        arguments = listOf(navArgument("classroomId") { type = NavType.LongType })
    ) { backStackEntry ->
        val classroomId = backStackEntry.arguments?.getLong("classroomId") ?: return@composable
        ClassroomDetailScreen(
            classroomId = classroomId,
            onBack = { navController.popBackStack() },
            onAssignmentsClick = { isTeacher ->
                navController.navigateTo(ClassroomRoutes.assignmentList(classroomId, isTeacher))
            },
            onStoriesClick = { week ->
                navController.navigateTo(ClassroomRoutes.classroomStories(classroomId, week))
            }
        )
    }

    composable(
        ClassroomRoutes.ASSIGNMENT_LIST,
        arguments = listOf(
            navArgument("classroomId") { type = NavType.LongType },
            navArgument("isTeacher") { type = NavType.BoolType }
        )
    ) { backStackEntry ->
        val classroomId = backStackEntry.arguments?.getLong("classroomId") ?: return@composable
        val isTeacher = backStackEntry.arguments?.getBoolean("isTeacher") ?: false
        AssignmentListScreen(
            classroomId = classroomId,
            isTeacher = isTeacher,
            onBack = { navController.popBackStack() },
            onStartAssignment = { navController.navigate(NavGraphs.CREATION) }
        )
    }

    composable(
        ClassroomRoutes.CLASSROOM_STORIES,
        arguments = listOf(
            navArgument("classroomId") { type = NavType.LongType },
            navArgument("week") { type = NavType.IntType }
        )
    ) { backStackEntry ->
        val classroomId = backStackEntry.arguments?.getLong("classroomId") ?: return@composable
        val week = backStackEntry.arguments?.getInt("week") ?: 1
        ClassroomStoriesScreen(
            classroomId = classroomId,
            week = week,
            onBack = { navController.popBackStack() }
        )
    }
}
