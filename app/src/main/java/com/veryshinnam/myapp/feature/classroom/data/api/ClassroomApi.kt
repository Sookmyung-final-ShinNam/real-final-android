package com.veryshinnam.myapp.feature.classroom.data.api

import com.veryshinnam.myapp.core.network.BaseResponse
import com.veryshinnam.myapp.feature.classroom.data.model.ClassroomResult
import com.veryshinnam.myapp.feature.classroom.data.model.ClassroomRequest
import com.veryshinnam.myapp.feature.creation.data.model.StartRequest
import com.veryshinnam.myapp.feature.creation.data.model.StartResult
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ClassroomApi {

    @POST("api/classrooms/email/send")
    suspend fun sendEmailVerification(
        @Body body: ClassroomRequest.SendEmailRequest
    ): BaseResponse<Unit>

    @POST("api/classrooms/email/verify")
    suspend fun verifyEmail(
        @Body body: ClassroomRequest.VerifyEmailRequest
    ): BaseResponse<Unit>

    @POST("api/classrooms")
    suspend fun createClassroom(
        @Body body: ClassroomRequest.CreateClassroomRequest
    ): BaseResponse<ClassroomResult.CreateClassroomResult>

    @GET("api/classrooms/teacher")
    suspend fun getTeacherClassrooms(): BaseResponse<ClassroomResult.TeacherClassroomListResult>

    @GET("api/classrooms/student")
    suspend fun getStudentClassrooms(): BaseResponse<ClassroomResult.StudentClassroomListResult>

    @GET("api/classrooms/{classroomId}")
    suspend fun getClassroomDetail(
        @Path("classroomId") classroomId: Long
    ): BaseResponse<ClassroomResult.ClassroomDetailResult>

    @POST("api/classrooms/{classroomId}/students/{studentId}/approve")
    suspend fun approveStudent(
        @Path("classroomId") classroomId: Long,
        @Path("studentId") studentId: Long
    ): BaseResponse<Unit>

    @POST("api/classrooms/join")
    suspend fun joinClassroom(
        @Body body: ClassroomRequest.JoinClassroomRequest
    ): BaseResponse<Unit>

    @POST("api/classrooms/{classroomId}/assignments")
    suspend fun createAssignment(
        @Path("classroomId") classroomId: Long,
        @Body body: ClassroomRequest.CreateAssignmentRequest
    ): BaseResponse<Unit>

    @GET("api/classrooms/{classroomId}/assignments")
    suspend fun getClassroomAssignments(
        @Path("classroomId") classroomId: Long
    ): BaseResponse<List<ClassroomResult.AssignmentResult>>

    @GET("api/classrooms/assignments")
    suspend fun getTeacherAssignments(): BaseResponse<List<ClassroomResult.TeacherAssignmentResult>>

    @POST("api/classrooms/{classroomId}/assignments/{assignmentId}/start")
    suspend fun startAssignment(
        @Path("classroomId") classroomId: Long,
        @Path("assignmentId") assignmentId: Long,
        @Body body: StartRequest
    ): BaseResponse<StartResult>

    @GET("api/classrooms/{classroomId}/stories")
    suspend fun getClassroomStories(
        @Path("classroomId") classroomId: Long,
        @Query("week") week: Int
    ): BaseResponse<ClassroomResult.ClassroomStoriesResult>

    @POST("api/classrooms/acorn")
    suspend fun chargeAcorn(): BaseResponse<Unit>
}
