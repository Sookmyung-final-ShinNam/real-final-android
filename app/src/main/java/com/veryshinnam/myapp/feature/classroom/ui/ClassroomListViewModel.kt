package com.veryshinnam.myapp.feature.classroom.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.veryshinnam.myapp.common.model.UserRole
import com.veryshinnam.myapp.core.network.serverMessage
import com.veryshinnam.myapp.feature.classroom.data.repository.ClassroomRepository
import com.veryshinnam.myapp.feature.classroom.model.JoinStatus
import com.veryshinnam.myapp.feature.settings.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClassroomListViewModel @Inject constructor(
    private val repository: ClassroomRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    // 승인 대기 상태 조회 중 (중복 조회 방지)
    private var isPolling = false

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

    // 승인 대기 중인 학급이 있는 학생: 화면을 벗어나지 않아도 승인 결과를 반영하도록 조용히 다시 조회
    // (화면이 보이는 동안 주기적으로 호출됨. 실패해도 화면을 바꾸지 않고 다음 주기에 다시 시도)
    fun refreshPending() {
        val current = _uiState.value as? ClassroomListUiState.Success ?: return
        val waitingIds = current.classrooms
            .filter { it.joinStatus == JoinStatus.PENDING }
            .map { it.id }
            .toSet()

        if (current.isTeacher || waitingIds.isEmpty() || isPolling) return
        isPolling = true

        viewModelScope.launch {
            try {
                val data = repository.getClassrooms()

                // 대기 중이던 학급 중 승인된 학급
                val approvedNames = data.classrooms
                    .filter { it.id in waitingIds && it.joinStatus != JoinStatus.PENDING }
                    .map { it.name }

                _uiState.update { state ->
                    if (state is ClassroomListUiState.Success) {
                        state.copy(nickname = data.nickname, classrooms = data.classrooms)
                    } else state
                }

                if (approvedNames.isNotEmpty()) {
                    _toast.tryEmit("${approvedNames.joinToString(", ")} 학급 가입이 승인됐어요! 이제 들어갈 수 있어요.")
                }
            } catch (_: Exception) {
                // 일시적인 오류는 무시
            } finally {
                isPolling = false
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
