package com.veryshinnam.myapp.feature.classroom.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.veryshinnam.myapp.core.network.serverMessage
import com.veryshinnam.myapp.feature.classroom.data.repository.ClassroomRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// 학급 도토리 최대 보유 개수 (서버 기준)
private const val MAX_ACORN = 20

private const val ACORN_LIMIT_MESSAGE =
    "도토리는 ${MAX_ACORN}개까지만 가능합니다.\n더 많은 도토리가 필요하다면 관리자에게 문의바랍니다."

@HiltViewModel
class ClassroomDetailViewModel @Inject constructor(
    private val repository: ClassroomRepository
) : ViewModel() {

    private var classroomId: Long = -1L
    private var isTeacher: Boolean = false

    // 화면 전체 ui 상태
    private val _uiState = MutableStateFlow<ClassroomDetailUiState>(ClassroomDetailUiState.Loading)
    val uiState: StateFlow<ClassroomDetailUiState> = _uiState.asStateFlow()

    // 경고창 문구 (null이면 닫힘)
    private val _warning = MutableStateFlow<String?>(null)
    val warning: StateFlow<String?> = _warning.asStateFlow()

    // 토스트 이벤트
    private val _toast = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val toast: SharedFlow<String> = _toast.asSharedFlow()

    // 학급 상세 조회 (화면 진입 시)
    fun load(classroomId: Long, isTeacher: Boolean) {
        this.classroomId = classroomId
        this.isTeacher = isTeacher

        if (_uiState.value !is ClassroomDetailUiState.Success) {
            _uiState.value = ClassroomDetailUiState.Loading
        }
        viewModelScope.launch { refresh(initial = true) }
    }

    // 다시 시도 (오류 화면)
    fun retry() = load(classroomId, isTeacher)

    private suspend fun refresh(initial: Boolean = false) {
        try {
            _uiState.value = ClassroomDetailUiState.Success(
                isTeacher = isTeacher,
                detail = repository.getClassroomDetail(classroomId)
            )
        } catch (e: Exception) {
            val message = e.serverMessage("학급 정보를 불러오지 못했어요.")
            // 이미 보여주는 화면이 있으면 유지하고 경고만 표시
            if (initial || _uiState.value !is ClassroomDetailUiState.Success) {
                _uiState.value = ClassroomDetailUiState.Error(message)
            } else {
                _warning.value = message
            }
        }
    }

    // 학생 가입 승인 (선생님)
    fun approveStudent(studentId: Long) {
        viewModelScope.launch {
            try {
                repository.approveStudent(classroomId, studentId)
                _toast.tryEmit("가입을 승인했어요.")
                refresh()
            } catch (e: Exception) {
                _warning.value = e.serverMessage("가입을 승인하지 못했어요.\n잠시 후 다시 시도해 주세요.")
            }
        }
    }

    // 학급 도토리 충전 (선생님, 5개씩 최대 20개)
    fun chargeAcorn() {
        val before = (_uiState.value as? ClassroomDetailUiState.Success)?.detail?.points

        // 이미 최대면 서버 호출 없이 안내
        if (before != null && before >= MAX_ACORN) {
            _warning.value = ACORN_LIMIT_MESSAGE
            return
        }

        viewModelScope.launch {
            try {
                repository.chargeAcorn(classroomId)
                refresh()

                val after = (_uiState.value as? ClassroomDetailUiState.Success)?.detail?.points
                if (before != null && after != null && after <= before) {
                    _warning.value = ACORN_LIMIT_MESSAGE
                } else {
                    _toast.tryEmit("학급 도토리를 충전했어요.")
                }
            } catch (e: Exception) {
                _warning.value = e.serverMessage("도토리를 충전하지 못했어요.\n잠시 후 다시 시도해 주세요.")
            }
        }
    }

    fun hideWarning() {
        _warning.value = null
    }
}
