package com.veryshinnam.myapp.feature.classroom.data.model

data class ClassroomRequest {
    data class SendEmailRequest(val email: String)
    data class VerifyEmailRequest(val code: String)
    data class CreateClassroomRequest(val name: String)
    data class JoinClassroomRequest(val code: String)
    data class CreateAssignmentRequest(
        val title: String,
        val description: String,
        val dueAt: String,
        val commonPrompt: String
    )
}
