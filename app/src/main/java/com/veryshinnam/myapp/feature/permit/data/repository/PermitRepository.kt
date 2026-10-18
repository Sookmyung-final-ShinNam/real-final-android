package com.veryshinnam.myapp.feature.permit.data.repository

import com.veryshinnam.myapp.common.model.UserRole
import com.veryshinnam.myapp.feature.permit.data.dto.EmailCodeRequest
import com.veryshinnam.myapp.feature.permit.data.dto.JwtResult

interface PermitRepository {

    // 로그인
    suspend fun login(tempCode: String, role: UserRole?): JwtResult

    // 선생님 회원가입 인증코드 발송
    suspend fun sendEmailCode(request: EmailCodeRequest.Send): Boolean

    // 선생님 회원가입 인증코드 검증
    suspend fun verifyEmailCode(request: EmailCodeRequest.Verification): Boolean

}