package com.veryshinnam.myapp.feature.admin.data.repository

import com.veryshinnam.myapp.core.network.BaseResponse
import com.veryshinnam.myapp.feature.admin.data.api.AdminApi
import com.veryshinnam.myapp.feature.admin.model.AdminStory
import com.veryshinnam.myapp.feature.admin.model.FailedStory
import javax.inject.Inject

class AdminRepositoryImpl @Inject constructor(
    private val adminApi: AdminApi
): AdminRepository {

    // 링크가 누락된 이미지/동영상 동화 모두 조회
    override suspend fun getIncompleteStories():List<AdminStory> {
        val response: BaseResponse<List<AdminStory>> = adminApi.getIncompleteStories()

        if (!response.isSuccess || response.result == null) {
            throw Exception("링크가 누락된 이미지/동영상 동화 조회 실패: ${response.message}")
        }

        return response.result
    }

    // 이미지 숏츠 링크 업로드
    override suspend fun uploadImageYoutubeLink(id: Long, youtubeLink: String): String {
        val response: BaseResponse<String> = adminApi.uploadImageYoutubeLink(id, youtubeLink)

        if (!response.isSuccess || response.result == null) {
            throw Exception("이미지 숏츠 링크 업로드 실패: ${response.message}")
        }

        return response.result
    }

    // 동영상 숏츠 링크 업로드
    override suspend fun uploadVideoYoutubeLink(id: Long, youtubeLink: String): String {
        val response: BaseResponse<String> = adminApi.uploadVideoYoutubeLink(id, youtubeLink)

        if (!response.isSuccess || response.result == null) {
            throw Exception("동영상 숏츠 링크 업로드 실패: ${response.message}")
        }

        return response.result
    }


    // 관리자 여부 확인 - 스플래시에서 분기
    override suspend fun checkIsAdmin(): Boolean {
        val response: BaseResponse<Boolean> = adminApi.checkIsAdmin()

        if (!response.isSuccess || response.result == null) {
            throw Exception("관리자 여부 확인 실패: ${response.message}")
        }

        return response.result
    }

    // 재생성 배치 실패한 동화 보기
    override suspend fun getFailedStories(): List<FailedStory> {
        val response: BaseResponse<List<FailedStory>> = adminApi.getFailedStories()

        if (!response.isSuccess || response.result == null) {
            throw Exception("재생성 배치 실패한 동화 조회 실패: ${response.message}")
        }

        return response.result
    }
}