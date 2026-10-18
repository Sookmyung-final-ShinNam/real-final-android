package com.veryshinnam.myapp.feature.creation.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

// 이어하기: 끝내지 않은(진행 중) 대화 세션
data class InProgressData(
    val sessionId: Long,
    val currentStep: String,        // 이어서 시작할 단계 (START, 기, 승, 전, 결)
    val characterName: String?,
    val assignmentId: Long?,        // 학급 과제로 만든 경우에만 값이 있음 (일반 동화는 null)
    val classroomId: Long?
) {
    // 일반 동화 여부 (학급 과제가 아닌 경우)
    val isPersonal: Boolean get() = assignmentId == null

    fun toResumeRequest(): ResumeRequest = ResumeRequest(
        sessionId = sessionId,
        currentStep = currentStep,
        characterName = characterName
    )
}

// 이어하기 요청 (화면 이동 시 대화 화면으로 전달)
@Parcelize
data class ResumeRequest(
    val sessionId: Long,
    val currentStep: String,
    val characterName: String?
) : Parcelable
