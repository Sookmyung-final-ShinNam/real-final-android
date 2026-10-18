package com.veryshinnam.myapp.feature.creation.data.api

import com.veryshinnam.myapp.core.network.BaseResponse
import com.veryshinnam.myapp.feature.creation.data.dto.FeedbackResult
import com.veryshinnam.myapp.feature.creation.data.dto.InProgressResult
import com.veryshinnam.myapp.feature.creation.data.dto.NextStepResult
import com.veryshinnam.myapp.feature.creation.data.dto.StartRequest
import com.veryshinnam.myapp.feature.creation.data.dto.StartResult
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ConversationApi {

    // 대화 세션 시작 api
    @POST("api/conversations/start")
    suspend fun startConversation(@Body request: StartRequest): BaseResponse<StartResult>

    // 진행 중인(끝내지 않은) 대화 세션 조회 api (이어하기)
    @GET("api/conversations/in-progress")
    suspend fun getInProgress(): BaseResponse<List<InProgressResult>>

    // 다음 스텝에서 메시지 조회 api
    @GET("api/conversations/next-step")
    suspend fun getNextStep(
        @Query("sessionId") sessionId: Long,
        @Query("currentStep") currentStep: String
    ): BaseResponse<NextStepResult>

    // 사용자 답변 피드백 api
    @POST("api/conversations/feedback")
    suspend fun feedbackConversation(
        @Query("messageId") messageId: Long,
        @Query("userAnswer") userAnswer: String
    ): BaseResponse<FeedbackResult>

    // 동화 생성 완성 api
    @POST("api/conversations/complete")
    suspend fun completeConversation(@Query("sessionId") sessionId: Long): BaseResponse<Unit>

    // 페이지별 동영상 생성 api
    @POST("api/conversations/video")
    suspend fun generateVideo(@Query("storyId") storyId: Long): BaseResponse<String>
}