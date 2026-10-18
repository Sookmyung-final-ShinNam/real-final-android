package com.veryshinnam.myapp.feature.character.data.dto

// 캐릭터 상세 조회 응답 결과
data class CharacterDetailResult (
    val characterId: Long,
    val name: String,
    val gender: String,
    val age: Int,
    val imageUrl: String,
    val personality: String,
    val important: Boolean,
    val createTime: String,
    val creatorNickname: String? = null, // 만든 사용자 닉네임
    val storyId: Long,
    val storyTitle: String,
    val imageStoryUrl: String,
    val videoStatus: String,
    val videoStoryUrl: String?,
    val imageYoutubeLink: String?,
    val videoYoutubeLink: String?
)
