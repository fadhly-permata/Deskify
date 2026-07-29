package com.example.model

import android.graphics.drawable.Drawable

enum class WindowAppType {
    FINDER,
    SAFARI,
    TERMINAL,
    NOTES,
    CALCULATOR,
    SETTINGS,
    MESSAGES,
    PHOTOS,
    MUSIC,
    LAUNCHPAD,
    NATIVE_APP
}

data class WindowState(
    val id: String,
    val appType: WindowAppType,
    val title: String,
    val xDp: Float = 120f,
    val yDp: Float = 80f,
    val widthDp: Float = 640f,
    val heightDp: Float = 440f,
    val isMinimized: Boolean = false,
    val isMaximized: Boolean = false,
    val isFocused: Boolean = true,
    val packageName: String? = null,
    val appIcon: Drawable? = null,
    val extraData: String? = null,
    val zIndex: Int = 0
)

data class InstalledApp(
    val packageName: String,
    val label: String,
    val icon: Drawable?,
    val isSystemApp: Boolean = false,
    val categoryName: String = "Utilities"
)

data class DockItemModel(
    val id: String,
    val label: String,
    val packageName: String? = null,
    val appType: WindowAppType? = null,
    val iconRes: Int = 0,
    val customIcon: Drawable? = null,
    val isRunning: Boolean = false,
    val isPinned: Boolean = true
)

data class DesktopIconModel(
    val id: String,
    val label: String,
    val packageName: String? = null,
    val appType: WindowAppType? = null,
    val iconRes: Int = 0,
    val customIcon: Drawable? = null,
    val xRatio: Float = 0.88f,
    val yRatio: Float = 0.12f
)
