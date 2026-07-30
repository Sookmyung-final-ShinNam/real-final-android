package com.veryshinnam.myapp.feature.permit.ui

import android.content.Context
import android.content.pm.ActivityInfo
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.veryshinnam.myapp.R
import com.veryshinnam.myapp.common.component.LogoTitle
import com.veryshinnam.myapp.core.orientation.OrientationManager
import com.veryshinnam.myapp.feature.permit.content.LoginErrorContent
import com.veryshinnam.myapp.feature.permit.content.LoginKakaoContent

/**
 * 로그인 화면
 * : 토큰 만료 시 초기 화면으로, 하단 카카오 로그인 버튼 누르면 카카오 로그인(LoginKakaoContent) 진행
 *
 * - onHome: 기존 유저 > 홈 화면(HomeScreen)으로 이동
 * - onAdmin: 기존 관리자 > 관리자 화면(AdminScreen)으로 이동
 * - onSignup: 신규 유저 > 회원가입 화면(SignUpScreen)으로 이동
 */
@Composable
fun LoginScreen(
    onHome: () -> Unit,
    onAdmin: () -> Unit,
    onSignup: (String) -> Unit, // 로그인 임시 코드 전달
    vm: PermitViewModel = hiltViewModel()
) {
    val state by vm.permitUiState.collectAsStateWithLifecycle()

    val context = LocalContext.current
    fun canAccessNetwork(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
    var isOffline by remember { mutableStateOf(false) }
    var isKakaoLogin by remember { mutableStateOf(false) } // 카카오 로그인 웹뷰 띄우기


    // 세로 모드 고정
    SideEffect {
        OrientationManager.setOrientation?.invoke(
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        )
    }

    LaunchedEffect(state) {
        when (state) {
            is PermitUiState.User -> onHome()
            is PermitUiState.Admin -> onAdmin()
            is PermitUiState.Error -> {
                isKakaoLogin = false // 로그인 재시도 유도
            }
            else -> Unit
        }
    }

    // 백핸들러 설정
    BackHandler {
        when {
            isKakaoLogin -> isKakaoLogin = false
            isOffline -> isOffline = false
        }
    }

    when {
        isOffline -> {
            LoginErrorContent(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colorResource(R.color.background_yellow)),
                onBack = { isOffline = false }
            )
        }

        isKakaoLogin -> {
            LoginKakaoContent(
                modifier = Modifier.fillMaxSize(),
                onTempCodeReceived = { tempCode, isNewUser, isAgreedToTerms ->
                    // 신규 유저 + 약관 미동의
                    if (isNewUser || !isAgreedToTerms) {
                        onSignup(tempCode)
                    } else {
                        // 약관 동의한 기존 유저
                        vm.login(tempCode)
                    }
                }
            )
        }

        else -> {
            // 카카오 로그인 진행 이전 UI
            Column(
                modifier = Modifier.fillMaxSize()
                    .background(colorResource(R.color.background_yellow)),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(Modifier.fillMaxHeight(0.15f))
                // 로고
                LogoTitle(modifier = Modifier)


                // 이미지
                Spacer(Modifier.fillMaxHeight(0.17f))
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center // 중앙 정렬
                ) {
                    Image(
                        painter = painterResource(R.drawable.img_login),
                        contentDescription = null, // 장식용
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }

                // 로그인 버튼
                Box(
                    modifier = Modifier
                        .fillMaxHeight(0.2f)
                        .fillMaxWidth(0.8f),
                    contentAlignment = Alignment.Center // 중앙 정렬
                ) {
                    Image(
                        painter = painterResource(R.drawable.img_kakao_login),
                        contentDescription = "카카오 로그인. 인터넷 연결이 필요합니다.",
                        modifier = Modifier.fillMaxSize()
                            .clickable{
                                if (canAccessNetwork(context)) {
                                    isKakaoLogin = true
                                } else {
                                    isOffline = true
                                }
                            }, // 로그인 버튼 클릭시 활성화
                        contentScale = ContentScale.Fit
                    )
                }

                Spacer(Modifier.fillMaxHeight(0.15f))
            }
        }
    }
}