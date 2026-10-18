package com.veryshinnam.myapp.feature.classroom.ui

import com.veryshinnam.myapp.feature.classroom.model.ClassroomDetailData

sealed interface ClassroomDetailUiState {
    data object Loading : ClassroomDetailUiState
    data class Error(val message: String) : ClassroomDetailUiState

    data class Success(
        val isTeacher: Boolean,
        val detail: ClassroomDetailData
    ) : ClassroomDetailUiState
}
