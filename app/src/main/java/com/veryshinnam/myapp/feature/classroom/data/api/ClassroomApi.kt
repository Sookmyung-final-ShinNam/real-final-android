package com.veryshinnam.myapp.feature.classroom.data.api

import com.veryshinnam.myapp.core.network.BaseResponse
import com.veryshinnam.myapp.feature.character.data.dto.CharactersResult
import com.veryshinnam.myapp.feature.classroom.data.dto.AssignmentResult
import com.veryshinnam.myapp.feature.classroom.data.dto.ClassroomDetailResult
import com.veryshinnam.myapp.feature.classroom.data.dto.ClassroomListResult
import com.veryshinnam.myapp.feature.classroom.data.dto.CreateAssignmentRequest
import com.veryshinnam.myapp.feature.classroom.data.dto.CreateClassroomRequest
import com.veryshinnam.myapp.feature.classroom.data.dto.CreateClassroomResult
import com.veryshinnam.myapp.feature.classroom.data.dto.JoinClassroomRequest
import com.veryshinnam.myapp.feature.creation.data.dto.StartRequest
import com.veryshinnam.myapp.feature.creation.data.dto.StartResult
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ClassroomApi {

    // --- 선생님 ---
    // 학급 생성 api
    @POST("api/classrooms")
    suspend fun createClassroom(
        @Body request: CreateClassroomRequest
    ): BaseResponse<CreateClassroomResult>

    // 학생 가입 승인 api
    @PATCH("api/classrooms/{classroomId}/students/{studentId}/approve")
    suspend fun approveStudent(
        @Path("classroomId") classroomId: Long,
        @Path("studentId") studentId: Long
    ): BaseResponse<Unit>

    // 학급 도토리 충전 api
    @PATCH("api/classrooms/{classroomId}/charge")
    suspend fun chargeAcorn(
        @Path("classroomId") classroomId: Long
    ): BaseResponse<Unit>

    // 과제 등록 api
    @POST("api/classrooms/{classroomId}/assignments")
    suspend fun createAssignment(
        @Path("classroomId") classroomId: Long,
        @Body request: CreateAssignmentRequest
    ): BaseResponse<Unit>

    // --- 학생 ---
    // 학급 가입 신청 api
    @POST("api/classrooms/join")
    suspend fun joinClassroom(
        @Body request: JoinClassroomRequest
    ): BaseResponse<Unit>

    // 과제 진행(동화 생성 대화 세션 시작) api
    @POST("api/classrooms/{classroomId}/assignments/{assignmentId}/start")
    suspend fun startAssignment(
        @Path("classroomId") classroomId: Long,
        @Path("assignmentId") assignmentId: Long,
        @Body request: StartRequest
    ): BaseResponse<StartResult>

    // --- 공통 (역할별 응답 상이) ---
    // 학급 목록 조회 api
    @GET("api/classrooms")
    suspend fun getClassrooms(): BaseResponse<ClassroomListResult>

    // 학급 상세 조회 api
    @GET("api/classrooms/{classroomId}")
    suspend fun getClassroomDetail(
        @Path("classroomId") classroomId: Long
    ): BaseResponse<ClassroomDetailResult>

    // 학급 과제 목록 조회 api
    @GET("api/classrooms/{classroomId}/assignments")
    suspend fun getAssignments(
        @Path("classroomId") classroomId: Long
    ): BaseResponse<List<AssignmentResult>>

    // 학급 동화 목록 조회 api (assignmentId 없으면 전체)
    @GET("api/classrooms/{classroomId}/stories")
    suspend fun getStories(
        @Path("classroomId") classroomId: Long,
        @Query("assignmentId") assignmentId: Long? = null
    ): BaseResponse<CharactersResult>
}
