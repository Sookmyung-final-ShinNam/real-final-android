package com.veryshinnam.myapp.feature.classroom.ui

import com.veryshinnam.myapp.feature.classroom.model.ClassroomData

sealed interface ClassroomListUiState {
    data object Loading : ClassroomListUiState
    data class Error(val message: String) : ClassroomListUiState

    data class Success(
        val isTeacher: Boolean,
        val nickname: String,
        val classrooms: List<ClassroomData>
    ) : ClassroomListUiState
}
