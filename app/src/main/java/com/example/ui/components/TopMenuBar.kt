package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MacOSDarkBar
import com.example.ui.theme.MacOSLightBar
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TopMenuBar(
    activeAppName: String,
    isDarkMode: Boolean,
    onAppleClick: () -> Unit,
    onControlCenterToggle: () -> Unit,
    onSpotlightClick: () -> Unit,
    onMenuItemClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var currentTime by remember { mutableStateOf("") }
    var activeMenuDropdown by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val dateFormat = SimpleDateFormat("EEE MMM d  h:mm a", Locale.getDefault())
        while (true) {
            currentTime = dateFormat.format(Date())
            delay(1000)
        }
    }

    val barBg = if (isDarkMode) MacOSDarkBar else MacOSLightBar
    val textColor = if (isDarkMode) Color.White else Color(0xFF1D1D1F)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .height(30.dp)
            .background(barBg)
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Side: Apple Logo + Active App Name + App Menus
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                // Apple Icon
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .clickable { onAppleClick() }
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                        .testTag("apple_menu_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "",
                        color = textColor,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Active App Title (Bold)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .clickable { activeMenuDropdown = if (activeMenuDropdown == "App") null else "App" }
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                        .testTag("active_app_menu"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = activeAppName.ifEmpty { "Finder" },
                        color = textColor,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )

                    DropdownMenu(
                        expanded = activeMenuDropdown == "App",
                        onDismissRequest = { activeMenuDropdown = null }
                    ) {
                        DropdownMenuItem(
                            text = { Text("About $activeAppName") },
                            onClick = {
                                activeMenuDropdown = null
                                onMenuItemClick("About $activeAppName")
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Preferences...") },
                            onClick = {
                                activeMenuDropdown = null
                                onMenuItemClick("Preferences")
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Hide $activeAppName") },
                            onClick = {
                                activeMenuDropdown = null
                                onMenuItemClick("Hide")
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Quit $activeAppName") },
                            onClick = {
                                activeMenuDropdown = null
                                onMenuItemClick("Quit")
                            }
                        )
                    }
                }

                // Standard macOS Menus: File, Edit, View, Window, Help
                val menus = listOf("File", "Edit", "View", "Window", "Help")
                menus.forEach { menu ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .clickable {
                                activeMenuDropdown = if (activeMenuDropdown == menu) null else menu
                            }
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = menu,
                            color = textColor.copy(alpha = 0.85f),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Normal
                        )

                        DropdownMenu(
                            expanded = activeMenuDropdown == menu,
                            onDismissRequest = { activeMenuDropdown = null }
                        ) {
                            when (menu) {
                                "File" -> {
                                    DropdownMenuItem(
                                        text = { Text("New Window") },
                                        onClick = { activeMenuDropdown = null; onMenuItemClick("New Window") }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("New Note") },
                                        onClick = { activeMenuDropdown = null; onMenuItemClick("New Note") }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Close Window") },
                                        onClick = { activeMenuDropdown = null; onMenuItemClick("Close Window") }
                                    )
                                }
                                "Edit" -> {
                                    DropdownMenuItem(
                                        text = { Text("Undo") },
                                        onClick = { activeMenuDropdown = null; onMenuItemClick("Undo") }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Cut") },
                                        onClick = { activeMenuDropdown = null; onMenuItemClick("Cut") }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Copy") },
                                        onClick = { activeMenuDropdown = null; onMenuItemClick("Copy") }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Paste") },
                                        onClick = { activeMenuDropdown = null; onMenuItemClick("Paste") }
                                    )
                                }
                                "View" -> {
                                    DropdownMenuItem(
                                        text = { Text("As Icons") },
                                        onClick = { activeMenuDropdown = null; onMenuItemClick("As Icons") }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("As List") },
                                        onClick = { activeMenuDropdown = null; onMenuItemClick("As List") }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Toggle Fullscreen") },
                                        onClick = { activeMenuDropdown = null; onMenuItemClick("Fullscreen") }
                                    )
                                }
                                "Window" -> {
                                    DropdownMenuItem(
                                        text = { Text("Minimize") },
                                        onClick = { activeMenuDropdown = null; onMenuItemClick("Minimize") }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Zoom") },
                                        onClick = { activeMenuDropdown = null; onMenuItemClick("Zoom") }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Bring All to Front") },
                                        onClick = { activeMenuDropdown = null; onMenuItemClick("Bring Front") }
                                    )
                                }
                                "Help" -> {
                                    DropdownMenuItem(
                                        text = { Text("macOS Launcher Help") },
                                        onClick = { activeMenuDropdown = null; onMenuItemClick("Help") }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Right Side: Status Icons + Control Center Trigger + Realtime Clock
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                // Battery
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) {
                    Text(
                        text = "98%",
                        color = textColor.copy(alpha = 0.9f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Icon(
                        imageVector = Icons.Default.BatteryChargingFull,
                        contentDescription = "Battery",
                        tint = textColor,
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Wi-Fi
                Icon(
                    imageVector = Icons.Default.Wifi,
                    contentDescription = "Wi-Fi",
                    tint = textColor,
                    modifier = Modifier
                        .padding(horizontal = 6.dp)
                        .size(15.dp)
                )

                // Bluetooth
                Icon(
                    imageVector = Icons.Default.Bluetooth,
                    contentDescription = "Bluetooth",
                    tint = textColor,
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(15.dp)
                )

                // Spotlight Search Trigger
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Spotlight Search",
                    tint = textColor,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .clickable { onSpotlightClick() }
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                        .size(15.dp)
                        .testTag("spotlight_search_button")
                )

                // Control Center Trigger Icon
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = "Control Center",
                    tint = textColor,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .clickable { onControlCenterToggle() }
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                        .size(15.dp)
                        .testTag("control_center_toggle")
                )

                Spacer(modifier = Modifier.width(6.dp))

                // Date & Clock
                Text(
                    text = currentTime,
                    color = textColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(end = 4.dp)
                )
            }
        }
    }
}
