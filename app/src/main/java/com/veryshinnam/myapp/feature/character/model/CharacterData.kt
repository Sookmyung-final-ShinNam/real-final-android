package com.veryshinnam.myapp.feature.character.model

import com.veryshinnam.myapp.common.model.Gender
import com.veryshinnam.myapp.common.model.ImageType

data class CharacterData(
    val id: Long,            // 캐릭터 아이디
    val name: String,        // 캐릭터 이름
    val gender: Gender,      // 캐릭터 성별
    val age: Int,            // 캐릭터 나이
    val image: ImageType,    // 캐릭터 이미지
    val personality: String, // 캐릭터 성격
    val birth: String,       // 캐릭터 생성일
    val isFavorite: Boolean, // 캐릭터 즐찾 여부
    val stories: StoriesData, // 캐릭터 동화(이미지-영상) 정보
    val creatorName: String? = null // 만든 사용자 닉네임 (학급 동화에서만 표시)
)
