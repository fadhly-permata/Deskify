package com.example.ui.apps

import android.graphics.drawable.Drawable
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.theme.MacOSBlue

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

    var urlInput by remember { mutableStateOf(initialUrl) }
    var currentLoadedUrl by remember { mutableStateOf(initialUrl) }
    var webViewRef by remember { mutableStateOf<WebView?>(null) }
    var canGoBack by remember { mutableStateOf(false) }
    var canGoForward by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    val toolbarBg = if (isDarkMode) Color(0xFF222225) else Color(0xFFF1F3F4)
    val inputBg = if (isDarkMode) Color(0xFF1E1E1E) else Color.White
    val textColor = if (isDarkMode) Color.White else Color(0xFF202124)

    fun loadFormattedUrl(input: String) {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) return
        val targetUrl = when {
            trimmed.startsWith("http://") || trimmed.startsWith("https://") -> trimmed
            trimmed.contains(".") && !trimmed.contains(" ") -> "https://$trimmed"
            else -> "https://www.google.com/search?q=${trimmed.replace(" ", "+")}"
        }
        urlInput = targetUrl
        currentLoadedUrl = targetUrl
        webViewRef?.loadUrl(targetUrl)
        focusManager.clearFocus()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("native_app_view_${packageName ?: title}")
    ) {
        // Chrome / Web Browser Navigation Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(toolbarBg)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Navigation buttons
            IconButton(
                onClick = { webViewRef?.goBack() },
                enabled = canGoBack,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = if (canGoBack) textColor else textColor.copy(alpha = 0.38f),
                    modifier = Modifier.size(18.dp)
                )
            }

            IconButton(
                onClick = { webViewRef?.goForward() },
                enabled = canGoForward,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Forward",
                    tint = if (canGoForward) textColor else textColor.copy(alpha = 0.38f),
                    modifier = Modifier.size(18.dp)
                )
            }

            IconButton(
                onClick = { webViewRef?.reload() },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Reload",
                    tint = textColor,
                    modifier = Modifier.size(18.dp)
                )
            }

            IconButton(
                onClick = { loadFormattedUrl("https://www.google.com") },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home",
                    tint = textColor,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Address & Search Bar
            OutlinedTextField(
                value = urlInput,
                onValueChange = { urlInput = it },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Secure",
                        tint = Color.Gray,
                        modifier = Modifier.size(14.dp)
                    )
                },
                trailingIcon = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (urlInput.isNotEmpty()) {
                            IconButton(
                                onClick = { urlInput = "" },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                        IconButton(
                            onClick = { loadFormattedUrl(urlInput) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Go",
                                tint = MacOSBlue,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
                keyboardActions = KeyboardActions(onGo = { loadFormattedUrl(urlInput) }),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = inputBg,
                    unfocusedContainerColor = inputBg,
                    focusedBorderColor = MacOSBlue,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = textColor,
                    unfocusedTextColor = textColor
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(36.dp)
                    .testTag("browser_url_input")
            )

            // App Label Badge
            Text(
                text = title,
                color = textColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(start = 4.dp, end = 4.dp)
            )
        }

        HorizontalDivider(color = textColor.copy(alpha = 0.1f))

        // Bookmarks / Quick Links Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(if (isDarkMode) Color(0xFF1B1B1E) else Color(0xFFE8EAED))
                .padding(horizontal = 12.dp, vertical = 3.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            listOf(
                "Google" to "https://www.google.com",
                "YouTube" to "https://m.youtube.com",
                "Wikipedia" to "https://m.wikipedia.org",
                "Maps" to "https://maps.google.com",
                "Play Store" to "https://play.google.com"
            ).forEach { (label, link) ->
                Text(
                    text = label,
                    color = MacOSBlue,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .clickable { loadFormattedUrl(link) }
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }
        }

        HorizontalDivider(color = textColor.copy(alpha = 0.08f))

        // WebView Area
        AndroidView(
            factory = { ctx ->
                WebView(ctx).apply {
                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        databaseEnabled = true
                        allowFileAccess = true
                        allowContentAccess = true
                        mediaPlaybackRequiresUserGesture = false
                        mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                        cacheMode = WebSettings.LOAD_DEFAULT
                        userAgentString = "Mozilla/5.0 (Linux; Android 13; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0.0.0 Mobile Safari/537.36"
                    }
                    webChromeClient = WebChromeClient()
                    webViewClient = object : WebViewClient() {
                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            if (url != null && view != null) {
                                urlInput = url
                                canGoBack = view.canGoBack()
                                canGoForward = view.canGoForward()
                            }
                        }
                    }
                    loadUrl(currentLoadedUrl)
                    webViewRef = this
                }
            },
            update = { webView ->
                if (webView.url != currentLoadedUrl && currentLoadedUrl.isNotEmpty()) {
                    webView.loadUrl(currentLoadedUrl)
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .testTag("native_app_webview")
        )
    }
}


