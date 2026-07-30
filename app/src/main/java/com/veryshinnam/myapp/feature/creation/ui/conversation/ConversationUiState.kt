package com.veryshinnam.myapp.feature.creation.ui.conversation

import com.veryshinnam.myapp.feature.creation.model.AnswerData
import com.veryshinnam.myapp.feature.creation.model.ConversationStep
import com.veryshinnam.myapp.feature.creation.model.FeedbackData
import com.veryshinnam.myapp.feature.creation.model.QuestionData

sealed interface ConversationUiState {
    data object Loading : ConversationUiState
    data class Error(val message: String) : ConversationUiState

    data class Success(
        val sessionId: Long,
        val story: String,
        val questionData: QuestionData,
        val answerData: AnswerData,
        val feedbackData: FeedbackData,
        val conversationStep: ConversationStep, // ui 화면 분기용
        val loopStep: Int = 1 // 1=기, 2=승, 3=전, 4=결
    ) : ConversationUiState
}