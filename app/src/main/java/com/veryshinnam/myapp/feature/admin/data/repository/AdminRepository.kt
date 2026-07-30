package com.veryshinnam.myapp.feature.admin.data.repository

import com.veryshinnam.myapp.feature.admin.model.AdminStory
import com.veryshinnam.myapp.feature.admin.model.FailedStory

interface AdminRepository {

    // 링크가 누락된 이미지/동영상 동화 모두 조회
    suspend fun getIncompleteStories(): List<AdminStory>

    // 이미지 숏츠 링크 업로드
    suspend fun uploadImageYoutubeLink(id: Long, youtubeLink: String): String

    // 동영상 숏츠 링크 업로드
    suspend fun uploadVideoYoutubeLink(id: Long, youtubeLink: String): String

    // 관리자 여부 확인 - 스플래시에서 분기
    suspend fun checkIsAdmin(): Boolean

    // 재생성 배치 실패한 동화 보기
    suspend fun getFailedStories(): List<FailedStory>
}