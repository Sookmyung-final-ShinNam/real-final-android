package com.veryshinnam.myapp.feature.creation.model

data class ManualScriptData (
    val step: ConversationStep,
    val story: String = "",
    val question: String = "",
    val feedback: FeedbackData = FeedbackData(false, "", 0),
)