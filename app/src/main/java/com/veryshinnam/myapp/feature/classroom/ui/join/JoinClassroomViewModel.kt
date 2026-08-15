package com.veryshinnam.myapp.feature.classroom.ui.join

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.veryshinnam.myapp.feature.classroom.data.repository.ClassroomRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

data class JoinUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

@HiltViewModel
class JoinClassroomViewModel @Inject constructor(
    private val repository: ClassroomRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(JoinUiState())
    val uiState = _uiState.asStateFlow()

    fun joinClassroom(code: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                repository.joinClassroom(code)
                _uiState.value = _uiState.value.copy(isLoading = false, isSuccess = true)
            } catch (e: HttpException) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = when (e.code()) {
                        400 -> "이미 가입하거나 가입 요청한 학급이에요."
                        404 -> "학급 코드를 찾을 수 없어요. 다시 확인해주세요."
                        else -> "가입 요청에 실패했어요."
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = "오류가 발생했어요.")
            }
        }
    }

    fun clearError() { _uiState.value = _uiState.value.copy(errorMessage = null) }
}
