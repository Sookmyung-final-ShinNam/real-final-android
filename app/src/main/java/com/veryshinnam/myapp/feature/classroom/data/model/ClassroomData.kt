package com.veryshinnam.myapp.feature.classroom.data.model

data class TeacherClassroomData(
    val nickname: String,
    val classrooms: List<TeacherClassroomItemData>
)

data class TeacherClassroomItemData(
    val classroomId: Long,
    val name: String,
    val studentCount: Int,
    val code: String
)

data class StudentClassroomData(
    val nickname: String,
    val classrooms: List<StudentClassroomItemData>
)

data class StudentClassroomItemData(
    val classroomId: Long,
    val name: String,
    val studentCount: Int,
    val code: String,
    val joinStatus: JoinStatus
)

data class ClassroomDetailData(
    val name: String,
    val points: Int,
    val createdAt: String,
    val students: List<StudentItemData>,
    val isTeacher: Boolean = false
)

data class StudentItemData(
    val number: Int,
    val studentId: Long,
    val studentName: String,
    val joinStatus: JoinStatus?
)

data class AssignmentData(
    val assignmentId: Long,
    val title: String,
    val description: String?,
    val dDay: Long,
    val dueAt: String
)

data class TeacherAssignmentData(
    val assignmentId: Long,
    val title: String,
    val dDay: Long,
    val dueAt: String,
    val submittedCount: Long,
    val notSubmittedCount: Long
)

data class ClassroomCharacterData(
    val characterId: Long,
    val name: String,
    val imageUrl: String?,
    val isFavorite: Boolean
)

enum class JoinStatus { PENDING, APPROVED }
