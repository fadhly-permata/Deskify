package com.example.ui.components

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import com.example.model.InstalledApp
import com.example.model.WindowAppType
import com.example.ui.theme.Win11Blue
import com.example.ui.theme.Win11DarkFlyout
import com.example.ui.theme.Win11LightFlyout

@Composable
fun WindowsStartMenu(
    isDarkMode: Boolean,
    installedApps: List<InstalledApp>,
    onDismiss: () -> Unit,
    onAppClick: (InstalledApp) -> Unit,
    onBuiltInAppClick: (WindowAppType) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var showAllApps by remember { mutableStateOf(false) }
    var showPowerMenu by remember { mutableStateOf(false) }

    val bg = if (isDarkMode) Win11DarkFlyout else Win11LightFlyout
    val textColor = if (isDarkMode) Color.White else Color(0xFF1C1C1C)
    val cardBg = if (isDarkMode) Color(0xFF2B2B2B) else Color.White
    val borderColor = if (isDarkMode) Color(0x33FFFFFF) else Color(0x1A000000)

    val builtInApps = remember {
        listOf(
            Triple(WindowAppType.FINDER, "File Explorer", Icons.Default.Folder),
            Triple(WindowAppType.SAFARI, "Microsoft Edge", Icons.Default.Language),
            Triple(WindowAppType.NOTES, "Notepad", Icons.Default.EditNote),
            Triple(WindowAppType.TERMINAL, "Terminal", Icons.Default.Code),
            Triple(WindowAppType.SETTINGS, "Settings", Icons.Default.Settings)
        )
    }

    val filteredInstalledApps = remember(searchQuery, installedApps) {
        if (searchQuery.isBlank()) installedApps else installedApps.filter { it.label.contains(searchQuery, ignoreCase = true) }
    }

    Surface(
        modifier = modifier
            .width(520.dp)
            .height(580.dp)
            .shadow(24.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .testTag("windows_start_menu"),
        color = bg,
        tonalElevation = 16.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Search Input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text("Type here to search", color = textColor.copy(alpha = 0.5f), fontSize = 13.sp)
                },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = Win11Blue)
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = cardBg,
                    unfocusedContainerColor = cardBg,
                    focusedBorderColor = Win11Blue,
                    unfocusedBorderColor = borderColor,
                    focusedTextColor = textColor,
                    unfocusedTextColor = textColor
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
                    .testTag("start_menu_search_input")
            )

            // Content Section (Pinned Grid vs All Apps)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 12.dp)
            ) {
                if (searchQuery.isNotBlank() || showAllApps) {
                    // All Apps List / Search Results
                    Column(modifier = Modifier.fillMaxSize()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (searchQuery.isNotBlank()) "Search Results" else "All Apps",
                                color = textColor,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (showAllApps && searchQuery.isBlank()) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(cardBg)
                                        .clickable { showAllApps = false }
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text("< Back to Pinned", color = Win11Blue, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(filteredInstalledApps) { app ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable {
                                            onAppClick(app)
                                            onDismiss()
                                        }
                                        .padding(horizontal = 10.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (app.icon != null) {
                                        val bitmap = remember(app.icon) { app.icon.toBitmap(64, 64) }
                                        Image(
                                            bitmap = bitmap.asImageBitmap(),
                                            contentDescription = app.label,
                                            modifier = Modifier
                                                .size(28.dp)
                                                .clip(RoundedCornerShape(6.dp))
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.Folder,
                                            contentDescription = app.label,
                                            tint = Win11Blue,
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Text(
                                        text = app.label,
                                        color = textColor,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                } else {
                    // Pinned Apps Section
                    Column(modifier = Modifier.fillMaxSize()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Pinned",
                                color = textColor,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(cardBg)
                                    .clickable { showAllApps = true }
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("All apps", color = textColor, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = "All apps", tint = textColor, modifier = Modifier.size(14.dp))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Pinned Grid (Built-in + Installed)
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(6),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            // Built in apps
                            items(builtInApps) { (appType, name, iconVec) ->
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable {
                                            onBuiltInAppClick(appType)
                                            onDismiss()
                                        }
                                        .padding(vertical = 6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(cardBg),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = iconVec,
                                            contentDescription = name,
                                            tint = Win11Blue,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = name,
                                        color = textColor,
                                        fontSize = 11.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }

                            // Installed apps preview
                            items(installedApps.take(12)) { app ->
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable {
                                            onAppClick(app)
                                            onDismiss()
                                        }
                                        .padding(vertical = 6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(cardBg),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (app.icon != null) {
                                            val bitmap = remember(app.icon) { app.icon.toBitmap(64, 64) }
                                            Image(
                                                bitmap = bitmap.asImageBitmap(),
                                                contentDescription = app.label,
                                                modifier = Modifier.size(26.dp)
                                            )
                                        } else {
                                            Icon(
                                                imageVector = Icons.Default.Folder,
                                                contentDescription = app.label,
                                                tint = Win11Blue,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = app.label,
                                        color = textColor,
                                        fontSize = 11.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }

            HorizontalDivider(color = borderColor)

            // Bottom Profile & Power Footer
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // User Avatar & Name
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Win11Blue),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("U", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("User", color = textColor, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }

                // Power Options
                Box {
                    IconButton(onClick = { showPowerMenu = true }) {
                        Icon(
                            imageVector = Icons.Default.PowerSettingsNew,
                            contentDescription = "Power",
                            tint = textColor
                        )
                    }

                    DropdownMenu(
                        expanded = showPowerMenu,
                        onDismissRequest = { showPowerMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Sleep") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                            onClick = { showPowerMenu = false; onDismiss() }
                        )
                        DropdownMenuItem(
                            text = { Text("Shut down") },
                            leadingIcon = { Icon(Icons.Default.PowerSettingsNew, contentDescription = null) },
                            onClick = { showPowerMenu = false; onDismiss() }
                        )
                        DropdownMenuItem(
                            text = { Text("Restart") },
                            leadingIcon = { Icon(Icons.Default.Refresh, contentDescription = null) },
                            onClick = { showPowerMenu = false; onDismiss() }
                        )
                    }
                }
            }
        }
    }
}
