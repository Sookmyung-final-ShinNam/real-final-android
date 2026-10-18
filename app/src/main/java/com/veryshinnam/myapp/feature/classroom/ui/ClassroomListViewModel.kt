package com.veryshinnam.myapp.feature.classroom.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.veryshinnam.myapp.common.model.UserRole
import com.veryshinnam.myapp.core.network.serverMessage
import com.veryshinnam.myapp.feature.classroom.data.repository.ClassroomRepository
import com.veryshinnam.myapp.feature.settings.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClassroomListViewModel @Inject constructor(
    private val repository: ClassroomRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    // 화면 전체 ui 상태
    private val _uiState = MutableStateFlow<ClassroomListUiState>(ClassroomListUiState.Loading)
    val uiState: StateFlow<ClassroomListUiState> = _uiState.asStateFlow()

    // 경고창 문구 (null이면 닫힘)
    private val _warning = MutableStateFlow<String?>(null)
    val warning: StateFlow<String?> = _warning.asStateFlow()

    // 학급 생성 직후 발급된 학급 코드 (null이면 닫힘)
    private val _createdCode = MutableStateFlow<String?>(null)
    val createdCode: StateFlow<String?> = _createdCode.asStateFlow()

    // 토스트 이벤트
    private val _toast = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val toast: SharedFlow<String> = _toast.asSharedFlow()

    // 학급 목록 조회
    fun load() {
        viewModelScope.launch {
            // 이미 보여주는 목록이 있으면 로딩 화면 없이 갱신
            if (_uiState.value !is ClassroomListUiState.Success) {
                _uiState.value = ClassroomListUiState.Loading
            }

            try {
                val isTeacher = userRepository.checkRole(UserRole.TEACHER)
                val data = repository.getClassrooms()

                _uiState.value = ClassroomListUiState.Success(
                    isTeacher = isTeacher,
                    nickname = data.nickname,
                    classrooms = data.classrooms
                )
            } catch (e: Exception) {
                _uiState.value = ClassroomListUiState.Error(
                    e.serverMessage("학급 목록을 불러오지 못했어요.")
                )
            }
        }
    }

    // 학급 생성 (선생님)
    fun createClassroom(name: String) {
        viewModelScope.launch {
            try {
                _createdCode.value = repository.createClassroom(name.trim())
                load()
            } catch (e: Exception) {
                _warning.value = e.serverMessage("학급을 만들지 못했어요.\n잠시 후 다시 시도해 주세요.")
            }
        }
    }

    // 학급 가입 신청 (학생)
    fun joinClassroom(code: String) {
        viewModelScope.launch {
            try {
                repository.joinClassroom(code.trim().uppercase())
                _toast.tryEmit("가입을 신청했어요! 선생님이 승인하면 활동할 수 있어요.")
                load()
            } catch (e: Exception) {
                _warning.value = e.serverMessage("학급에 가입하지 못했어요.\n잠시 후 다시 시도해 주세요.")
            }
        }
    }

    fun showWarning(text: String) {
        _warning.value = text
    }

    fun hideWarning() {
        _warning.value = null
    }

    fun hideCreatedCode() {
        _createdCode.value = null
    }
}
