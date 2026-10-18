package com.veryshinnam.myapp.feature.creation.data.dto

import com.veryshinnam.myapp.feature.creation.model.InProgressData

// 진행 중인 대화 세션 조회 응답 결과
data class InProgressResult(
    val sessionId: Long,
    val currentStep: String,
    val characterName: String?,
    val assignmentId: Long?,
    val classroomId: Long?
)

fun InProgressResult.toInProgressData(): InProgressData =
    InProgressData(
        sessionId = sessionId,
        currentStep = currentStep,
        characterName = characterName,
        assignmentId = assignmentId,
        classroomId = classroomId
    )
