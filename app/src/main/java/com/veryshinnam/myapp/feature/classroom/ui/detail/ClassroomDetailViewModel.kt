package com.veryshinnam.myapp.feature.classroom.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.veryshinnam.myapp.feature.classroom.data.model.ClassroomDetailData
import com.veryshinnam.myapp.feature.classroom.data.repository.ClassroomRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

sealed interface ClassroomDetailUiState {
    data object Loading : ClassroomDetailUiState
    data class Error(val message: String) : ClassroomDetailUiState
    data class Success(val detail: ClassroomDetailData) : ClassroomDetailUiState
}

@HiltViewModel
class ClassroomDetailViewModel @Inject constructor(
    private val repository: ClassroomRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val classroomId: Long = savedStateHandle["classroomId"] ?: 0L

    private val _uiState = MutableStateFlow<ClassroomDetailUiState>(ClassroomDetailUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _approveResult = MutableStateFlow<String?>(null)
    val approveResult = _approveResult.asStateFlow()

    init { reload() }

    fun reload() {
        viewModelScope.launch {
            _uiState.value = ClassroomDetailUiState.Loading
            try {
                val detail = repository.getClassroomDetail(classroomId)
                _uiState.value = ClassroomDetailUiState.Success(detail)
            } catch (e: HttpException) {
                if (e.code() != 401) _uiState.value = ClassroomDetailUiState.Error("학급 정보를 불러오지 못했어요.")
            } catch (e: Exception) {
                _uiState.value = ClassroomDetailUiState.Error(e.message ?: "오류가 발생했어요.")
            }
        }
    }

    fun approveStudent(studentId: Long) {
        viewModelScope.launch {
            try {
                repository.approveStudent(classroomId, studentId)
                _approveResult.value = "승인 완료!"
                reload()
            } catch (e: Exception) {
                _approveResult.value = "승인에 실패했어요."
            }
        }
    }

    fun clearApproveResult() { _approveResult.value = null }
}
