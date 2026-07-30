package com.veryshinnam.myapp.feature.permit.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.veryshinnam.myapp.core.session.SessionManager
import com.veryshinnam.myapp.feature.admin.data.repository.AdminRepository
import com.veryshinnam.myapp.feature.permit.data.repository.PermitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PermitViewModel @Inject constructor(
    private val permitRepository: PermitRepository,
    private val adminRepository: AdminRepository,
    private val sessionManager: SessionManager
) : ViewModel() {
    private val _permitUiState = MutableStateFlow<PermitUiState>(PermitUiState.Idle)
    val permitUiState: StateFlow<PermitUiState> = _permitUiState

    // 액세스 토큰 존재 + 만료 확인
    fun checkAccessToken() {
        viewModelScope.launch {
            _permitUiState.value = PermitUiState.Loading

            try {
                val token = sessionManager.getToken()
                val isExpired = sessionManager.isTokenExpired()
                val isReviewPeriod = sessionManager.isReviewPeriod()
                val isUsingReview = sessionManager.isUsingReviewToken()
                val reviewTokenRejected = sessionManager.isReviewTokenRejected()

                // 리뷰 토큰이 401 받았으면 다시 설정 안 함
                if (reviewTokenRejected) {
                    sessionManager.clearToken() // 혹시 모를 토큰 제거
                    _permitUiState.value = PermitUiState.Error("리뷰 토큰 만료 - 재로그인 필요")
                    return@launch
                }

                // 1. 리뷰 기간 & 리뷰 토큰 401 전까지 강제 로그인
                if (isReviewPeriod) {
                    sessionManager.forceReviewToken()
                    _permitUiState.value = PermitUiState.User
                    return@launch
                }

                // 2. 리뷰 기간 아닌데 리뷰 토큰 가진 경우 로그아웃
                if (!isReviewPeriod && isUsingReview) {
                    sessionManager.clearToken()
                    sessionManager.clearReviewState() // 리뷰 상태 정리
                    _permitUiState.value = PermitUiState.Error("리뷰 기간 종료 - 재로그인 필요")
                    return@launch
                }


                // 3. 일반 토큰 검증 및 사용자/관리자 체크
                if (token != null && !isExpired) {
                    checkUserOrAdmin()
                } else {
                    // 토큰 없거나 만료됨
                    if (token != null) {
                        sessionManager.clearToken() // 만료된 토큰 제거
                    }
                    _permitUiState.value = PermitUiState.Error("토큰 만료 또는 없음")
                }
            } catch (e: Exception) {
                _permitUiState.value = PermitUiState.Error("토큰 확인 실패: ${e.message}")
            }
        }
    }

    // 로그인 사용자 관리자 체크
    private suspend fun checkUserOrAdmin() {
        try {
            val isAdmin = adminRepository.checkIsAdmin()

            _permitUiState.value = if (isAdmin) PermitUiState.Admin else PermitUiState.User
        } catch (e: Exception) {
            _permitUiState.value = PermitUiState.Error("관리자 체크 실패: ${e.message}")
        }
    }

    // 회원가입
    fun signup(tempCode: String) {
        saveToken(tempCode, isNewUser = true)
    }

    // 로그인
    fun login(tempCode: String) {
        saveToken(tempCode, isNewUser = false)
    }

    // 공통 로직
    fun saveToken(tempCode: String, isNewUser: Boolean) {
        viewModelScope.launch {
            _permitUiState.value = PermitUiState.Loading

            try {
                val jwt = permitRepository.login(tempCode) // api 호출

                // 세션 저장
                sessionManager.saveToken(
                    jwt.accessToken,
                    jwt.refreshToken,
                    jwt.accessTokenExpiredAt
                )

                // 신규 유저인 경우 플래그 설정
                if (isNewUser) sessionManager.saveNewUser(true)

                checkUserOrAdmin()
            } catch (e: Exception) {
                _permitUiState.value = PermitUiState.Error("로그인 실패: ${e.message}")
            }
        }
    }

    // 상태 초기화
    fun resetState() {
        _permitUiState.value = PermitUiState.Idle
    }

    // 리뷰 기간 종료 시 상태 정리
    fun clearReviewStateIfNeeded() {
        viewModelScope.launch {
            if (!sessionManager.isReviewPeriod()) {
                sessionManager.clearReviewState()
            }
        }
    }
}