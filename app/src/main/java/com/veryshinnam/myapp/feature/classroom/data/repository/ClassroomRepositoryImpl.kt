package com.veryshinnam.myapp.feature.classroom.data.repository

import com.veryshinnam.myapp.feature.character.data.dto.toCollectionData
import com.veryshinnam.myapp.feature.classroom.data.api.ClassroomApi
import com.veryshinnam.myapp.feature.classroom.data.dto.CreateAssignmentRequest
import com.veryshinnam.myapp.feature.classroom.data.dto.CreateClassroomRequest
import com.veryshinnam.myapp.feature.classroom.data.dto.JoinClassroomRequest
import com.veryshinnam.myapp.feature.classroom.data.dto.toAssignmentData
import com.veryshinnam.myapp.feature.classroom.data.dto.toClassroomDetailData
import com.veryshinnam.myapp.feature.classroom.data.dto.toClassroomListData
import com.veryshinnam.myapp.feature.classroom.data.dto.toSubmissionData
import com.veryshinnam.myapp.feature.classroom.model.AssignmentData
import com.veryshinnam.myapp.feature.classroom.model.ClassroomDetailData
import com.veryshinnam.myapp.feature.classroom.model.ClassroomListData
import com.veryshinnam.myapp.feature.classroom.model.SubmissionData
import com.veryshinnam.myapp.feature.collection.model.CollectionData
import javax.inject.Inject

class ClassroomRepositoryImpl @Inject constructor(
    private val api: ClassroomApi
) : ClassroomRepository {

    // 학급 생성
    override suspend fun createClassroom(name: String): String {
        val response = api.createClassroom(CreateClassroomRequest(name))

        if (!response.isSuccess || response.result == null) {
            throw Exception("학급 생성 실패: ${response.message}")
        }
        return response.result.code
    }

    // 학급 가입 신청
    override suspend fun joinClassroom(code: String) {
        val response = api.joinClassroom(JoinClassroomRequest(code))

        if (!response.isSuccess) {
            throw Exception("학급 가입 신청 실패: ${response.message}")
        }
    }

    // 학생 가입 승인
    override suspend fun approveStudent(classroomId: Long, studentId: Long) {
        val response = api.approveStudent(classroomId, studentId)

        if (!response.isSuccess) {
            throw Exception("학생 가입 승인 실패: ${response.message}")
        }
    }

    // 학급 도토리 충전
    override suspend fun chargeAcorn(classroomId: Long) {
        val response = api.chargeAcorn(classroomId)

        if (!response.isSuccess) {
            throw Exception("학급 도토리 충전 실패: ${response.message}")
        }
    }

    // 학급 목록 조회
    override suspend fun getClassrooms(): ClassroomListData {
        val response = api.getClassrooms()

        if (!response.isSuccess || response.result == null) {
            throw Exception("학급 목록 조회 실패: ${response.message}")
        }
        return response.result.toClassroomListData()
    }

    // 학급 상세 조회
    override suspend fun getClassroomDetail(classroomId: Long): ClassroomDetailData {
        val response = api.getClassroomDetail(classroomId)

        if (!response.isSuccess || response.result == null) {
            throw Exception("학급 상세 조회 실패: ${response.message}")
        }
        return response.result.toClassroomDetailData()
    }

    // 과제 등록
    override suspend fun createAssignment(
        classroomId: Long,
        title: String,
        description: String?,
        dueAt: String,
        commonPrompt: String
    ) {
        val response = api.createAssignment(
            classroomId,
            CreateAssignmentRequest(title, description, dueAt, commonPrompt)
        )

        if (!response.isSuccess) {
            throw Exception("과제 등록 실패: ${response.message}")
        }
    }

    // 과제 제출 현황 조회
    override suspend fun getSubmissions(classroomId: Long, assignmentId: Long): SubmissionData {
        val response = api.getSubmissions(classroomId, assignmentId)

        if (!response.isSuccess || response.result == null) {
            throw Exception("과제 제출 현황 조회 실패: ${response.message}")
        }
        return response.result.toSubmissionData()
    }

    // 미제출 학생 알림 메일 발송
    override suspend fun sendReminder(classroomId: Long, assignmentId: Long): Int {
        val response = api.sendReminder(classroomId, assignmentId)

        if (!response.isSuccess || response.result == null) {
            throw Exception("미제출 학생 알림 메일 발송 실패: ${response.message}")
        }
        return response.result.recipientCount
    }

    // 학급 과제 목록 조회
    override suspend fun getAssignments(classroomId: Long): List<AssignmentData> {
        val response = api.getAssignments(classroomId)

        if (!response.isSuccess || response.result == null) {
            throw Exception("학급 과제 목록 조회 실패: ${response.message}")
        }
        return response.result.map { it.toAssignmentData() }
    }

    // 학급 동화 목록 조회
    override suspend fun getStories(classroomId: Long, assignmentId: Long?): List<CollectionData> {
        val response = api.getStories(classroomId, assignmentId)

        if (!response.isSuccess || response.result == null) {
            throw Exception("학급 동화 목록 조회 실패: ${response.message}")
        }
        return response.result.characters.mapNotNull { it.toCollectionData() }
    }
}
