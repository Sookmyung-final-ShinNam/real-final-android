package com.veryshinnam.myapp.feature.character.data.repository

import com.veryshinnam.myapp.core.network.BaseResponse
import com.veryshinnam.myapp.feature.character.data.api.CharacterApi
import com.veryshinnam.myapp.feature.character.data.dto.CharacterDetailResult
import com.veryshinnam.myapp.feature.character.data.dto.CharactersResult
import com.veryshinnam.myapp.feature.character.data.dto.toCharacterData
import com.veryshinnam.myapp.feature.character.data.dto.toCollectionData
import com.veryshinnam.myapp.feature.character.data.dto.toStoriesData
import com.veryshinnam.myapp.feature.character.model.CharacterData
import com.veryshinnam.myapp.feature.character.model.StoriesData
import com.veryshinnam.myapp.feature.collection.model.CollectionData
import com.veryshinnam.myapp.feature.creation.data.api.ConversationApi
import javax.inject.Inject

class CharacterRepositoryImpl @Inject constructor(
    private val characterApi: CharacterApi,
    private val conversationApi: ConversationApi
) : CharacterRepository {

    // 캐릭터 전체 조회
    override suspend fun getCharacters(gender: String?): List<CollectionData> {
        val response: BaseResponse<CharactersResult> = characterApi.getCharacters(gender)

        if (!response.isSuccess || response.result == null) {
            throw Exception("캐릭터 전체 조회 실패: ${response.message}")
        }
//        return response.result.characters.map { it.toCollectionData() } // 스토리 미완성 캐릭터도 조회 가능
        return response.result.characters.mapNotNull  { it.toCollectionData() } // 스토리 완성된 캐릭터만 조회 가능
    }

    // 캐릭터 상세 조회
    override suspend fun getCharacterDetail(id: Long): CharacterData {
        val response: BaseResponse<CharacterDetailResult> = characterApi.getCharacterDetail(id)

        if (!response.isSuccess || response.result == null) {
            throw Exception("캐릭터 상세 조회 실패: ${response.message}")
        }
        return response.result.toCharacterData()
    }

    // 캐릭터 스토리 조회
    override suspend fun getStories(characterId: Long): StoriesData {
        val response = characterApi.getCharacterDetail(characterId)

        if (!response.isSuccess || response.result == null) {
            throw Exception(" 캐릭터 스토리 조회 실패: ${response.message}")
        }
        return response.result.toStoriesData()
    }

    // 관심 캐릭터 등록
    override suspend fun addFavorite(id: Long): Boolean {
        val res = characterApi.addFavorite(id)

        if (!res.isSuccess) {
            throw Exception("관심 캐릭터 등록 실패: ${res.message}")
        }
        return true
    }

    // 관심 캐릭터 취소
    override suspend fun removeFavorite(id: Long): Boolean {
        val res = characterApi.deleteFavorite(id)

        if (!res.isSuccess) {
            throw Exception("관심 캐릭터 취소 실패: ${res.message}")
        }
        return true
    }

    // 페이지별 동영상 생성
    override suspend fun generateVideo(id: Long): Boolean {
        val res = conversationApi.generateVideo(id)

        if (!res.isSuccess) {
            throw Exception("페이지별 동영상 생성 실패: ${res.message}")
        }
        return true
    }
}