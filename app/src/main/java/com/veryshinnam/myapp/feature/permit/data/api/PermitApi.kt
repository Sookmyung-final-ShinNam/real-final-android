package com.veryshinnam.myapp.feature.permit.data.api

import com.veryshinnam.myapp.common.model.UserRole
import com.veryshinnam.myapp.core.network.BaseResponse
import com.veryshinnam.myapp.feature.permit.data.dto.EmailCodeRequest
import com.veryshinnam.myapp.feature.permit.data.dto.JwtResult
import retrofit2.http.Body
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface PermitApi {

    // 사용자 활성화 및 토큰 조회 (로그인) api
    @PATCH("api/permit/login")
    suspend fun login(
        @Query("tempCode") tempCode: String,
        @Query("role") role: UserRole? = null
    ): BaseResponse<JwtResult>

    // 선생님 회원가입 인증코드 발송 api
    @POST("api/permit/email/send")
    suspend fun sendEmailCode(
        @Body request: EmailCodeRequest.Send
    ): BaseResponse<Unit>

    // 선생님 회원가입 인증코드 검증 api
    @PATCH("api/permit/email/verify")
    suspend fun verifyEmailCode(
        @Body request: EmailCodeRequest.Verification
    ): BaseResponse<Unit>
}