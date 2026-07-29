package com.example.ui.components

import android.graphics.drawable.Drawable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import com.example.model.DockItemModel
import com.example.model.WindowAppType
import com.example.ui.theme.MacOSDarkGlass
import com.example.ui.theme.MacOSLightGlass

@Composable
fun MacOSDock(
    dockItems: List<DockItemModel>,
    activeAppTypes: Set<WindowAppType>,
    activePackageNames: Set<String>,
    isDarkMode: Boolean,
    onDockItemClick: (DockItemModel) -> Unit,
    onDockItemLongClick: (DockItemModel) -> Unit,
    modifier: Modifier = Modifier
) {
    val glassBg = if (isDarkMode) MacOSDarkGlass else MacOSLightGlass
    val borderClr = if (isDarkMode) Color(0x33FFFFFF) else Color(0x33000000)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(bottom = 10.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            modifier = Modifier
                .shadow(24.dp, RoundedCornerShape(20.dp))
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, borderClr, RoundedCornerShape(20.dp))
                .background(glassBg)
                .testTag("macos_dock"),
            color = glassBg,
            tonalElevation = 16.dp
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                dockItems.forEach { item ->
                    val isRunning = (item.appType != null && activeAppTypes.contains(item.appType)) ||
                            (item.packageName != null && activePackageNames.contains(item.packageName))

                    DockIconView(
                        item = item,
                        isRunning = isRunning,
                        onClick = { onDockItemClick(item) },
                        onLongClick = { onDockItemLongClick(item) }
                    )
                }
            }
        }
    }
}

@Composable
private fun DockIconView(
    item: DockItemModel,
    isRunning: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    var contextMenuOpen by remember { mutableStateOf(false) }

    val animatedScale by animateFloatAsState(
        targetValue = if (isPressed) 1.25f else 1.0f,
        animationSpec = spring(stiffness = 400f),
        label = "dock_scale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier.padding(bottom = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .scale(animatedScale)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            isPressed = true
                            tryAwaitRelease()
                            isPressed = false
                        },
                        onTap = { onClick() },
                        onLongPress = {
                            contextMenuOpen = true
                            onLongClick()
                        }
                    )
                }
                .testTag("dock_icon_${item.id}"),
            contentAlignment = Alignment.Center
        ) {
            // Render built-in icon or custom Android App Drawable
            if (item.customIcon != null) {
                val bitmap = item.customIcon.toBitmap(128, 128)
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = item.label,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                )
            } else {
                val (vector, bgGradient) = getBuiltInIconData(item.appType)
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(bgGradient),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = vector,
                        contentDescription = item.label,
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            DropdownMenu(
                expanded = contextMenuOpen,
                onDismissRequest = { contextMenuOpen = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Open ${item.label}") },
                    onClick = {
                        contextMenuOpen = false
                        onClick()
                    }
                )
                DropdownMenuItem(
                    text = { Text(if (item.isPinned) "Remove from Dock" else "Keep in Dock") },
                    onClick = {
                        contextMenuOpen = false
                    }
                )
                if (isRunning) {
                    DropdownMenuItem(
                        text = { Text("Quit") },
                        onClick = {
                            contextMenuOpen = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(3.dp))

        // macOS Active App Indicator Dot below icon
        Box(
            modifier = Modifier
                .size(4.dp)
                .clip(CircleShape)
                .background(if (isRunning) Color.White.copy(alpha = 0.9f) else Color.Transparent)
        )
    }
}

fun getBuiltInIconData(appType: WindowAppType?): Pair<ImageVector, Color> {
    return when (appType) {
        WindowAppType.FINDER -> Pair(Icons.Default.Folder, Color(0xFF1E88E5))
        WindowAppType.SAFARI -> Pair(Icons.Default.Language, Color(0xFF00B0FF))
        WindowAppType.TERMINAL -> Pair(Icons.Default.Terminal, Color(0xFF263238))
        WindowAppType.NOTES -> Pair(Icons.Default.Description, Color(0xFFFFB300))
        WindowAppType.CALCULATOR -> Pair(Icons.Default.Calculate, Color(0xFFFF9800))
        WindowAppType.SETTINGS -> Pair(Icons.Default.Settings, Color(0xFF78909C))
        WindowAppType.MESSAGES -> Pair(Icons.Default.Chat, Color(0xFF4CAF50))
        WindowAppType.PHOTOS -> Pair(Icons.Default.PhotoLibrary, Color(0xFFE91E63))
        WindowAppType.MUSIC -> Pair(Icons.Default.MusicNote, Color(0xFFFF2D55))
        WindowAppType.LAUNCHPAD -> Pair(Icons.Default.Apps, Color(0xFF7C4DFF))
        else -> Pair(Icons.Default.Apps, Color(0xFF9E9E9E))
    }
}
