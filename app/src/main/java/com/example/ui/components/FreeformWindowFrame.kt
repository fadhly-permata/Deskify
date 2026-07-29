package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CropSquare
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import com.example.ui.theme.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import com.example.model.WindowState
import com.example.ui.theme.MacOSDarkTitleBar
import com.example.ui.theme.MacOSDarkWindowBg
import com.example.ui.theme.MacOSLightTitleBar
import com.example.ui.theme.MacOSLightWindowBg
import com.example.ui.theme.TrafficGreen
import com.example.ui.theme.TrafficRed
import com.example.ui.theme.TrafficYellow
import kotlin.math.roundToInt

@Composable
fun FreeformWindowFrame(
    windowState: WindowState,
    isDarkMode: Boolean,
    onClose: () -> Unit,
    onMinimize: () -> Unit,
    onMaximize: () -> Unit,
    onFocus: () -> Unit,
    onMove: (dx: Float, dy: Float) -> Unit,
    onResize: (dw: Float, dh: Float) -> Unit,
    content: @Composable () -> Unit
) {
    if (windowState.isMinimized) return

    val titleBarBg = if (isDarkMode) Win11DarkTitleBar else Win11LightTitleBar
    val windowBg = if (isDarkMode) Win11DarkWindowBg else Win11LightWindowBg
    val textColor = if (isDarkMode) Color.White else Color(0xFF1C1C1C)
    val borderColor = if (windowState.isFocused) {
        if (isDarkMode) Color(0x66FFFFFF) else Color(0x33000000)
    } else {
        if (isDarkMode) Color(0x22FFFFFF) else Color(0x15000000)
    }

    val elevation = if (windowState.isFocused) 16.dp else 6.dp
    val density = LocalDensity.current

    val modifier = if (windowState.isMaximized) {
        Modifier
            .fillMaxSize()
            .testTag("window_frame_maximized_${windowState.id}")
    } else {
        Modifier
            .offset { IntOffset(windowState.xDp.dp.roundToPx(), windowState.yDp.dp.roundToPx()) }
            .size(windowState.widthDp.dp, windowState.heightDp.dp)
            .testTag("window_frame_${windowState.id}")
    }

    Surface(
        modifier = modifier
            .shadow(elevation, RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, borderColor, RoundedCornerShape(10.dp))
            .pointerInput(Unit) {
                detectDragGestures { _, _ -> onFocus() }
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onFocus() },
        color = windowBg,
        tonalElevation = elevation
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Windows 11 Title Bar (Draggable)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp)
                        .background(titleBarBg)
                        .pointerInput(density) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                val dxDp = with(density) { dragAmount.x.toDp().value }
                                val dyDp = with(density) { dragAmount.y.toDp().value }
                                onMove(dxDp, dyDp)
                            }
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left: App Icon & Title
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (windowState.appIcon != null) {
                            val bitmap = windowState.appIcon.toBitmap(48, 48)
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = windowState.title,
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(RoundedCornerShape(3.dp))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }

                        Text(
                            text = windowState.title,
                            color = textColor.copy(alpha = if (windowState.isFocused) 1f else 0.7f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Right: Windows 11 Window Controls (Minimize, Maximize, Close)
                    Row(
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Minimize
                        Box(
                            modifier = Modifier
                                .width(44.dp)
                                .height(36.dp)
                                .clickable { onMinimize() }
                                .testTag("window_control_minimize_${windowState.id}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Remove,
                                contentDescription = "Minimize",
                                tint = textColor,
                                modifier = Modifier.size(14.dp)
                            )
                        }

                        // Maximize / Restore
                        Box(
                            modifier = Modifier
                                .width(44.dp)
                                .height(36.dp)
                                .clickable { onMaximize() }
                                .testTag("window_control_maximize_${windowState.id}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CropSquare,
                                contentDescription = "Maximize",
                                tint = textColor,
                                modifier = Modifier.size(13.dp)
                            )
                        }

                        // Close (Windows Red Hover Style)
                        Box(
                            modifier = Modifier
                                .width(46.dp)
                                .height(36.dp)
                                .background(Color.Transparent)
                                .clickable { onClose() }
                                .testTag("window_control_close_${windowState.id}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = textColor,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }

                // Window Body Content Container
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(windowBg)
                ) {
                    content()
                }
            }

            // Bottom-Right Corner Resize Handle
            if (!windowState.isMaximized) {
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .align(Alignment.BottomEnd)
                        .pointerInput(density) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                val dwDp = with(density) { dragAmount.x.toDp().value }
                                val dhDp = with(density) { dragAmount.y.toDp().value }
                                onResize(dwDp, dhDp)
                            }
                        }
                        .testTag("window_resize_handle_${windowState.id}")
                )
            }
        }
    }
}
