package com.example.ui.apps

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MacOSBlue

data class FileItem(
    val name: String,
    val isDirectory: Boolean,
    val sizeStr: String,
    val modifiedDate: String,
    val icon: ImageVector
)

@Composable
fun FinderApp(
    isDarkMode: Boolean,
    onOpenFile: (FileItem) -> Unit = {}
) {
    var selectedSection by remember { mutableStateOf("Applications") }
    var isGridView by remember { mutableStateOf(true) }
    var searchQuery by remember { mutableStateOf("") }

    val textColor = if (isDarkMode) Color.White else Color(0xFF1D1D1F)
    val sidebarBg = if (isDarkMode) Color(0xFF252525) else Color(0xFFEBEBEB)
    val contentBg = if (isDarkMode) Color(0xFF1E1E1E) else Color.White

    val sidebarItems = remember {
        listOf(
            "Recents" to Icons.Default.Schedule,
            "Applications" to Icons.Default.Home,
            "Documents" to Icons.Default.Description,
            "Downloads" to Icons.Default.Download,
            "Pictures" to Icons.Default.Image,
            "Desktop" to Icons.Default.Folder
        )
    }

    val mockFiles = remember(selectedSection) {
        when (selectedSection) {
            "Applications" -> listOf(
                FileItem("Safari.app", false, "124 MB", "Today, 2:15 PM", Icons.Default.Folder),
                FileItem("Terminal.app", false, "45 MB", "Yesterday, 10:30 AM", Icons.Default.Folder),
                FileItem("Notes.app", false, "32 MB", "Jul 28, 2026", Icons.Default.Folder),
                FileItem("Calculator.app", false, "12 MB", "Jul 25, 2026", Icons.Default.Folder),
                FileItem("System Settings.app", false, "88 MB", "Jul 20, 2026", Icons.Default.Folder)
            )
            "Documents" -> listOf(
                FileItem("Project_PRD_macOS.pdf", false, "2.4 MB", "Today, 1:45 PM", Icons.Default.Description),
                FileItem("Architecture_Diagram.png", false, "4.1 MB", "Jul 27, 2026", Icons.Default.Image),
                FileItem("Meeting_Notes.txt", false, "15 KB", "Jul 26, 2026", Icons.Default.Description)
            )
            "Downloads" -> listOf(
                FileItem("macOS_Sequoia_Installer.dmg", false, "12.8 GB", "Jul 24, 2026", Icons.Default.Download),
                FileItem("wallpaper_4k.jpg", false, "8.2 MB", "Jul 22, 2026", Icons.Default.Image)
            )
            else -> listOf(
                FileItem("Sample_Folder", true, "--", "Jul 28, 2026", Icons.Default.Folder),
                FileItem("Note_Draft.txt", false, "4 KB", "Jul 29, 2026", Icons.Default.Description)
            )
        }
    }

    val filteredFiles = mockFiles.filter {
        it.name.contains(searchQuery, ignoreCase = true)
    }

    Column(modifier = Modifier.fillMaxSize().background(contentBg)) {
        // Finder Toolbar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .background(sidebarBg)
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Navigation Buttons
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {}, modifier = Modifier.size(28.dp)) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = textColor.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )
                }
                IconButton(onClick = {}, modifier = Modifier.size(28.dp)) {
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Forward",
                        tint = textColor.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = selectedSection,
                    color = textColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // View Mode & Search
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { isGridView = true },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.GridView,
                        contentDescription = "Grid View",
                        tint = if (isGridView) MacOSBlue else textColor.copy(alpha = 0.6f),
                        modifier = Modifier.size(18.dp)
                    )
                }
                IconButton(
                    onClick = { isGridView = false },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.List,
                        contentDescription = "List View",
                        tint = if (!isGridView) MacOSBlue else textColor.copy(alpha = 0.6f),
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search", fontSize = 11.sp, color = textColor.copy(alpha = 0.5f)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = textColor.copy(alpha = 0.6f),
                            modifier = Modifier.size(14.dp)
                        )
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedBorderColor = MacOSBlue,
                        unfocusedBorderColor = textColor.copy(alpha = 0.2f),
                        focusedTextColor = textColor,
                        unfocusedTextColor = textColor
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.width(160.dp).height(32.dp).testTag("finder_search_input")
                )
            }
        }

        HorizontalDivider(color = textColor.copy(alpha = 0.1f))

        // Main Body: Sidebar + File List
        Row(modifier = Modifier.fillMaxSize()) {
            // Sidebar
            Column(
                modifier = Modifier
                    .width(180.dp)
                    .fillMaxHeight()
                    .background(sidebarBg)
                    .padding(8.dp)
            ) {
                Text(
                    text = "Favorites",
                    color = textColor.copy(alpha = 0.5f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp, top = 8.dp, bottom = 6.dp)
                )

                sidebarItems.forEach { (label, icon) ->
                    val isSelected = selectedSection == label
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSelected) MacOSBlue else Color.Transparent)
                            .clickable { selectedSection = label }
                            .padding(horizontal = 8.dp, vertical = 6.dp),
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
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                }
            }

            // File Content Grid or List
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(12.dp)
            ) {
                if (isGridView) {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 100.dp),
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(filteredFiles) { file ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { onOpenFile(file) }
                                    .padding(8.dp)
                            ) {
                                Icon(
                                    imageVector = file.icon,
                                    contentDescription = file.name,
                                    tint = MacOSBlue,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = file.name,
                                    color = textColor,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 2
                                )
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(filteredFiles) { file ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .clickable { onOpenFile(file) }
                                    .padding(horizontal = 8.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = file.icon,
                                        contentDescription = file.name,
                                        tint = MacOSBlue,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = file.name,
                                        color = textColor,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                Row {
                                    Text(
                                        text = file.sizeStr,
                                        color = textColor.copy(alpha = 0.6f),
                                        fontSize = 12.sp
                                    )
                                    Spacer(modifier = Modifier.width(20.dp))
                                    Text(
                                        text = file.modifiedDate,
                                        color = textColor.copy(alpha = 0.6f),
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
