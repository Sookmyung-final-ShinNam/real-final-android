@file:OptIn(UnstableApi::class)

package com.veryshinnam.myapp.feature.permit.ui

import android.content.pm.ActivityInfo
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout
import com.veryshinnam.myapp.R
import com.veryshinnam.myapp.common.component.LogoTitle
import com.veryshinnam.myapp.common.component.VideoPlayer
import com.veryshinnam.myapp.core.orientation.OrientationManager

/**
 * 스플래시 화면
 * : 앱 시작과 동시에 mp4 재생 & 토큰 만료 체크 진행
 *
 * - onHome: 토큰 유효 & 사용지 > 홈 화면(HomeScreen)으로 이동
 * - onAdmin: 토큰 유효 & 관리자 > 관리자 화면(AdminScreen)으로 이동
 * - onLogin: 토큰 만료 > 로그인 화면(LoginScreen)으로 이동
 */
@Composable
fun SplashScreen(
    onHome: () -> Unit,
    onAdmin: () -> Unit,
    onLogin: () -> Unit,
    vm: PermitViewModel = hiltViewModel()
) {
    val state by vm.permitUiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val splashVideo = "android.resource://${context.packageName}/${R.raw.splash}"

    // 세로 모드 고정
    SideEffect {
        OrientationManager.setOrientation?.invoke(
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        )
    }

    var videoFinished by remember { mutableStateOf(false) }
    LaunchedEffect(videoFinished, state) {
        if (!videoFinished) return@LaunchedEffect

        when (state) {
            is PermitUiState.User -> onHome()
            is PermitUiState.Admin -> onAdmin()
            is PermitUiState.Error -> onLogin()
            else -> Unit
        }
    }

    LaunchedEffect(Unit) {
        vm.clearReviewStateIfNeeded() // 리뷰 기간 종료 시 상태 정리
        vm.checkAccessToken()
    }

    //  스플래시 UI
    Column(
        modifier = Modifier.fillMaxSize()
            .background(colorResource(R.color.background_yellow)),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1.5f))

        // 로고
        LogoTitle(modifier = Modifier.weight(1.5f))

        // mp4
        VideoPlayer(
            videoUrl = splashVideo,
            modifier = Modifier
                .weight(6f)
                .fillMaxWidth(),
            isRepeated = false,
            resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT, // 원본 비율 유지
            onVideoEnd = {
                videoFinished = true
            }
        )

        Spacer(modifier = Modifier.weight(1f))
    }
}