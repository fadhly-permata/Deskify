package com.example.ui.apps

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Dock
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.MacOSBlue

@Composable
fun SettingsApp(
    wallpaperId: String,
    isDarkMode: Boolean,
    dockSizeDp: Int,
    dockMagnification: Boolean,
    onWallpaperChange: (String) -> Unit,
    onDarkModeToggle: (Boolean) -> Unit,
    onDockSizeChange: (Int) -> Unit,
    onDockMagnificationToggle: (Boolean) -> Unit
) {
    var selectedSection by remember { mutableStateOf("Appearance") }

    val sidebarBg = if (isDarkMode) Color(0xFF252525) else Color(0xFFEBEBEB)
    val contentBg = if (isDarkMode) Color(0xFF1E1E1E) else Color.White
    val textColor = if (isDarkMode) Color.White else Color(0xFF1D1D1F)

    val sections = listOf(
        "Appearance" to Icons.Default.Palette,
        "Desktop & Dock" to Icons.Default.Dock,
        "Displays" to Icons.Default.Tv,
        "Battery" to Icons.Default.BatteryFull,
        "About" to Icons.Default.Info
    )

    Row(modifier = Modifier.fillMaxSize().background(contentBg)) {
        // Left Sidebar Navigation
        Column(
            modifier = Modifier
                .width(200.dp)
                .fillMaxHeight()
                .background(sidebarBg)
                .padding(10.dp)
        ) {
            Text(
                text = "System Settings",
                color = textColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp, top = 8.dp, bottom = 12.dp)
            )

            sections.forEach { (label, icon) ->
                val isSelected = selectedSection == label
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) MacOSBlue else Color.Transparent)
                        .clickable { selectedSection = label }
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                        .testTag("settings_nav_$label"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = if (isSelected) Color.White else MacOSBlue,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = label,
                        color = if (isSelected) Color.White else textColor,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
            }
        }

        // Right Settings Section Detail Pane
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = selectedSection,
                color = textColor,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            when (selectedSection) {
                "Appearance" -> {
                    // Dark Mode Toggle
                    SettingCardRow(textColor = textColor, isDarkMode = isDarkMode) {
                        Text("Dark Appearance", color = textColor, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        Switch(
                            checked = isDarkMode,
                            onCheckedChange = onDarkModeToggle,
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = MacOSBlue)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Desktop Wallpaper", color = textColor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        WallpaperCard("default_sequoia", R.drawable.img_wallpaper_macos_default_1785360027008, "Sequoia Default", wallpaperId == "default_sequoia") {
                            onWallpaperChange("default_sequoia")
                        }
                        WallpaperCard("dark_ventura", R.drawable.img_wallpaper_dark_ventura_1785360037431, "Dark Ventura", wallpaperId == "dark_ventura") {
                            onWallpaperChange("dark_ventura")
                        }
                    }
                }

                "Desktop & Dock" -> {
                    SettingCardRow(textColor = textColor, isDarkMode = isDarkMode) {
                        Text("Dock Size", color = textColor, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        Slider(
                            value = dockSizeDp.toFloat(),
                            onValueChange = { onDockSizeChange(it.toInt()) },
                            valueRange = 48f..80f,
                            modifier = Modifier.width(180.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    SettingCardRow(textColor = textColor, isDarkMode = isDarkMode) {
                        Text("Magnification Effect", color = textColor, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        Switch(
                            checked = dockMagnification,
                            onCheckedChange = onDockMagnificationToggle,
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = MacOSBlue)
                        )
                    }
                }

                "Displays" -> {
                    Text("Built-in Display", color = textColor, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text("2560 x 1600 Retina Display — 120Hz ProMotion", color = textColor.copy(alpha = 0.7f), fontSize = 13.sp)
                }

                "Battery" -> {
                    Text("Battery Health: Normal (98%)", color = textColor, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text("Power Source: Battery / Power Adapter", color = textColor.copy(alpha = 0.7f), fontSize = 13.sp)
                }

                "About" -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth().padding(top = 20.dp)
                    ) {
                        Box(
                            modifier = Modifier.size(72.dp).clip(CircleShape).background(MacOSBlue),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("", color = Color.White, fontSize = 36.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text("macOS Sequoia", color = textColor, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text("Version 15.0 (2026 Build)", color = textColor.copy(alpha = 0.6f), fontSize = 13.sp)

                        Spacer(modifier = Modifier.height(16.dp))

                        Text("Processor: Octa-core ARM64", color = textColor, fontSize = 13.sp)
                        Text("Memory: 16 GB Unified RAM", color = textColor, fontSize = 13.sp)
                        Text("Serial Number: C02G90XXMD6M", color = textColor, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingCardRow(
    textColor: Color,
    isDarkMode: Boolean,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(if (isDarkMode) Color(0xFF2D2D2D) else Color(0xFFF2F2F7))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )
}

@Composable
private fun WallpaperCard(
    id: String,
    resId: Int,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(140.dp)
            .clip(RoundedCornerShape(10.dp))
            .border(if (isSelected) 3.dp else 0.dp, if (isSelected) MacOSBlue else Color.Transparent, RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(4.dp)
    ) {
        Image(
            painter = painterResource(id = resId),
            contentDescription = label,
            modifier = Modifier.fillMaxWidth().height(80.dp).clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}
