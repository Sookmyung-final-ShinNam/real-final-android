package com.veryshinnam.myapp.feature.classroom.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.veryshinnam.myapp.core.network.serverMessage
import com.veryshinnam.myapp.feature.classroom.data.repository.ClassroomRepository
import com.veryshinnam.myapp.feature.classroom.model.AssignmentData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface AssignmentListUiState {
    data object Loading : AssignmentListUiState
    data class Error(val message: String) : AssignmentListUiState

    data class Success(
        val classroomName: String,
        val assignments: List<AssignmentData>
    ) : AssignmentListUiState
}

@HiltViewModel
class AssignmentListViewModel @Inject constructor(
    private val repository: ClassroomRepository
) : ViewModel() {

    private var classroomId: Long = -1L

    // 화면 전체 ui 상태
    private val _uiState = MutableStateFlow<AssignmentListUiState>(AssignmentListUiState.Loading)
    val uiState: StateFlow<AssignmentListUiState> = _uiState.asStateFlow()

    // 학급 이름 + 과제 목록 조회 (화면 진입 시, 과제 등록/동화 생성 후 복귀 시)
    fun load(classroomId: Long) {
        this.classroomId = classroomId

        // 이미 보여주는 목록이 있으면 로딩 화면 없이 갱신
        if (_uiState.value !is AssignmentListUiState.Success) {
            _uiState.value = AssignmentListUiState.Loading
        }

        viewModelScope.launch {
            try {
                val detail = repository.getClassroomDetail(classroomId)
                val assignments = repository.getAssignments(classroomId)

                _uiState.value = AssignmentListUiState.Success(
                    classroomName = detail.name,
                    assignments = assignments
                )
            } catch (e: Exception) {
                _uiState.value = AssignmentListUiState.Error(
                    e.serverMessage("과제 목록을 불러오지 못했어요.")
                )
            }
        }
    }

    // 다시 시도 (오류 화면)
    fun retry() = load(classroomId)
}
