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

    val titleBarBg = if (isDarkMode) MacOSDarkTitleBar else MacOSLightTitleBar
    val windowBg = if (isDarkMode) MacOSDarkWindowBg else MacOSLightWindowBg
    val textColor = if (isDarkMode) Color.White else Color(0xFF1D1D1F)
    val borderColor = if (windowState.isFocused) {
        if (isDarkMode) Color(0x66FFFFFF) else Color(0x44000000)
    } else {
        if (isDarkMode) Color(0x22FFFFFF) else Color(0x22000000)
    }

    val elevation = if (windowState.isFocused) 24.dp else 8.dp
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
            .shadow(elevation, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
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
                // macOS Title Bar (Draggable)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(34.dp)
                        .background(titleBarBg)
                        .pointerInput(density) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                val dxDp = with(density) { dragAmount.x.toDp().value }
                                val dyDp = with(density) { dragAmount.y.toDp().value }
                                onMove(dxDp, dyDp)
                            }
                        }
                        .padding(horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Traffic Light Buttons (🔴 🟡 🟢)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 🔴 Close
                        Box(
                            modifier = Modifier
                                .size(13.dp)
                                .clip(CircleShape)
                                .background(TrafficRed)
                                .clickable { onClose() }
                                .testTag("traffic_light_close_${windowState.id}"),
                            contentAlignment = Alignment.Center
                        ) {}

                        // 🟡 Minimize
                        Box(
                            modifier = Modifier
                                .size(13.dp)
                                .clip(CircleShape)
                                .background(TrafficYellow)
                                .clickable { onMinimize() }
                                .testTag("traffic_light_minimize_${windowState.id}"),
                            contentAlignment = Alignment.Center
                        ) {}

                        // 🟢 Maximize / Zoom
                        Box(
                            modifier = Modifier
                                .size(13.dp)
                                .clip(CircleShape)
                                .background(TrafficGreen)
                                .clickable { onMaximize() }
                                .testTag("traffic_light_maximize_${windowState.id}"),
                            contentAlignment = Alignment.Center
                        ) {}
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // App Title & Optional Icon
                    Row(
                        modifier = Modifier.weight(1f),
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
                            Spacer(modifier = Modifier.width(6.dp))
                        }

                        Text(
                            text = windowState.title,
                            color = textColor.copy(alpha = if (windowState.isFocused) 1f else 0.6f),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
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
