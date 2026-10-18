package com.veryshinnam.myapp.feature.classroom.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

// 학급 가입 상태
enum class JoinStatus {
    PENDING,    // 가입 요청 중
    APPROVED    // 승인 완료
}

// 학급 목록 조회
data class ClassroomListData(
    val nickname: String,
    val classrooms: List<ClassroomData>
)

data class ClassroomData(
    val id: Long,
    val name: String,
    val studentCount: Int,
    val code: String,
    val joinStatus: JoinStatus? // 선생님은 null
)

// 학급 상세 조회
data class ClassroomDetailData(
    val name: String,
    val points: Int,         // 학급 도토리 수
    val createdDate: String, // 개설일 (yyyy.MM.dd)
    val students: List<StudentData>
) {
    val pendingCount: Int get() = students.count { !it.isApproved }
    val approvedCount: Int get() = students.count { it.isApproved }
}

data class StudentData(
    val no: Int,
    val id: Long,
    val name: String,
    val isApproved: Boolean
)

// 과제
data class AssignmentData(
    val id: Long,
    val title: String,
    val description: String?,       // 학생 조회에서만 존재
    val dDay: Long,                 // 0: 오늘 마감, 음수: 마감 지남
    val dueDate: String,            // 마감 일시 (yyyy.MM.dd 오후 h:mm)
    val submittedCount: Int?,       // 선생님 조회에서만 존재
    val notSubmittedCount: Int?,    // 선생님 조회에서만 존재
    val isSubmitted: Boolean?,      // 학생 조회에서만 존재
    val isClosed: Boolean           // 마감 시각이 지났는지 (지나면 과제 시작 불가)
)

// 과제 진행 대상 (과제로 동화 생성 시 생성 플로우로 전달)
@Parcelize
data class AssignmentTarget(
    val classroomId: Long,
    val assignmentId: Long
) : Parcelable
