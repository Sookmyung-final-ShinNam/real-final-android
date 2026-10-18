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

@HiltViewModel
class AssignmentCreateViewModel @Inject constructor(
    private val repository: ClassroomRepository
) : ViewModel() {

    // 등록 요청 중 (중복 등록 방지)
    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting.asStateFlow()

    // 경고창 문구 (null이면 닫힘)
    private val _warning = MutableStateFlow<String?>(null)
    val warning: StateFlow<String?> = _warning.asStateFlow()

    // 등록 완료 이벤트 (화면에서 뒤로 이동)
    private val _created = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val created: SharedFlow<Unit> = _created.asSharedFlow()

    // 과제 등록
    fun createAssignment(
        classroomId: Long,
        title: String,
        description: String,
        dueAt: String,
        commonPrompt: String
    ) {
        if (_isSubmitting.value) return
        _isSubmitting.value = true

        viewModelScope.launch {
            try {
                repository.createAssignment(
                    classroomId = classroomId,
                    title = title.trim(),
                    description = description.trim().ifBlank { null },
                    dueAt = dueAt,
                    commonPrompt = commonPrompt.trim()
                )
                _created.tryEmit(Unit)
            } catch (e: Exception) {
                _warning.value = e.serverMessage("과제를 등록하지 못했어요.\n잠시 후 다시 시도해 주세요.")
            } finally {
                _isSubmitting.value = false
            }
        }
    }

    fun showWarning(text: String) {
        _warning.value = text
    }

    fun hideWarning() {
        _warning.value = null
    }
}
