package com.veryshinnam.myapp.feature.permit.data.repository

import com.google.gson.Gson
import com.veryshinnam.myapp.common.model.UserRole
import com.veryshinnam.myapp.core.network.BaseResponse
import com.veryshinnam.myapp.feature.permit.data.dto.JwtResult
import com.veryshinnam.myapp.feature.permit.data.api.PermitApi
import com.veryshinnam.myapp.feature.permit.data.dto.EmailCodeRequest
import javax.inject.Inject

class PermitRepositoryImpl  @Inject constructor(
    private val api: PermitApi
) : PermitRepository {

    // 로그인
    override suspend fun login(
        tempCode: String,
        role: UserRole?
    ): JwtResult {
        val response: BaseResponse<JwtResult> = api.login(tempCode, role)

        if (!response.isSuccess || response.result == null) {
            throw Exception("로그인 실패: ${response.message}")
        }

        return response.result
    }

    // 선생님 회원가입 인증코드 발송
    override suspend fun sendEmailCode(request: EmailCodeRequest.Send): Boolean {
        try {
            val response: BaseResponse<Unit> = api.sendEmailCode(request)
            if (!response.isSuccess) {
                throw Exception(response.message)
            }
            return true
        } catch (e: retrofit2.HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val message = try {
                errorBody?.let { Gson().fromJson(it, BaseResponse::class.java).message }
            } catch (_: Exception) { null }

            throw Exception(message ?: "인증 코드 발송에 실패했습니다.")
        }
    }

    // 선생님 회원가입 인증코드 검증
    override suspend fun verifyEmailCode(request: EmailCodeRequest.Verification): Boolean {
        try {
            val response: BaseResponse<Unit> = api.verifyEmailCode(request)
            if (!response.isSuccess) {
                throw Exception(response.message)
            }
            return true;
        }  catch (e: retrofit2.HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val message = try {
                errorBody?.let { Gson().fromJson(it, BaseResponse::class.java).message }
            } catch (_: Exception) { null }

            throw Exception(message ?: "인증 코드가 일치하지 않습니다.")
        }
    }
}