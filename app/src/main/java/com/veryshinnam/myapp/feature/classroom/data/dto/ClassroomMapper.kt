package com.veryshinnam.myapp.feature.classroom.data.dto

import com.veryshinnam.myapp.feature.classroom.model.AssignmentData
import com.veryshinnam.myapp.feature.classroom.model.ClassroomData
import com.veryshinnam.myapp.feature.classroom.model.ClassroomDetailData
import com.veryshinnam.myapp.feature.classroom.model.ClassroomListData
import com.veryshinnam.myapp.feature.classroom.model.JoinStatus
import com.veryshinnam.myapp.feature.classroom.model.StudentData
import org.threeten.bp.LocalDateTime

// 서버 날짜(2026-10-20 / 2026-10-20T23:59:59) > 화면 표시용(2026.10.20)
private fun String.toDisplayDate(): String = take(10).replace('-', '.')

// 서버 일시(2026-10-20T23:59:59) > 화면 표시용(2026.10.20 오후 11:59)
private fun String.toDisplayDateTime(): String {
    val date = toDisplayDate()
    val hour = drop(11).take(2).toIntOrNull() ?: return date
    val minute = drop(14).take(2)
    val period = if (hour < 12) "오전" else "오후"
    val hour12 = if (hour % 12 == 0) 12 else hour % 12
    return "$date $period $hour12:$minute"
}

// 마감 일시(2026-10-20T23:59:59)가 현재 시각 이전인지 (파싱 실패 시 마감 아님으로 처리)
private fun isPast(dueAt: String): Boolean =
    runCatching { LocalDateTime.parse(dueAt).isBefore(LocalDateTime.now()) }.getOrDefault(false)

private fun String?.toJoinStatus(): JoinStatus? = when (this) {
    "PENDING" -> JoinStatus.PENDING
    "APPROVED" -> JoinStatus.APPROVED
    else -> null
}

fun ClassroomListResult.toClassroomListData(): ClassroomListData =
    ClassroomListData(
        nickname = nickname,
        classrooms = classrooms.map { it.toClassroomData() }
    )

fun ClassroomItemResult.toClassroomData(): ClassroomData =
    ClassroomData(
        id = classroomId,
        name = name,
        studentCount = studentCount,
        code = code,
        joinStatus = joinStatus.toJoinStatus()
    )

fun ClassroomDetailResult.toClassroomDetailData(): ClassroomDetailData =
    ClassroomDetailData(
        name = name,
        points = points,
        createdDate = createdAt.toDisplayDate(),
        students = students.map { it.toStudentData() }
    )

// 학생 조회 시 joinStatus가 없으므로(승인된 학생만 내려옴) PENDING이 아니면 승인으로 처리
fun StudentResult.toStudentData(): StudentData =
    StudentData(
        no = studentNo,
        id = studentId,
        name = studentName,
        isApproved = joinStatus.toJoinStatus() != JoinStatus.PENDING
    )

fun AssignmentResult.toAssignmentData(): AssignmentData =
    AssignmentData(
        id = assignmentId,
        title = title,
        description = description,
        dDay = dDay,
        dueDate = dueAt.toDisplayDateTime(),
        submittedCount = submittedCount,
        notSubmittedCount = notSubmittedCount,
        isSubmitted = submitted,
        isClosed = dDay < 0 || isPast(dueAt)
    )
