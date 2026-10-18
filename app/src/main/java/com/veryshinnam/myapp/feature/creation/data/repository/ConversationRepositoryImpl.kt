package com.veryshinnam.myapp.feature.creation.data.repository

import com.veryshinnam.myapp.core.network.BaseResponse
import com.veryshinnam.myapp.core.network.serverMessage
import com.veryshinnam.myapp.feature.classroom.data.api.ClassroomApi
import com.veryshinnam.myapp.feature.creation.data.api.ConversationApi
import com.veryshinnam.myapp.feature.creation.data.dto.FeedbackResult
import com.veryshinnam.myapp.feature.creation.data.dto.InProgressResult
import com.veryshinnam.myapp.feature.creation.data.dto.toInProgressData
import com.veryshinnam.myapp.feature.creation.data.dto.NextStepResult
import com.veryshinnam.myapp.feature.creation.data.dto.StartRequest
import com.veryshinnam.myapp.feature.creation.data.dto.StartResult
import com.veryshinnam.myapp.feature.creation.data.dto.toFeedbackData
import com.veryshinnam.myapp.feature.creation.model.FeedbackData
import com.veryshinnam.myapp.feature.creation.model.InProgressData
import javax.inject.Inject

class ConversationRepositoryImpl @Inject constructor(
    private val api: ConversationApi,
    private val classroomApi: ClassroomApi
) : ConversationRepository {

    // 과제 진행 (과제용 대화 세션 시작)
    // : 학급 도토리 부족, 미승인 학생 등 서버 에러 메시지를 그대로 전달
    override suspend fun startAssignment(
        classroomId: Long,
        assignmentId: Long,
        request: StartRequest
    ): StartResult {
        val response: BaseResponse<StartResult> = try {
            classroomApi.startAssignment(classroomId, assignmentId, request)
        } catch (e: Exception) {
            throw Exception(e.serverMessage("과제를 시작하지 못했어요."))
        }

        if (!response.isSuccess || response.result == null) {
            throw Exception("과제 진행 실패: ${response.message}")
        }

        return response.result
    }

    // 대화 세션 시작
    override suspend fun startConversation(request: StartRequest): StartResult {
        val response: BaseResponse<StartResult> = api.startConversation(request)

        if (!response.isSuccess || response.result == null) {
            throw Exception("대화 세션 시작 실패: ${response.result}")
        }

        return response.result
    }

    // 진행 중인(끝내지 않은) 대화 세션 조회 (최신순)
    override suspend fun getInProgress(): List<InProgressData> {
        val response: BaseResponse<List<InProgressResult>> = api.getInProgress()

        if (!response.isSuccess || response.result == null) {
            throw Exception("진행 중인 대화 세션 조회 실패: ${response.message}")
        }

        return response.result.map { it.toInProgressData() }
    }

    // 다음 스텝에서 메시지 조회
    override suspend fun getNextStep(
        sessionId: Long,
        currentStep: String
    ): NextStepResult {
        val response: BaseResponse<NextStepResult> = api.getNextStep(sessionId,currentStep)

        if (!response.isSuccess || response.result == null) {
            throw Exception("다음 스텝에서 메시지 조회 실패: ${response.result}")
        }

        return response.result
    }

    // 사용자 답변 피드백
    override suspend fun feedbackConversation(
        messageId: Long,
        userAnswer: String
    ): FeedbackData {
        val response: BaseResponse<FeedbackResult> = api.feedbackConversation(messageId, userAnswer)

        if (!response.isSuccess || response.result == null) {
            throw Exception("사용자 답변 피드백 실패: ${response.result}")
        }

        return response.result.toFeedbackData()
    }

    // 동화 생성 완성
    override suspend fun completeConversation(sessionId: Long): Boolean {
        val response: BaseResponse<Unit> = api.completeConversation(sessionId)

        if (!response.isSuccess) {
            throw Exception("동화 생성 완성 실패: ${response.message}")
        }

        return true
    }
}