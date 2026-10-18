package com.veryshinnam.myapp.feature.classroom.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.veryshinnam.myapp.core.network.serverMessage
import com.veryshinnam.myapp.feature.character.data.repository.CharacterRepository
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
    private val repository: ClassroomRepository,
    private val characterRepository: CharacterRepository
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

    // 관심 캐릭터 등록/해제 (내가 만든 캐릭터, 친구가 만든 캐릭터 모두 가능)
    // 일반 보관함/홈의 관심 캐릭터와 같은 목록이며 5개 한도를 함께 씀
    fun updateFavorite(id: Long, onError: (String) -> Unit = {}) {
        val state = _uiState.value as? ClassroomStoriesUiState.Success ?: return
        val target = state.stories?.find { it.id == id } ?: return
        val newFavorite = !target.isFavorite

        // ui 먼저 반영
        setFavorite(id, newFavorite)

        // 서버 반영
        viewModelScope.launch {
            try {
                if (newFavorite) characterRepository.addFavorite(id)
                else characterRepository.removeFavorite(id)
            } catch (_: Exception) {
                // 실패 시 원복
                setFavorite(id, !newFavorite)
                onError("좋아하는 캐릭터는 5명까지 등록할 수 있어요!\n(홈의 즐겨찾기와 합쳐서 5명이에요)")
            }
        }
    }

    // 목록에서 해당 캐릭터의 관심 여부만 바꿈
    private fun setFavorite(id: Long, isFavorite: Boolean) {
        _uiState.update { current ->
            if (current is ClassroomStoriesUiState.Success) {
                current.copy(
                    stories = current.stories?.map {
                        if (it.id == id) it.copy(isFavorite = isFavorite) else it
                    }
                )
            } else current
        }
    }

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
