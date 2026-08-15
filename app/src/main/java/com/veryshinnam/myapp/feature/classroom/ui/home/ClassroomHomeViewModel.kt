package com.veryshinnam.myapp.feature.classroom.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.veryshinnam.myapp.feature.classroom.data.model.TeacherClassroomData
import com.veryshinnam.myapp.feature.classroom.data.model.StudentClassroomData
import com.veryshinnam.myapp.feature.classroom.data.repository.ClassroomRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

data class AcornState(
    val isLoading: Boolean = false,
    val message: String? = null
)

sealed interface ClassroomHomeUiState {
    data object Loading : ClassroomHomeUiState
    data class Error(val message: String) : ClassroomHomeUiState
    data class Success(
        val teacherData: TeacherClassroomData?,
        val studentData: StudentClassroomData?
    ) : ClassroomHomeUiState
}

@HiltViewModel
class ClassroomHomeViewModel @Inject constructor(
    private val repository: ClassroomRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ClassroomHomeUiState>(ClassroomHomeUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _acornState = MutableStateFlow(AcornState())
    val acornState = _acornState.asStateFlow()

    init { reload() }

    fun chargeAcorn() {
        viewModelScope.launch {
            _acornState.value = AcornState(isLoading = true)
            try {
                repository.chargeAcorn()
                _acornState.value = AcornState(message = "도토리 5개가 충전됐어요! 🌰")
            } catch (e: HttpException) {
                _acornState.value = AcornState(
                    message = when (e.code()) {
                        400 -> "도토리가 이미 20개입니다. 관리자 승인이 필요해요."
                        else -> "충전에 실패했어요."
                    }
                )
            } catch (e: Exception) {
                _acornState.value = AcornState(message = "오류가 발생했어요.")
            }
        }
    }

    fun clearAcornMessage() { _acornState.value = AcornState() }

    fun reload() {
        viewModelScope.launch {
            _uiState.value = ClassroomHomeUiState.Loading
            try {
                val teacherDeferred = async { runCatching { repository.getTeacherClassrooms() }.getOrNull() }
                val studentDeferred = async { runCatching { repository.getStudentClassrooms() }.getOrNull() }
                _uiState.value = ClassroomHomeUiState.Success(
                    teacherData = teacherDeferred.await(),
                    studentData = studentDeferred.await()
                )
            } catch (e: HttpException) {
                if (e.code() != 401) _uiState.value = ClassroomHomeUiState.Error("일시적인 오류가 발생했어요.")
            } catch (e: Exception) {
                _uiState.value = ClassroomHomeUiState.Error(e.message ?: "오류가 발생했어요.")
            }
        }
    }
}
