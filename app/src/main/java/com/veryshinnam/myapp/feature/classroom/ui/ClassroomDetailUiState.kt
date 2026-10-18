package com.veryshinnam.myapp.feature.classroom.ui

import com.veryshinnam.myapp.feature.classroom.model.AssignmentData
import com.veryshinnam.myapp.feature.classroom.model.ClassroomDetailData

sealed interface ClassroomDetailUiState {
    data object Loading : ClassroomDetailUiState
    data class Error(val message: String) : ClassroomDetailUiState

    data class Success(
        val isTeacher: Boolean,
        val detail: ClassroomDetailData,
        // 학생 학급 화면의 임박한 과제/제출 현황용 (선생님은 사용하지 않음)
        val assignments: List<AssignmentData> = emptyList()
    ) : ClassroomDetailUiState
}
