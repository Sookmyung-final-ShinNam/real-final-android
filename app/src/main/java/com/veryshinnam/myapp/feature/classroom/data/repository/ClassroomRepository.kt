package com.veryshinnam.myapp.feature.classroom.data.repository

import com.veryshinnam.myapp.feature.classroom.data.model.*
import com.veryshinnam.myapp.feature.creation.data.dto.StartRequest
import com.veryshinnam.myapp.feature.creation.data.dto.StartResult

interface ClassroomRepository {
    suspend fun sendEmailVerification(email: String)
    suspend fun verifyEmail(code: String)
    suspend fun createClassroom(name: String): String
    suspend fun getTeacherClassrooms(): TeacherClassroomData
    suspend fun getStudentClassrooms(): StudentClassroomData
    suspend fun getClassroomDetail(classroomId: Long): ClassroomDetailData
    suspend fun approveStudent(classroomId: Long, studentId: Long)
    suspend fun joinClassroom(code: String)
    suspend fun createAssignment(classroomId: Long, title: String, description: String, dueAt: String, commonPrompt: String)
    suspend fun getClassroomAssignments(classroomId: Long): List<AssignmentData>
    suspend fun getTeacherAssignments(): List<TeacherAssignmentData>
    suspend fun startAssignment(classroomId: Long, assignmentId: Long, request: StartRequest): StartResult
    suspend fun getClassroomStories(classroomId: Long, week: Int): List<ClassroomCharacterData>
    suspend fun chargeAcorn()
}
