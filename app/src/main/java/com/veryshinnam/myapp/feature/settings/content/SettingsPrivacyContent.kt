package com.veryshinnam.myapp.feature.settings.content

import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.veryshinnam.myapp.core.network.BaseUrls

/**
 * 개인정보처리 방침
 * : 웹뷰로 개인정보처리 방침 페이지 로드
 */
@Composable
fun SettingsPrivacyContent(
    modifier: Modifier = Modifier
) {
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )

                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true       // crash 방지
                    mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
                    allowFileAccess = false
                    allowContentAccess = false
                }

                loadUrl(BaseUrls.PRIVACY_URL) // 개인정보처리 방침 페이지 로드
            }
        },
        modifier = modifier
            .fillMaxSize()
    )
}