package com.veryshinnam.myapp.feature.classroom.data.model

class ClassroomResult {

    data class CreateClassroomResult(val code: String)

    data class TeacherClassroomListResult(
        val nickname: String,
        val classrooms: List<TeacherClassroomItem>
    )

    data class TeacherClassroomItem(
        val classroomId: Long,
        val name: String,
        val studentCount: Int,
        val code: String
    )

    data class StudentClassroomListResult(
        val nickname: String,
        val classrooms: List<StudentClassroomItem>
    )

    data class StudentClassroomItem(
        val classroomId: Long,
        val name: String,
        val studentCount: Int,
        val code: String,
        val joinStatus: String   // "PENDING" | "APPROVED"
    )

    // API 8 - 선생님 응답 or 학생 응답 (동적)
    data class ClassroomDetailResult(
        val name: String,
        val points: Int,
        val createdAt: String,
        val students: List<StudentItem>
    )

    data class StudentItem(
        val number: Int,
        val studentId: Long,
        val studentName: String,
        val joinStatus: String?  // 선생님만 전달, 학생은 null
    )

    data class AssignmentResult(
        val assignmentId: Long,
        val title: String,
        val description: String?,
        val dDay: Long,
        val dueAt: String
    )

    data class TeacherAssignmentResult(
        val assignmentId: Long,
        val title: String,
        val dDay: Long,
        val dueAt: String,
        val submittedCount: Long,
        val notSubmittedCount: Long
    )

    data class ClassroomStoriesResult(
        val characters: List<ClassroomCharacterItem>
    )

    data class ClassroomCharacterItem(
        val characterId: Long,
        val name: String,
        val gender: String,
        val imageUrl: String?,
        val important: Boolean,
        val createTime: String
    )
}
