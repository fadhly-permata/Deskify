package com.example.ui.apps

import android.content.Context
import android.graphics.drawable.Drawable
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.drawable.toBitmap
import com.example.ui.theme.MacOSBlue
import com.example.windowing.FreeformWindowLauncher

@Composable
fun NativeAppView(
    title: String,
    packageName: String?,
    appIcon: Drawable?,
    isDarkMode: Boolean
) {
    val context = LocalContext.current

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

    var urlInput by remember { mutableStateOf(initialUrl) }
    var currentUrl by remember { mutableStateOf(initialUrl) }
    var webViewRef by remember { mutableStateOf<WebView?>(null) }

    val toolbarBg = if (isDarkMode) Color(0xFF2B2B2B) else Color(0xFFE8E8E8)
    val textColor = if (isDarkMode) Color.White else Color(0xFF1D1D1F)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("native_app_view_$packageName")
    ) {
        // Window Inner Navigation & Toolbar Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .background(toolbarBg)
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // App Icon & Title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(end = 8.dp)
            ) {
                if (appIcon != null) {
                    val bitmap = remember(appIcon) { appIcon.toBitmap(64, 64) }
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = title,
                        modifier = Modifier
                            .size(22.dp)
                            .clip(RoundedCornerShape(5.dp))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                }
                Text(
                    text = title,
                    color = textColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.width(90.dp)
                )
            }

            // Web Navigation Controls
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { webViewRef?.goBack() },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = textColor,
                        modifier = Modifier.size(15.dp)
                    )
                }
                IconButton(
                    onClick = { webViewRef?.goForward() },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Forward",
                        tint = textColor,
                        modifier = Modifier.size(15.dp)
                    )
                }
                IconButton(
                    onClick = { webViewRef?.reload() },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reload",
                        tint = textColor,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }

            // Address Input
            OutlinedTextField(
                value = urlInput,
                onValueChange = { urlInput = it },
                trailingIcon = {
                    IconButton(onClick = {
                        val formattedUrl = if (!urlInput.startsWith("http://") && !urlInput.startsWith("https://")) {
                            "https://$urlInput"
                        } else urlInput
                        currentUrl = formattedUrl
                        webViewRef?.loadUrl(formattedUrl)
                    }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Go",
                            tint = MacOSBlue,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = if (isDarkMode) Color(0xFF1C1C1E) else Color.White,
                    unfocusedContainerColor = if (isDarkMode) Color(0xFF1C1C1E) else Color.White,
                    focusedBorderColor = MacOSBlue,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = textColor,
                    unfocusedTextColor = textColor
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(30.dp)
                    .padding(horizontal = 6.dp)
            )

            // External Launch Button
            if (packageName != null) {
                IconButton(
                    onClick = {
                        FreeformWindowLauncher.launchInFreeform(context, packageName)
                    },
                    modifier = Modifier
                        .size(28.dp)
                        .testTag("external_launch_btn_$packageName")
                ) {
                    Icon(
                        imageVector = Icons.Default.OpenInNew,
                        contentDescription = "Pop out to OS Freeform",
                        tint = MacOSBlue,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        HorizontalDivider(color = textColor.copy(alpha = 0.12f))

        // In-Window Interactive App Content (WebView)
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
                    webViewClient = object : WebViewClient() {
                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            if (url != null) {
                                urlInput = url
                            }
                        }
                    }
                    loadUrl(currentUrl)
                    webViewRef = this
                }
            },
            update = { webView ->
                if (webView.url != currentUrl) {
                    webView.loadUrl(currentUrl)
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .testTag("native_app_webview")
        )
    }
}
