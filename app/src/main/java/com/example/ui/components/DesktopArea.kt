package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.DesktopIconModel
import com.example.model.WindowAppType

@Composable
fun DesktopArea(
    wallpaperId: String,
    desktopIcons: List<DesktopIconModel>,
    onDesktopIconClick: (DesktopIconModel) -> Unit,
    onNewNoteClick: () -> Unit,
    onOpenTerminalClick: () -> Unit,
    onLaunchpadClick: () -> Unit,
    onChangeWallpaperClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var wallpaperContextMenuOpen by remember { mutableStateOf(false) }

    val wallpaperRes = when (wallpaperId) {
        "dark_ventura" -> R.drawable.img_wallpaper_dark_ventura_1785360037431
        else -> R.drawable.img_wallpaper_macos_default_1785360027008
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = { wallpaperContextMenuOpen = true }
                )
            }
            .testTag("desktop_area")
    ) {
        // High-res Wallpaper Background Image
        Image(
            painter = painterResource(id = wallpaperRes),
            contentDescription = "Desktop Wallpaper",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Desktop Context Menu on empty wallpaper tap/long press
        DropdownMenu(
            expanded = wallpaperContextMenuOpen,
            onDismissRequest = { wallpaperContextMenuOpen = false }
        ) {
            DropdownMenuItem(
                text = { Text("New Note") },
                onClick = {
                    wallpaperContextMenuOpen = false
                    onNewNoteClick()
                }
            )
            DropdownMenuItem(
                text = { Text("Open Terminal") },
                onClick = {
                    wallpaperContextMenuOpen = false
                    onOpenTerminalClick()
                }
            )
            DropdownMenuItem(
                text = { Text("Launchpad") },
                onClick = {
                    wallpaperContextMenuOpen = false
                    onLaunchpadClick()
                }
            )
            DropdownMenuItem(
                text = { Text("Change Desktop Background...") },
                onClick = {
                    wallpaperContextMenuOpen = false
                    onChangeWallpaperClick()
                }
            )
        }

        // Desktop Shortcuts List on Right Side (macOS default layout)
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 16.dp, end = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            desktopIcons.forEach { iconModel ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onDesktopIconClick(iconModel) }
                        .padding(8.dp)
                        .testTag("desktop_icon_${iconModel.id}")
                ) {
                    val (vector, bgGradient) = getBuiltInIconData(iconModel.appType)
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(bgGradient),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = vector,
                            contentDescription = iconModel.label,
                            tint = Color.White,
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = iconModel.label,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.35f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}
