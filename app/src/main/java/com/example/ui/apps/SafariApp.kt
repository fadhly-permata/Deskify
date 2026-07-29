package com.example.ui.apps

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.theme.MacOSBlue

@Composable
fun SafariApp(
    isDarkMode: Boolean,
    initialUrl: String = "https://www.google.com"
) {
    var urlText by remember { mutableStateOf(initialUrl) }
    var currentLoadedUrl by remember { mutableStateOf(initialUrl) }
    var webViewRef by remember { mutableStateOf<WebView?>(null) }

    val toolbarBg = if (isDarkMode) Color(0xFF2A2A2A) else Color(0xFFEBEBEB)
    val textColor = if (isDarkMode) Color.White else Color(0xFF1D1D1F)

    Column(modifier = Modifier.fillMaxSize()) {
        // Safari Toolbar / Address Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .background(toolbarBg)
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { webViewRef?.goBack() },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = textColor,
                        modifier = Modifier.size(16.dp)
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
                        modifier = Modifier.size(16.dp)
                    )
                }
                IconButton(
                    onClick = { webViewRef?.reload() },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        tint = textColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Address Input
            OutlinedTextField(
                value = urlText,
                onValueChange = { urlText = it },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Secure",
                        tint = Color.Gray,
                        modifier = Modifier.size(14.dp)
                    )
                },
                trailingIcon = {
                    IconButton(onClick = {
                        val formattedUrl = if (!urlText.startsWith("http://") && !urlText.startsWith("https://")) {
                            "https://$urlText"
                        } else urlText
                        currentLoadedUrl = formattedUrl
                        webViewRef?.loadUrl(formattedUrl)
                    }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Go",
                            tint = MacOSBlue,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = if (isDarkMode) Color(0xFF1E1E1E) else Color.White,
                    unfocusedContainerColor = if (isDarkMode) Color(0xFF1E1E1E) else Color.White,
                    focusedBorderColor = MacOSBlue,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = textColor,
                    unfocusedTextColor = textColor
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(32.dp)
                    .padding(horizontal = 12.dp)
                    .testTag("safari_url_input")
            )

            Text(
                text = "Safari",
                color = textColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        HorizontalDivider(color = textColor.copy(alpha = 0.1f))

        // Bookmarks Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(if (isDarkMode) Color(0xFF222222) else Color(0xFFF5F5F7))
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            listOf(
                "Google" to "https://www.google.com",
                "Wikipedia" to "https://www.wikipedia.org",
                "Apple" to "https://www.apple.com",
                "GitHub" to "https://github.com"
            ).forEach { (name, link) ->
                Text(
                    text = name,
                    color = MacOSBlue,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .clickable {
                            urlText = link
                            currentLoadedUrl = link
                            webViewRef?.loadUrl(link)
                        }
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }
        }

        // WebView Content
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    webViewClient = object : WebViewClient() {
                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            if (url != null) {
                                urlText = url
                            }
                        }
                    }
                    loadUrl(currentLoadedUrl)
                    webViewRef = this
                }
            },
            update = { webView ->
                if (webView.url != currentLoadedUrl) {
                    webView.loadUrl(currentLoadedUrl)
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .testTag("safari_webview")
        )
    }
}
