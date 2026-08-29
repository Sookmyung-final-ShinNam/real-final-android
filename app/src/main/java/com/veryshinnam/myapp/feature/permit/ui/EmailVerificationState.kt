package com.veryshinnam.myapp.feature.permit.ui

data class EmailVerificationState (
    val isSendLoading: Boolean = false,
    val isSent: Boolean = false,
    val sendErrorMessage: String? = null,

    val isVerifyLoading: Boolean = false,
    val isVerified: Boolean = false,
    val verifyErrorMessage: String? = null
)