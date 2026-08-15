package com.veryshinnam.myapp.feature.classroom.ui.assignment

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.veryshinnam.myapp.feature.classroom.data.model.AssignmentData
import com.veryshinnam.myapp.feature.classroom.data.repository.ClassroomRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

sealed interface AssignmentListUiState {
    data object Loading : AssignmentListUiState
    data class Error(val message: String) : AssignmentListUiState
    data class Success(val assignments: List<AssignmentData>) : AssignmentListUiState
}

@HiltViewModel
class AssignmentListViewModel @Inject constructor(
    private val repository: ClassroomRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val classroomId: Long = savedStateHandle["classroomId"] ?: 0L

    private val _uiState = MutableStateFlow<AssignmentListUiState>(AssignmentListUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init { reload() }

    fun reload() {
        viewModelScope.launch {
            _uiState.value = AssignmentListUiState.Loading
            try {
                val assignments = repository.getClassroomAssignments(classroomId)
                _uiState.value = AssignmentListUiState.Success(assignments)
            } catch (e: HttpException) {
                if (e.code() != 401) _uiState.value = AssignmentListUiState.Error("과제 목록을 불러오지 못했어요.")
            } catch (e: Exception) {
                _uiState.value = AssignmentListUiState.Error(e.message ?: "오류가 발생했어요.")
            }
        }
    }
}
