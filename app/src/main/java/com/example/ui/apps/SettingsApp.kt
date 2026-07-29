package com.example.ui.apps

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.AddLink
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Dock
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.ui.theme.MacOSBlue
import java.io.File
import java.io.FileOutputStream
import androidx.compose.ui.platform.testTag

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
                    val context = LocalContext.current
                    var customUrlText by remember { mutableStateOf("") }

                    val cameraLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.TakePicturePreview()
                    ) { bitmap ->
                        bitmap?.let {
                            try {
                                val file = File(context.filesDir, "custom_wallpaper_cam_${System.currentTimeMillis()}.jpg")
                                FileOutputStream(file).use { out ->
                                    it.compress(Bitmap.CompressFormat.JPEG, 90, out)
                                }
                                onWallpaperChange("file://${file.absolutePath}")
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }

                    val filePickerLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.GetContent()
                    ) { uri ->
                        uri?.let {
                            try {
                                val inputStream = context.contentResolver.openInputStream(it)
                                val file = File(context.filesDir, "custom_wallpaper_file_${System.currentTimeMillis()}.jpg")
                                inputStream?.use { input ->
                                    file.outputStream().use { output ->
                                        input.copyTo(output)
                                    }
                                }
                                onWallpaperChange("file://${file.absolutePath}")
                            } catch (e: Exception) {
                                onWallpaperChange(it.toString())
                            }
                        }
                    }

                    val isCustom = wallpaperId.startsWith("http://") ||
                            wallpaperId.startsWith("https://") ||
                            wallpaperId.startsWith("content://") ||
                            wallpaperId.startsWith("file://")

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

                    Text("Preset Wallpaper", color = textColor.copy(alpha = 0.7f), fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        WallpaperCard("win11_bloom", R.drawable.img_wallpaper_win11_bloom_1785365304683, "Windows 11 Bloom", !isCustom && (wallpaperId == "win11_bloom" || wallpaperId == "default_sequoia")) {
                            onWallpaperChange("win11_bloom")
                        }
                        WallpaperCard("dark_ventura", R.drawable.img_wallpaper_dark_ventura_1785360037431, "Dark Ventura", !isCustom && wallpaperId == "dark_ventura") {
                            onWallpaperChange("dark_ventura")
                        }
                        WallpaperCard("macos_default", R.drawable.img_wallpaper_macos_default_1785360027008, "Classic Default", !isCustom && wallpaperId == "macos_default") {
                            onWallpaperChange("macos_default")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = if (isDarkMode) Color(0x33FFFFFF) else Color(0x1A000000))
                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Ganti Wallpaper Kustom", color = textColor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(10.dp))

                    // Buttons for Camera and File Picker
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedButton(
                            onClick = { cameraLauncher.launch(null) },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = "Camera", modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Ambil Foto (Kamera)", fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = { filePickerLauncher.launch("image/*") },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.FolderOpen, contentDescription = "File Picker", modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Pilih File Gambar", fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // URL Input field
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = customUrlText,
                            onValueChange = { customUrlText = it },
                            placeholder = { Text("Masukkan URL gambar (https://...)", fontSize = 12.sp) },
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MacOSBlue,
                                unfocusedBorderColor = if (isDarkMode) Color(0x44FFFFFF) else Color(0x33000000)
                            ),
                            modifier = Modifier.weight(1f)
                        )

                        Button(
                            onClick = {
                                if (customUrlText.isNotBlank()) {
                                    onWallpaperChange(customUrlText.trim())
                                }
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MacOSBlue)
                        ) {
                            Icon(Icons.Default.AddLink, contentDescription = "Apply URL", modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Gunakan URL", fontSize = 12.sp)
                        }
                    }

                    if (isCustom) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Wallpaper Kustom Saat Ini (Aktif)", color = textColor, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(8.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .border(2.dp, MacOSBlue, RoundedCornerShape(10.dp))
                        ) {
                            AsyncImage(
                                model = wallpaperId,
                                contentDescription = "Custom Active Wallpaper",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = { onWallpaperChange("win11_bloom") },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Reset ke Wallpaper Bawaan", fontSize = 12.sp, color = Color.Red)
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
