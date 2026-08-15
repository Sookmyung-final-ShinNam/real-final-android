package com.veryshinnam.myapp.feature.classroom.ui.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.veryshinnam.myapp.feature.classroom.data.repository.ClassroomRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

enum class CreateStep { EMAIL, VERIFY, NAME }

data class CreateClassroomUiState(
    val step: CreateStep = CreateStep.EMAIL,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val generatedCode: String? = null,
    val isDone: Boolean = false
)

@HiltViewModel
class CreateClassroomViewModel @Inject constructor(
    private val repository: ClassroomRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateClassroomUiState())
    val uiState = _uiState.asStateFlow()

    fun sendEmailVerification(email: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                repository.sendEmailVerification(email)
                _uiState.value = _uiState.value.copy(isLoading = false, step = CreateStep.VERIFY)
            } catch (e: HttpException) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = when (e.code()) {
                        400 -> "이미 인증된 이메일이에요."
                        else -> "이메일 전송에 실패했어요."
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = "오류가 발생했어요.")
            }
        }
    }

    fun verifyEmail(code: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                repository.verifyEmail(code)
                _uiState.value = _uiState.value.copy(isLoading = false, step = CreateStep.NAME)
            } catch (e: HttpException) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = when (e.code()) {
                        400 -> "인증코드가 만료됐거나 일치하지 않아요."
                        else -> "인증에 실패했어요."
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = "오류가 발생했어요.")
            }
        }
    }

    fun createClassroom(name: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val code = repository.createClassroom(name)
                _uiState.value = _uiState.value.copy(isLoading = false, generatedCode = code)
            } catch (e: HttpException) {
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = "학급 생성에 실패했어요.")
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = "오류가 발생했어요.")
            }
        }
    }

    fun clearError() { _uiState.value = _uiState.value.copy(errorMessage = null) }
    fun confirmDone() { _uiState.value = _uiState.value.copy(isDone = true) }
}
