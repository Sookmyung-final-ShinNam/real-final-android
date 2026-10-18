package com.veryshinnam.myapp.feature.classroom.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.veryshinnam.myapp.core.network.serverMessage
import com.veryshinnam.myapp.feature.classroom.data.repository.ClassroomRepository
import com.veryshinnam.myapp.feature.classroom.model.AssignmentData
import com.veryshinnam.myapp.feature.classroom.model.SubmissionData
import com.veryshinnam.myapp.feature.creation.data.repository.ConversationRepository
import com.veryshinnam.myapp.feature.creation.model.ResumeRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface AssignmentListUiState {
    data object Loading : AssignmentListUiState
    data class Error(val message: String) : AssignmentListUiState

    data class Success(
        val classroomName: String,
        val assignments: List<AssignmentData>,
        // 학생이 끝내지 않은 과제 (과제 아이디 > 이어하기 요청). 이어하기 버튼 표시에 사용
        val resumables: Map<Long, ResumeRequest> = emptyMap()
    ) : AssignmentListUiState
}

// 과제 제출 현황 시트 상태 (선생님)
sealed interface SubmissionUiState {
    data object Closed : SubmissionUiState
    data object Loading : SubmissionUiState
    data class Error(val message: String) : SubmissionUiState

    data class Success(
        val submission: SubmissionData,
        val isClosed: Boolean // 마감된 과제는 알림 메일 발송 불가
    ) : SubmissionUiState
}

@HiltViewModel
class AssignmentListViewModel @Inject constructor(
    private val repository: ClassroomRepository,
    private val conversationRepository: ConversationRepository
) : ViewModel() {

    private var classroomId: Long = -1L
    private var isTeacher: Boolean = false
    private var submissionAssignmentId: Long = -1L

    // 화면 전체 ui 상태
    private val _uiState = MutableStateFlow<AssignmentListUiState>(AssignmentListUiState.Loading)
    val uiState: StateFlow<AssignmentListUiState> = _uiState.asStateFlow()

    // 제출 현황 시트 상태
    private val _submissionState = MutableStateFlow<SubmissionUiState>(SubmissionUiState.Closed)
    val submissionState: StateFlow<SubmissionUiState> = _submissionState.asStateFlow()

    // 알림 메일 발송 요청 중 (중복 발송 방지)
    private val _isSending = MutableStateFlow(false)
    val isSending: StateFlow<Boolean> = _isSending.asStateFlow()

    // 경고창 문구 (null이면 닫힘)
    private val _warning = MutableStateFlow<String?>(null)
    val warning: StateFlow<String?> = _warning.asStateFlow()

    // 토스트 이벤트
    private val _toast = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val toast: SharedFlow<String> = _toast.asSharedFlow()

    // 학급 이름 + 과제 목록 조회 (화면 진입 시, 과제 등록/동화 생성 후 복귀 시)
    fun load(classroomId: Long, isTeacher: Boolean) {
        this.classroomId = classroomId
        this.isTeacher = isTeacher

        // 이미 보여주는 목록이 있으면 로딩 화면 없이 갱신
        if (_uiState.value !is AssignmentListUiState.Success) {
            _uiState.value = AssignmentListUiState.Loading
        }

        viewModelScope.launch {
            try {
                val detail = repository.getClassroomDetail(classroomId)
                val assignments = repository.getAssignments(classroomId)

                // 학생: 끝내지 않은 과제는 이어하기로 표시 (조회에 실패해도 과제 목록은 보여줌)
                val resumables = if (isTeacher) emptyMap() else try {
                    conversationRepository.getInProgress()
                        .filter { it.classroomId == classroomId && it.assignmentId != null }
                        .asReversed() // 서버는 최신순 > 같은 과제가 여러 개면 가장 최근 세션이 남도록 뒤집어서 매핑
                        .associate { it.assignmentId!! to it.toResumeRequest() }
                } catch (e: Exception) {
                    emptyMap()
                }

                _uiState.value = AssignmentListUiState.Success(
                    classroomName = detail.name,
                    assignments = assignments,
                    resumables = resumables
                )
            } catch (e: Exception) {
                _uiState.value = AssignmentListUiState.Error(
                    e.serverMessage("과제 목록을 불러오지 못했어요.")
                )
            }
        }
    }

    // 다시 시도 (오류 화면)
    fun retry() = load(classroomId, isTeacher)

    // --- 제출 현황 (선생님) ---
    // 제출 현황 시트 열기 + 조회
    fun openSubmissions(assignmentId: Long) {
        submissionAssignmentId = assignmentId
        _submissionState.value = SubmissionUiState.Loading

        viewModelScope.launch {
            try {
                val submission = repository.getSubmissions(classroomId, assignmentId)
                val isClosed = (_uiState.value as? AssignmentListUiState.Success)
                    ?.assignments?.firstOrNull { it.id == assignmentId }?.isClosed ?: false

                // 조회 중 시트를 닫았거나 다른 과제를 열었다면 이전 응답은 버림
                if (submissionAssignmentId == assignmentId && _submissionState.value is SubmissionUiState.Loading) {
                    _submissionState.value = SubmissionUiState.Success(submission, isClosed)
                }
            } catch (e: Exception) {
                if (submissionAssignmentId == assignmentId && _submissionState.value is SubmissionUiState.Loading) {
                    _submissionState.value = SubmissionUiState.Error(
                        e.serverMessage("제출 현황을 불러오지 못했어요.")
                    )
                }
            }
        }
    }

    fun closeSubmissions() {
        submissionAssignmentId = -1L
        _submissionState.value = SubmissionUiState.Closed
    }

    // 미제출 학생에게 알림 메일 발송 (서버가 학생별로 발송, 마감 전 과제는 24시간에 한 번만 가능)
    fun sendReminder() {
        if (_isSending.value || submissionAssignmentId < 0) return
        _isSending.value = true

        viewModelScope.launch {
            try {
                val count = repository.sendReminder(classroomId, submissionAssignmentId)
                _toast.tryEmit("미제출 학생 ${count}명에게 알림 메일을 보냈어요.")
            } catch (e: Exception) {
                _warning.value = e.serverMessage("알림 메일을 보내지 못했어요.\n잠시 후 다시 시도해 주세요.")
            } finally {
                _isSending.value = false
            }
        }
    }

    fun hideWarning() {
        _warning.value = null
    }
}
