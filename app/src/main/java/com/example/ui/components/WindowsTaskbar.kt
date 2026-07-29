package com.example.ui.components

import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import com.example.model.DockItemModel
import com.example.model.WindowAppType
import com.example.model.WindowState
import com.example.ui.theme.Win11Blue
import com.example.ui.theme.Win11DarkTaskbar
import com.example.ui.theme.Win11LightTaskbar
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WindowsTaskbar(
    isDarkMode: Boolean,
    openWindows: List<WindowState>,
    activeWindowId: String?,
    dockItems: List<DockItemModel>,
    isStartMenuOpen: Boolean,
    isQuickSettingsOpen: Boolean,
    isCalendarOpen: Boolean,
    onToggleStartMenu: () -> Unit,
    onToggleQuickSettings: () -> Unit,
    onToggleCalendar: () -> Unit,
    onTaskbarItemClick: (DockItemModel) -> Unit,
    onWindowClick: (WindowState) -> Unit,
    onShowDesktop: () -> Unit,
    modifier: Modifier = Modifier
) {
    val taskbarBg = if (isDarkMode) Win11DarkTaskbar else Win11LightTaskbar
    val iconColor = if (isDarkMode) Color.White else Color(0xFF1C1C1C)

    var currentTimeText by remember { mutableStateOf("") }
    var currentDateText by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        while (true) {
            val now = Date()
            currentTimeText = SimpleDateFormat("h:mm a", Locale.getDefault()).format(now)
            currentDateText = SimpleDateFormat("d/M/yyyy", Locale.getDefault()).format(now)
            delay(1000)
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(taskbarBg)
            .navigationBarsPadding()
            .height(48.dp)
            .testTag("windows_taskbar")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Empty spacer for balance or Left Widgets icon
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .clickable { }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Widgets,
                        contentDescription = "Widgets",
                        tint = iconColor.copy(alpha = 0.8f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Center Taskbar App Row (Start, Search, Task View, App Icons)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.weight(1f, fill = false)
            ) {
                // Windows 11 Start Button
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isStartMenuOpen) Win11Blue.copy(alpha = 0.15f) else Color.Transparent)
                        .clickable { onToggleStartMenu() }
                        .testTag("taskbar_start_button"),
                    contentAlignment = Alignment.Center
                ) {
                    // Windows 11 4-Square Logo
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                            Box(modifier = Modifier.size(7.dp).background(Color(0xFF00ADEF), RoundedCornerShape(1.dp)))
                            Box(modifier = Modifier.size(7.dp).background(Color(0xFF00ADEF), RoundedCornerShape(1.dp)))
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                            Box(modifier = Modifier.size(7.dp).background(Color(0xFF00ADEF), RoundedCornerShape(1.dp)))
                            Box(modifier = Modifier.size(7.dp).background(Color(0xFF00ADEF), RoundedCornerShape(1.dp)))
                        }
                    }
                }

                // Search Icon
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .clickable { onToggleStartMenu() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = iconColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Task View Icon
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .clickable { },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.GridView,
                        contentDescription = "Task View",
                        tint = iconColor,
                        modifier = Modifier.size(19.dp)
                    )
                }

                // Pinned & Running Taskbar Icons
                dockItems.forEach { item ->
                    val isRunning = openWindows.any { it.appType == item.appType || (item.packageName != null && it.packageName == item.packageName) }
                    val isFocused = openWindows.any { (it.appType == item.appType || (item.packageName != null && it.packageName == item.packageName)) && it.id == activeWindowId && !it.isMinimized }

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isFocused) Win11Blue.copy(alpha = 0.2f) else Color.Transparent)
                            .clickable { onTaskbarItemClick(item) }
                            .testTag("taskbar_item_${item.id}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            TaskbarIcon(item = item, isDarkMode = isDarkMode)

                            if (isRunning) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Box(
                                    modifier = Modifier
                                        .width(if (isFocused) 16.dp else 6.dp)
                                        .height(3.dp)
                                        .clip(RoundedCornerShape(1.5.dp))
                                        .background(Win11Blue)
                                )
                            }
                        }
                    }
                }

                // Extra open native windows not in dock
                openWindows.filter { win -> dockItems.none { it.appType == win.appType || (it.packageName != null && it.packageName == win.packageName) } }.forEach { win ->
                    val isFocused = win.id == activeWindowId && !win.isMinimized

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isFocused) Win11Blue.copy(alpha = 0.2f) else Color.Transparent)
                            .clickable { onWindowClick(win) }
                            .testTag("taskbar_open_window_${win.id}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            if (win.appIcon != null) {
                                val bitmap = win.appIcon.toBitmap(64, 64)
                                Image(
                                    bitmap = bitmap.asImageBitmap(),
                                    contentDescription = win.title,
                                    modifier = Modifier.size(22.dp)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Folder,
                                    contentDescription = win.title,
                                    tint = Win11Blue,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(2.dp))
                            Box(
                                modifier = Modifier
                                    .width(if (isFocused) 16.dp else 6.dp)
                                    .height(3.dp)
                                    .clip(RoundedCornerShape(1.5.dp))
                                    .background(Win11Blue)
                            )
                        }
                    }
                }
            }

            // Right System Tray (Chevron, Quick Settings Pill, Date/Time, Notification, Show Desktop)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Chevron
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .clickable { },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = "Hidden icons",
                        tint = iconColor,
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Quick Settings System Pill (Wi-Fi, Volume, Battery)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isQuickSettingsOpen) Win11Blue.copy(alpha = 0.18f) else Color.Transparent)
                        .clickable { onToggleQuickSettings() }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("taskbar_quick_settings_pill"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Wifi,
                            contentDescription = "Wi-Fi",
                            tint = iconColor,
                            modifier = Modifier.size(15.dp)
                        )
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Volume",
                            tint = iconColor,
                            modifier = Modifier.size(15.dp)
                        )
                        Icon(
                            imageVector = Icons.Default.BatteryFull,
                            contentDescription = "Battery",
                            tint = iconColor,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }

                // Date & Time Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isCalendarOpen) Win11Blue.copy(alpha = 0.18f) else Color.Transparent)
                        .clickable { onToggleCalendar() }
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                        .testTag("taskbar_calendar_pill"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = currentTimeText,
                            color = iconColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.End
                        )
                        Text(
                            text = currentDateText,
                            color = iconColor.copy(alpha = 0.8f),
                            fontSize = 10.sp,
                            textAlign = TextAlign.End
                        )
                    }
                }

                // Notification Center Icon
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .clickable { onToggleCalendar() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = iconColor,
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Show Desktop thin line at far right
                Box(
                    modifier = Modifier
                        .width(6.dp)
                        .fillMaxHeight()
                        .clickable { onShowDesktop() }
                        .testTag("taskbar_show_desktop")
                )
            }
        }
    }
}

@Composable
private fun TaskbarIcon(item: DockItemModel, isDarkMode: Boolean) {
    if (item.customIcon != null) {
        val bitmap = remember(item.customIcon) { item.customIcon.toBitmap(64, 64) }
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = item.label,
            modifier = Modifier.size(22.dp)
        )
    } else {
        val iconVector = when (item.appType) {
            WindowAppType.FINDER -> Icons.Default.Folder
            WindowAppType.SAFARI -> Icons.Default.Language
            WindowAppType.NOTES -> Icons.Default.EditNote
            WindowAppType.CALCULATOR -> Icons.Default.Calculate
            WindowAppType.TERMINAL -> Icons.Default.Code
            WindowAppType.SETTINGS -> Icons.Default.Settings
            else -> Icons.Default.Folder
        }

        val tintColor = when (item.appType) {
            WindowAppType.FINDER -> Color(0xFFFFC107) // Explorer Gold
            WindowAppType.SAFARI -> Color(0xFF0078D4) // Edge Blue
            WindowAppType.NOTES -> Color(0xFF00838F) // Notepad Cyan
            WindowAppType.CALCULATOR -> Color(0xFF009688)
            WindowAppType.TERMINAL -> Color(0xFF3F51B5)
            WindowAppType.SETTINGS -> Color(0xFF78909C)
            else -> Win11Blue
        }

        Icon(
            imageVector = iconVector,
            contentDescription = item.label,
            tint = tintColor,
            modifier = Modifier.size(20.dp)
        )
    }
}
