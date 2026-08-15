package com.veryshinnam.myapp.feature.classroom.data.repository

import com.veryshinnam.myapp.feature.classroom.data.api.ClassroomApi
import com.veryshinnam.myapp.feature.classroom.data.model.*
import com.veryshinnam.myapp.feature.classroom.data.model.ClassroomRequest
import com.veryshinnam.myapp.feature.creation.data.model.StartRequest
import com.veryshinnam.myapp.feature.creation.data.model.StartResult
import javax.inject.Inject

class ClassroomRepositoryImpl @Inject constructor(
    private val api: ClassroomApi
) : ClassroomRepository {

    override suspend fun sendEmailVerification(email: String) {
        api.sendEmailVerification(ClassroomRequest.SendEmailRequest(email))
    }

    override suspend fun verifyEmail(code: String) {
        api.verifyEmail(ClassroomRequest.VerifyEmailRequest(code))
    }

    override suspend fun createClassroom(name: String): String {
        val result = api.createClassroom(ClassroomRequest.CreateClassroomRequest(name))
        return result.result?.code ?: error("학급 코드를 받지 못했습니다.")
    }

    override suspend fun getTeacherClassrooms(): TeacherClassroomData {
        val result = api.getTeacherClassrooms().result ?: error("데이터를 불러오지 못했습니다.")
        return TeacherClassroomData(
            nickname = result.nickname,
            classrooms = result.classrooms.map {
                TeacherClassroomItemData(it.classroomId, it.name, it.studentCount, it.code)
            }
        )
    }

    override suspend fun getStudentClassrooms(): StudentClassroomData {
        val result = api.getStudentClassrooms().result ?: error("데이터를 불러오지 못했습니다.")
        return StudentClassroomData(
            nickname = result.nickname,
            classrooms = result.classrooms.map {
                StudentClassroomItemData(
                    classroomId = it.classroomId,
                    name = it.name,
                    studentCount = it.studentCount,
                    code = it.code,
                    joinStatus = if (it.joinStatus == "APPROVED") JoinStatus.APPROVED else JoinStatus.PENDING
                )
            }
        )
    }

    override suspend fun getClassroomDetail(classroomId: Long): ClassroomDetailData {
        val result = api.getClassroomDetail(classroomId).result ?: error("데이터를 불러오지 못했습니다.")
        return ClassroomDetailData(
            name = result.name,
            points = result.points,
            createdAt = result.createdAt,
            students = result.students.map {
                StudentItemData(
                    number = it.number,
                    studentId = it.studentId,
                    studentName = it.studentName,
                    joinStatus = it.joinStatus?.let { s ->
                        if (s == "APPROVED") JoinStatus.APPROVED else JoinStatus.PENDING
                    }
                )
            }
        )
    }

    override suspend fun approveStudent(classroomId: Long, studentId: Long) {
        api.approveStudent(classroomId, studentId)
    }

    override suspend fun joinClassroom(code: String) {
        api.joinClassroom(ClassroomRequest.JoinClassroomRequest(code))
    }

    override suspend fun createAssignment(
        classroomId: Long, title: String, description: String, dueAt: String, commonPrompt: String
    ) {
        api.createAssignment(
            classroomId,
            ClassroomRequest.CreateAssignmentRequest(title, description, dueAt, commonPrompt)
        )
    }

    override suspend fun getClassroomAssignments(classroomId: Long): List<AssignmentData> {
        val result = api.getClassroomAssignments(classroomId).result ?: emptyList()
        return result.map { AssignmentData(it.assignmentId, it.title, it.description, it.dDay, it.dueAt) }
    }

    override suspend fun getTeacherAssignments(): List<TeacherAssignmentData> {
        val result = api.getTeacherAssignments().result ?: emptyList()
        return result.map {
            TeacherAssignmentData(it.assignmentId, it.title, it.dDay, it.dueAt, it.submittedCount, it.notSubmittedCount)
        }
    }

    override suspend fun startAssignment(classroomId: Long, assignmentId: Long, request: StartRequest): StartResult {
        return api.startAssignment(classroomId, assignmentId, request).result
            ?: error("과제 시작에 실패했습니다.")
    }

    override suspend fun getClassroomStories(classroomId: Long, week: Int): List<ClassroomCharacterData> {
        val result = api.getClassroomStories(classroomId, week).result ?: return emptyList()
        return result.characters.map {
            ClassroomCharacterData(it.characterId, it.name, it.imageUrl, it.important)
        }
    }

    override suspend fun chargeAcorn() {
        api.chargeAcorn()
    }
}
