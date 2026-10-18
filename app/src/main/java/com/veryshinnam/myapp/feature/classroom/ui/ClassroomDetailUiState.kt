package com.veryshinnam.myapp.feature.classroom.ui

import com.veryshinnam.myapp.feature.classroom.model.AssignmentData
import com.veryshinnam.myapp.feature.classroom.model.ClassroomDetailData
import com.veryshinnam.myapp.feature.collection.model.CollectionData

sealed interface ClassroomDetailUiState {
    data object Loading : ClassroomDetailUiState
    data class Error(val message: String) : ClassroomDetailUiState

    data class Success(
        val isTeacher: Boolean,
        val detail: ClassroomDetailData,
        // 학생 학급 화면의 임박한 과제/제출 현황용 (선생님은 사용하지 않음)
        val assignments: List<AssignmentData> = emptyList(),
        // 학생 학급 화면의 즐겨찾기 카드용: 이 학급 동화 중 내가 관심 캐릭터로 등록한 것 (내가 만든 것 + 친구가 만든 것)
        val favorites: List<CollectionData> = emptyList()
    ) : ClassroomDetailUiState
}
