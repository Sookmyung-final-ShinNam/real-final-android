package com.veryshinnam.myapp.feature.character.data.dto

import com.veryshinnam.myapp.common.model.Gender
import com.veryshinnam.myapp.common.model.ImageType
import com.veryshinnam.myapp.feature.character.model.CharacterData
import com.veryshinnam.myapp.feature.character.model.StoriesData
import com.veryshinnam.myapp.feature.character.model.VideoStatus

fun CharacterDetailResult.toCharacterData(): CharacterData =
    CharacterData(
        id = characterId,
        name = name,
        gender = Gender.valueOf(gender),
        age = age,
        image = ImageType.Url(imageUrl),
        personality = personality,
        birth = createTime.substring(0, 10),
        isFavorite = important,
        stories = this.toStoriesData()
    )

fun CharacterDetailResult.toStoriesData(): StoriesData =
    StoriesData(
        storyId = storyId,
        title = storyTitle,
        imageUrl = ImageType.Url(imageStoryUrl), // 기본값
        imageYLink = imageYoutubeLink,
        videoStatus = videoStatus.toVideoStatus(),
        videoUrl = videoStoryUrl,
        videoYLink = videoYoutubeLink
    )

private fun String.toVideoStatus(): VideoStatus =
    when (this) {
        "NONE" -> VideoStatus.NONE
        "VIDEO_COMPLETED" -> VideoStatus.COMPLETED
        "COMPLETED" -> VideoStatus.COMPLETED // 기존 응답 (다음 업데이트에서 삭제)
        else -> VideoStatus.MAKING // "VIDEO_MAKING", "VIDEO_FAILED" 처리
    }