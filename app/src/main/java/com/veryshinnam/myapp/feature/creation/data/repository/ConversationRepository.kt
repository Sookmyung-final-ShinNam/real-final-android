package com.veryshinnam.myapp.feature.creation.data.repository

import com.veryshinnam.myapp.feature.creation.data.dto.FeedbackResult
import com.veryshinnam.myapp.feature.creation.data.dto.NextStepResult
import com.veryshinnam.myapp.feature.creation.data.dto.StartRequest
import com.veryshinnam.myapp.feature.creation.data.dto.StartResult
import com.veryshinnam.myapp.feature.creation.model.FeedbackData
import com.veryshinnam.myapp.feature.creation.model.InProgressData

interface ConversationRepository {
    suspend fun startConversation(request: StartRequest): StartResult
    suspend fun startAssignment(classroomId: Long, assignmentId: Long, request: StartRequest): StartResult
    suspend fun getInProgress(): List<InProgressData>
    suspend fun getNextStep(sessionId: Long, currentStep: String): NextStepResult
    suspend fun feedbackConversation(messageId: Long, userAnswer: String): FeedbackData
    suspend fun completeConversation(sessionId: Long): Boolean
}