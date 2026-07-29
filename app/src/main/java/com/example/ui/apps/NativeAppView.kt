package com.example.ui.apps

import android.graphics.drawable.Drawable
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun NativeAppView(
    title: String,
    packageName: String?,
    appIcon: Drawable?,
    isDarkMode: Boolean
) {
    val initialUrl = remember(packageName, title) {
        val lowerPkg = packageName?.lowercase() ?: ""
        val lowerTitle = title.lowercase()

        when {
            lowerPkg.contains("youtube") || lowerTitle.contains("youtube") -> "https://m.youtube.com"
            lowerPkg.contains("chrome") || lowerTitle.contains("chrome") -> "https://www.google.com"
            lowerPkg.contains("vending") || lowerTitle.contains("play store") -> "https://play.google.com"
            lowerPkg.contains("maps") || lowerTitle.contains("maps") -> "https://maps.google.com"
            lowerPkg.contains("gm") || lowerTitle.contains("gmail") -> "https://mail.google.com"
            lowerTitle.contains("google") -> "https://www.google.com"
            lowerTitle.contains("twitter") || lowerTitle.contains("x") -> "https://x.com"
            lowerTitle.contains("instagram") -> "https://www.instagram.com"
            lowerTitle.contains("wikipedia") -> "https://m.wikipedia.org"
            else -> "https://www.google.com/search?q=${title.replace(" ", "+")}"
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("native_app_view_$packageName")
    ) {
        AndroidView(
            factory = { ctx ->
                WebView(ctx).apply {
                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        allowFileAccess = true
                        mediaPlaybackRequiresUserGesture = false
                        mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                        userAgentString = "Mozilla/5.0 (Linux; Android 13; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0.0.0 Mobile Safari/537.36"
                    }
                    webViewClient = WebViewClient()
                    loadUrl(initialUrl)
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .testTag("native_app_webview")
        )
    }
}

