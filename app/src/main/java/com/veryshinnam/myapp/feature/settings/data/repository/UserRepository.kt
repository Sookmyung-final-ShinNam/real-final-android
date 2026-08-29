package com.veryshinnam.myapp.feature.settings.data.repository

import com.veryshinnam.myapp.common.model.UserRole
import retrofit2.http.Query

interface UserRepository {

    // 사용자 권한 확인 - 스플래시, 나의 학급에서 분기
    suspend fun checkRole(
        @Query("role") role: UserRole
    ): Boolean

    // 로그아웃
    suspend fun logout(): Boolean

    // 회원탈퇴
    suspend fun withdraw(): Boolean
}