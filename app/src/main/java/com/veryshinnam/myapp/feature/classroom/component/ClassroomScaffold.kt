package com.veryshinnam.myapp.feature.classroom.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.veryshinnam.myapp.R
import com.veryshinnam.myapp.common.component.BackButton
import com.veryshinnam.myapp.common.component.LogoBar

/**
 * 학급 화면 공통 틀
 * : 상태바 여백 + 상단 로고 + 뒤로 가기 버튼 + 내용 (다른 화면과 동일한 구성)
 *
 * - onBack: 뒤로 가기 버튼 클릭 시
 * - onLogoClick: 로고 클릭 시, 홈 화면으로 복구
 */
@Composable
fun ClassroomScaffold(
    onBack: () -> Unit,
    onLogoClick: () -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    Scaffold(
        containerColor = colorResource(id = R.color.background_yellow),
        topBar = {
            // 상태바 만큼 여백 & 상단 로고
            Column {
                Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
                LogoBar(onLogoClick = onLogoClick)
            }
        },
        bottomBar = {
            // 네비게이션바 만큼 여백
            Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            // 뒤로 가기 버튼
            BackButton(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .zIndex(1f),
                onBackClick = onBack
            )

            content()
        }
    }
}

// 조회 로딩
@Composable
fun ClassroomLoading() {
    CircularProgressIndicator(
        color = colorResource(id = R.color.main_orange), // 주황색
        trackColor = Color.Gray.copy(alpha = 0.5f),
        strokeWidth = 4.dp
    )
}
