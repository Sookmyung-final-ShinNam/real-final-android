package com.veryshinnam.myapp.feature.collection.model

import com.veryshinnam.myapp.common.model.Gender
import com.veryshinnam.myapp.common.model.ImageType

data class CollectionData(
    val id: Long,       // 캐릭터 아이디
    val name: String,   // 캐릭터 이름
    val image: ImageType, // 캐릭터 이미지
    val gender: Gender, // 캐릭터 성별
    val isFavorite: Boolean, // 캐릭터 즐찾 여부
    val creatorName: String? = null // 만든 사용자 닉네임 (학급 동화에서만 값이 있음)
)