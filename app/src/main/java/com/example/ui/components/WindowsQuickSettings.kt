package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AirplanemodeActive
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.Win11Blue
import com.example.ui.theme.Win11DarkFlyout
import com.example.ui.theme.Win11LightFlyout

@Composable
fun WindowsQuickSettings(
    isDarkMode: Boolean,
    onToggleDarkMode: (Boolean) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isWifiOn by remember { mutableStateOf(true) }
    var isBluetoothOn by remember { mutableStateOf(true) }
    var isAirplaneOn by remember { mutableStateOf(false) }
    var isBatterySaverOn by remember { mutableStateOf(false) }
    var isNightLightOn by remember { mutableStateOf(false) }

    var volumeValue by remember { mutableFloatStateOf(0.75f) }
    var brightnessValue by remember { mutableFloatStateOf(0.85f) }

    val bg = if (isDarkMode) Win11DarkFlyout else Win11LightFlyout
    val textColor = if (isDarkMode) Color.White else Color(0xFF1C1C1C)
    val cardBg = if (isDarkMode) Color(0xFF2C2C2C) else Color.White
    val borderColor = if (isDarkMode) Color(0x33FFFFFF) else Color(0x1A000000)

    Surface(
        modifier = modifier
            .width(360.dp)
            .shadow(24.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .testTag("windows_quick_settings"),
        color = bg,
        tonalElevation = 16.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Quick Action Tiles Grid (3x2)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Wi-Fi
                QuickTile(
                    title = "Wi-Fi",
                    icon = Icons.Default.Wifi,
                    isActive = isWifiOn,
                    isDarkMode = isDarkMode,
                    onClick = { isWifiOn = !isWifiOn },
                    modifier = Modifier.weight(1f)
                )
                // Bluetooth
                QuickTile(
                    title = "Bluetooth",
                    icon = Icons.Default.Bluetooth,
                    isActive = isBluetoothOn,
                    isDarkMode = isDarkMode,
                    onClick = { isBluetoothOn = !isBluetoothOn },
                    modifier = Modifier.weight(1f)
                )
                // Airplane Mode
                QuickTile(
                    title = "Airplane",
                    icon = Icons.Default.AirplanemodeActive,
                    isActive = isAirplaneOn,
                    isDarkMode = isDarkMode,
                    onClick = { isAirplaneOn = !isAirplaneOn },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Battery Saver
                QuickTile(
                    title = "Saver",
                    icon = Icons.Default.BatteryAlert,
                    isActive = isBatterySaverOn,
                    isDarkMode = isDarkMode,
                    onClick = { isBatterySaverOn = !isBatterySaverOn },
                    modifier = Modifier.weight(1f)
                )
                // Night Light
                QuickTile(
                    title = "Night light",
                    icon = Icons.Default.NightsStay,
                    isActive = isNightLightOn,
                    isDarkMode = isDarkMode,
                    onClick = { isNightLightOn = !isNightLightOn },
                    modifier = Modifier.weight(1f)
                )
                // Dark Theme
                QuickTile(
                    title = if (isDarkMode) "Dark" else "Light",
                    icon = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                    isActive = isDarkMode,
                    isDarkMode = isDarkMode,
                    onClick = { onToggleDarkMode(!isDarkMode) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Brightness Slider
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.LightMode,
                    contentDescription = "Brightness",
                    tint = textColor,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Slider(
                    value = brightnessValue,
                    onValueChange = { brightnessValue = it },
                    colors = SliderDefaults.colors(
                        thumbColor = Win11Blue,
                        activeTrackColor = Win11Blue,
                        inactiveTrackColor = borderColor
                    ),
                    modifier = Modifier.weight(1f)
                )
            }

            // Volume Slider
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.VolumeUp,
                    contentDescription = "Volume",
                    tint = textColor,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Slider(
                    value = volumeValue,
                    onValueChange = { volumeValue = it },
                    colors = SliderDefaults.colors(
                        thumbColor = Win11Blue,
                        activeTrackColor = Win11Blue,
                        inactiveTrackColor = borderColor
                    ),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = borderColor)
            Spacer(modifier = Modifier.height(12.dp))

            // Footer Battery Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.BatteryFull,
                        contentDescription = "Battery",
                        tint = Win11Blue,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "85% • Fully Charged",
                        color = textColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(cardBg)
                        .clickable { onDismiss() }
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text("Done", color = textColor, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun QuickTile(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isActive: Boolean,
    isDarkMode: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tileBg = if (isActive) Win11Blue else if (isDarkMode) Color(0xFF2C2C2C) else Color.White
    val contentColor = if (isActive) Color.White else if (isDarkMode) Color.White else Color(0xFF1C1C1C)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(tileBg)
            .clickable { onClick() }
            .padding(vertical = 12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = contentColor,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = title,
            color = contentColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
