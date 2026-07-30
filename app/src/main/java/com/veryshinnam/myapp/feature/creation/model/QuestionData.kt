package com.veryshinnam.myapp.feature.creation.model

data class QuestionData (
    val messageId: Long,  // 메시지 아이디
    val question: String, // llm 질문
)