package com.veryshinnam.myapp.feature.classroom.data.repository

import com.veryshinnam.myapp.feature.classroom.model.AssignmentData
import com.veryshinnam.myapp.feature.classroom.model.ClassroomDetailData
import com.veryshinnam.myapp.feature.classroom.model.ClassroomListData
import com.veryshinnam.myapp.feature.classroom.model.SubmissionData
import com.veryshinnam.myapp.feature.collection.model.CollectionData

interface ClassroomRepository {

    // 학급 생성 (선생님) > 발급된 학급 코드 반환
    suspend fun createClassroom(name: String): String

    // 학급 가입 신청 (학생)
    suspend fun joinClassroom(code: String)

    // 학생 가입 승인 (선생님)
    suspend fun approveStudent(classroomId: Long, studentId: Long)

    // 학급 도토리 충전 (선생님)
    suspend fun chargeAcorn(classroomId: Long)

    // 학급 목록 조회
    suspend fun getClassrooms(): ClassroomListData

    // 학급 상세 조회
    suspend fun getClassroomDetail(classroomId: Long): ClassroomDetailData

    // 과제 등록 (선생님)
    suspend fun createAssignment(
        classroomId: Long,
        title: String,
        description: String?,
        dueAt: String,
        commonPrompt: String
    )

    // 과제 제출 현황 조회 (선생님): 학생별 제출 여부
    suspend fun getSubmissions(classroomId: Long, assignmentId: Long): SubmissionData

    // 미제출 학생에게 알림 메일 발송 (선생님) > 메일을 보내는 학생 수 반환
    suspend fun sendReminder(classroomId: Long, assignmentId: Long): Int

    // 학급 과제 목록 조회
    suspend fun getAssignments(classroomId: Long): List<AssignmentData>

    // 학급 동화 목록 조회 (assignmentId 없으면 전체)
    suspend fun getStories(classroomId: Long, assignmentId: Long? = null): List<CollectionData>
}
