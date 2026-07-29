package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MacOSDarkCard
import com.example.ui.theme.MacOSLightCard

@Composable
fun AppleMenuPopup(
    isOpen: Boolean,
    isDarkMode: Boolean,
    onDismiss: () -> Unit,
    onAboutMacClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onLaunchpadClick: () -> Unit,
    onLockClick: () -> Unit,
    onRestartClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!isOpen) return

    val bg = if (isDarkMode) MacOSDarkCard else MacOSLightCard
    val textColor = if (isDarkMode) Color.White else Color(0xFF1D1D1F)

    Surface(
        modifier = modifier
            .width(240.dp)
            .shadow(16.dp, RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .background(bg)
            .testTag("apple_menu_popup"),
        color = bg,
        tonalElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp)
        ) {
            AppleMenuItem("About This Mac", textColor) {
                onDismiss()
                onAboutMacClick()
            }
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
                color = textColor.copy(alpha = 0.12f)
            )
            AppleMenuItem("System Settings...", textColor) {
                onDismiss()
                onSettingsClick()
            }
            AppleMenuItem("App Store / Launchpad", textColor) {
                onDismiss()
                onLaunchpadClick()
            }
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
                color = textColor.copy(alpha = 0.12f)
            )
            AppleMenuItem("Force Quit Apps...", textColor) {
                onDismiss()
                onLaunchpadClick()
            }
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
                color = textColor.copy(alpha = 0.12f)
            )
            AppleMenuItem("Sleep", textColor) {
                onDismiss()
            }
            AppleMenuItem("Restart...", textColor) {
                onDismiss()
                onRestartClick()
            }
            AppleMenuItem("Shut Down...", textColor) {
                onDismiss()
                onRestartClick()
            }
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
                color = textColor.copy(alpha = 0.12f)
            )
            AppleMenuItem("Lock Screen", textColor) {
                onDismiss()
                onLockClick()
            }
        }
    }
}

@Composable
private fun AppleMenuItem(
    label: String,
    textColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Normal
        )
    }
}
