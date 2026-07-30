package com.veryshinnam.myapp.feature.admin.model

data class FailedStory(
    val storyId: Long,
    val retryCount: Int,
    val storyStatus: String,
    val updatedAt: String
)