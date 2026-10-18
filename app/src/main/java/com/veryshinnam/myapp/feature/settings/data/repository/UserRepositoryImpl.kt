package com.veryshinnam.myapp.feature.settings.data.repository

import com.veryshinnam.myapp.common.model.UserRole
import com.veryshinnam.myapp.core.network.BaseResponse
import com.veryshinnam.myapp.feature.settings.data.api.UserApi
import retrofit2.http.Query
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val api: UserApi
): UserRepository {

    // 사용자 권한 확인 - 스플래시, 나의 학급에서 분기
    override suspend fun checkRole(role: UserRole): Boolean {
        val response: BaseResponse<Boolean> = api.checkRole(role)

        if (!response.isSuccess) throw Exception("역할 확인 실패: ${response.message}")

        return response.result ?: false // result가 null일 경우 false
    }

    // 로그아웃
    override suspend fun logout(): Boolean {
        val response: BaseResponse<String> = api.logout()

        if (!response.isSuccess || response.result == null) {
            throw Exception("로그아웃 실패: ${response.result}")
        }

        return true
    }

    // 회원 탈퇴
    override suspend fun withdraw(): Boolean {
        val response: BaseResponse<String> = api.withdraw()

        if (!response.isSuccess || response.result == null) {
            throw Exception("회원 탈퇴 실패: ${response.result}")
        }

        return true
    }
}