package com.veryshinnam.myapp.feature.classroom.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.veryshinnam.myapp.core.network.serverMessage
import com.veryshinnam.myapp.feature.classroom.data.repository.ClassroomRepository
import com.veryshinnam.myapp.feature.classroom.model.AssignmentData
import com.veryshinnam.myapp.feature.collection.model.CollectionData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ClassroomStoriesUiState {
    data object Loading : ClassroomStoriesUiState
    data class Error(val message: String) : ClassroomStoriesUiState

    data class Success(
        val assignments: List<AssignmentData>,          // 과제 필터 (과제 1, 과제 2 ...)
        val selectedAssignmentId: Long? = null,         // null: 전체
        val stories: List<CollectionData>?              // null: 선택한 과제의 동화를 불러오는 중
    ) : ClassroomStoriesUiState
}

@HiltViewModel
class ClassroomStoriesViewModel @Inject constructor(
    private val repository: ClassroomRepository
) : ViewModel() {

    private var classroomId: Long = -1L

    // 화면 전체 ui 상태
    private val _uiState = MutableStateFlow<ClassroomStoriesUiState>(ClassroomStoriesUiState.Loading)
    val uiState: StateFlow<ClassroomStoriesUiState> = _uiState.asStateFlow()

    // 과제 목록 + 동화 목록 조회 (화면 진입 시)
    fun load(classroomId: Long) {
        this.classroomId = classroomId

        // 이미 보여주는 목록이 있으면 로딩 화면 없이 갱신 (선택한 과제 유지)
        val selected = (_uiState.value as? ClassroomStoriesUiState.Success)?.selectedAssignmentId
        if (_uiState.value !is ClassroomStoriesUiState.Success) {
            _uiState.value = ClassroomStoriesUiState.Loading
        }

        viewModelScope.launch {
            try {
                val assignments = repository.getAssignments(classroomId)
                // 삭제 등으로 사라진 과제를 선택하고 있었다면 전체로
                val filter = selected?.takeIf { id -> assignments.any { it.id == id } }
                val stories = repository.getStories(classroomId, filter)

                _uiState.value = ClassroomStoriesUiState.Success(
                    assignments = assignments,
                    selectedAssignmentId = filter,
                    stories = stories
                )
            } catch (e: Exception) {
                _uiState.value = ClassroomStoriesUiState.Error(
                    e.serverMessage("학급 동화를 불러오지 못했어요.")
                )
            }
        }
    }

    // 다시 시도 (오류 화면)
    fun retry() = load(classroomId)

    // 과제 필터 선택 (null: 전체)
    fun selectAssignment(assignmentId: Long?) {
        val state = _uiState.value as? ClassroomStoriesUiState.Success ?: return
        if (state.selectedAssignmentId == assignmentId) return

        _uiState.value = state.copy(selectedAssignmentId = assignmentId, stories = null)

        viewModelScope.launch {
            try {
                val stories = repository.getStories(classroomId, assignmentId)
                _uiState.update { current ->
                    // 조회 중 다른 과제로 바뀌었다면 이전 응답은 버림
                    if (current is ClassroomStoriesUiState.Success && current.selectedAssignmentId == assignmentId) {
                        current.copy(stories = stories)
                    } else current
                }
            } catch (e: Exception) {
                _uiState.value = ClassroomStoriesUiState.Error(
                    e.serverMessage("학급 동화를 불러오지 못했어요.")
                )
            }
        }
    }
}
