package com.veryshinnam.myapp.feature.settings.data.api

import com.veryshinnam.myapp.common.model.UserRole
import com.veryshinnam.myapp.core.network.BaseResponse
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Query

interface UserApi {

    // 사용자 권한 확인 api
    @GET("/api/user/check-role")
    suspend fun checkRole(
        @Query("role") role: UserRole
    ): BaseResponse<Boolean>

    // 로그아웃 api
    @PATCH("api/user/logout")
    suspend fun logout(): BaseResponse<String>

    // 회원 탈퇴 api
    @DELETE("api/user/withdraw")
    suspend fun withdraw(): BaseResponse<String>
}