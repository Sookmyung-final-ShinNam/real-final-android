package com.veryshinnam.myapp.feature.character.data.dto

import com.veryshinnam.myapp.feature.collection.model.CollectionData
import com.veryshinnam.myapp.common.model.Gender
import com.veryshinnam.myapp.common.model.ImageType

fun CharacterResult.toCollectionData(): CollectionData? {
    // 이미지 없는 캐릭터 null 처리 > repository에서 필터링
    if (imageUrl.isNullOrBlank()) return null

    return CollectionData(
        id = characterId,
        name = name,
        image = ImageType.Url(imageUrl),
        gender = Gender.valueOf(gender),
        isFavorite = important,
        creatorName = creatorNickname
    )
}
