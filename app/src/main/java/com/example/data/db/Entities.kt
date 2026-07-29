package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val colorHex: String = "#FFF8E1",
    val isPinned: Boolean = false
)

@Entity(tableName = "dock_items")
data class DockItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val packageName: String?,
    val appName: String,
    val windowAppType: String?, // Enum name for built-in apps or null for native
    val iconRes: Int = 0,
    val isPinned: Boolean = true,
    val position: Int = 0
)

@Entity(tableName = "desktop_icons")
data class DesktopIconEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val packageName: String?,
    val label: String,
    val windowAppType: String?,
    val xRatio: Float = 0.85f, // Position on desktop relative width
    val yRatio: Float = 0.1f   // Position on desktop relative height
)

@Entity(tableName = "launcher_settings")
data class LauncherSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val wallpaperId: String = "default_sequoia",
    val isDarkMode: Boolean = true,
    val dockMagnification: Boolean = true,
    val dockSizeDp: Int = 64,
    val enableNativeFreeform: Boolean = true
)
