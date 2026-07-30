package com.veryshinnam.myapp.feature.permit.ui

sealed interface PermitUiState {
    object Idle : PermitUiState // 스플래시
    object Loading : PermitUiState
    object User : PermitUiState  // 유저 > 홈 화면
    object Admin : PermitUiState // 관리자 > 관리자 화면
    data class Error(val message: String) : PermitUiState
}