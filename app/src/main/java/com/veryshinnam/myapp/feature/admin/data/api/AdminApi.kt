package com.veryshinnam.myapp.feature.admin.data.api

import com.veryshinnam.myapp.core.network.BaseResponse
import com.veryshinnam.myapp.feature.admin.model.AdminStory
import com.veryshinnam.myapp.feature.admin.model.FailedStory
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface AdminApi {

    // 링크가 누락된 이미지/동영상 동화 모두 조회
    @GET("/api/admin/stories/incomplete")
    suspend fun getIncompleteStories(): BaseResponse<List<AdminStory>>

    // 이미지 숏츠 링크 업로드
    @POST("/api/admin/stories/{id}/youtube/image")
    suspend fun uploadImageYoutubeLink(
        @Path("id") id: Long,
        @Query("youtubeLink") youtubeLink: String
    ): BaseResponse<String>

    // 동영상 숏츠 링크 업로드
    @POST("/api/admin/stories/{id}/youtube/video")
    suspend fun uploadVideoYoutubeLink(
        @Path("id") id: Long,
        @Query("youtubeLink") youtubeLink: String
    ): BaseResponse<String>

    // 관리자 여부 확인 - 스플래시에서 분기
    @GET("/api/user/is-admin")
    suspend fun checkIsAdmin(): BaseResponse<Boolean>

    // 재생성 배치 실패한 동화 보기
    @GET("/api/admin/stories/failed-retry")
    suspend fun getFailedStories(): BaseResponse<List<FailedStory>>
}