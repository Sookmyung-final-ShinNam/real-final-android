package com.veryshinnam.myapp.feature.classroom.data.dto

import com.google.gson.annotations.SerializedName

// --- 요청 ---
// 학급 생성 요청
data class CreateClassroomRequest(
    val name: String
)

// 학급 가입 요청
data class JoinClassroomRequest(
    val code: String
)

// 과제 등록 요청 (dueAt: yyyy-MM-ddTHH:mm:ss)
data class CreateAssignmentRequest(
    val title: String,
    val description: String?,
    val dueAt: String,
    val commonPrompt: String
)

// --- 응답 ---
// 학급 생성 응답 결과
data class CreateClassroomResult(
    val code: String
)

// 학급 목록 조회 응답 결과 (선생님/학생 공통, joinStatus는 학생만 존재)
data class ClassroomListResult(
    val nickname: String,
    val classrooms: List<ClassroomItemResult>
)

data class ClassroomItemResult(
    val classroomId: Long,
    val name: String,
    val studentCount: Int,
    val code: String,
    val joinStatus: String? // PENDING, APPROVED (선생님은 null)
)

// 학급 상세 조회 응답 결과 (선생님/학생 공통, joinStatus는 선생님만 존재)
data class ClassroomDetailResult(
    val name: String,
    val points: Int,
    val createdAt: String,
    val students: List<StudentResult>
)

data class StudentResult(
    val studentNo: Int,
    val studentId: Long,
    val studentName: String,
    val joinStatus: String? // PENDING, APPROVED (학생 조회 시 null)
)

// 과제 목록 조회 응답 결과 (선생님/학생 공통)
// - 선생님: submittedCount, notSubmittedCount
// - 학생: description, submitted
data class AssignmentResult(
    val assignmentId: Long,
    val title: String,
    val description: String?,
    // 서버(Lombok getDDay + Jackson)는 필드명을 "dday"로 내려줌. 값이 없으면 null > 마감일로 직접 계산
    @SerializedName(value = "dDay", alternate = ["dday", "DDay"])
    val dDay: Long?,
    val dueAt: String,
    val submittedCount: Int?,
    val notSubmittedCount: Int?,
    val submitted: Boolean?
)

// 과제 제출 현황 조회 응답 결과 (선생님)
data class SubmissionResult(
    val assignmentId: Long,
    val title: String,
    val submittedCount: Int,
    val notSubmittedCount: Int,
    val students: List<SubmissionStudentResult>
)

data class SubmissionStudentResult(
    val studentNo: Int,
    val studentId: Long,
    val studentName: String,
    val submitted: Boolean
)

// 미제출 학생 알림 메일 발송 응답 결과
data class ReminderResult(
    val recipientCount: Int // 알림 메일을 보내는 학생 수
)
