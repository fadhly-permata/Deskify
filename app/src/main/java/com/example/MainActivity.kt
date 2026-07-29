package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.data.db.LauncherSettingsEntity
import com.example.model.WindowAppType
import com.example.ui.apps.AboutMacDialog
import com.example.ui.apps.CalculatorApp
import com.example.ui.apps.FinderApp
import com.example.ui.apps.NativeAppView
import com.example.ui.apps.NotesApp
import com.example.ui.apps.SafariApp
import com.example.ui.apps.SettingsApp
import com.example.ui.apps.TerminalApp
import com.example.ui.components.AppleMenuPopup
import com.example.ui.components.ControlCenterPopup
import com.example.ui.components.DesktopArea
import com.example.ui.components.FreeformWindowFrame
import com.example.ui.components.LaunchpadOverlay
import com.example.ui.components.MacOSDock
import com.example.ui.components.TopMenuBar
import com.example.ui.theme.MacOSLauncherTheme
import com.example.viewmodel.LauncherViewModel
import com.example.windowing.FreeformWindowLauncher

class MainActivity : ComponentActivity() {

    private val viewModel: LauncherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())

        setContent {
            val settingsState by viewModel.launcherSettings.collectAsState()
            val currentSettings = settingsState ?: LauncherSettingsEntity()

            MacOSLauncherTheme(darkTheme = currentSettings.isDarkMode) {
                LauncherMainScreen(
                    viewModel = viewModel,
                    settings = currentSettings
                )
            }
        }
    }
}

@Composable
fun LauncherMainScreen(
    viewModel: LauncherViewModel,
    settings: LauncherSettingsEntity
) {
    val context = LocalContext.current

    val openWindows by viewModel.openWindows.collectAsState()
    val activeWindowId by viewModel.activeFocusedWindowId.collectAsState()
    val installedApps by viewModel.installedApps.collectAsState()
    val dockItems by viewModel.dockItems.collectAsState()
    val desktopIcons by viewModel.desktopIcons.collectAsState()
    val notes by viewModel.notes.collectAsState()

    val isLaunchpadOpen by viewModel.isLaunchpadOpen.collectAsState()
    val isControlCenterOpen by viewModel.isControlCenterOpen.collectAsState()
    val isAppleMenuOpen by viewModel.isAppleMenuOpen.collectAsState()

    val activeWindow = openWindows.find { it.id == activeWindowId }
    val activeAppName = activeWindow?.title ?: "Finder"

    val activeAppTypes = remember(openWindows) {
        openWindows.map { it.appType }.toSet()
    }
    val activePackageNames = remember(openWindows) {
        openWindows.mapNotNull { it.packageName }.toSet()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                viewModel.setAppleMenuOpen(false)
                viewModel.setControlCenterOpen(false)
            }
    ) {
        // 1. Desktop Backdrop Area (Wallpaper & Icons)
        DesktopArea(
            wallpaperId = settings.wallpaperId,
            desktopIcons = desktopIcons,
            onDesktopIconClick = { icon ->
                if (icon.appType != null) {
                    viewModel.openWindow(icon.appType, icon.label)
                }
            },
            onNewNoteClick = {
                viewModel.openWindow(WindowAppType.NOTES, "Notes")
            },
            onOpenTerminalClick = {
                viewModel.openWindow(WindowAppType.TERMINAL, "Terminal")
            },
            onLaunchpadClick = {
                viewModel.setLaunchpadOpen(true)
            },
            onChangeWallpaperClick = {
                viewModel.openWindow(WindowAppType.SETTINGS, "System Settings")
            }
        )

        // 2. Interactive Freeform Windowing Layer
        val sortedWindows = remember(openWindows) {
            openWindows.sortedBy { it.zIndex }
        }

        sortedWindows.forEach { winState ->
            FreeformWindowFrame(
                windowState = winState,
                isDarkMode = settings.isDarkMode,
                onClose = { viewModel.closeWindow(winState.id) },
                onMinimize = { viewModel.minimizeWindow(winState.id) },
                onMaximize = { viewModel.maximizeWindow(winState.id) },
                onFocus = { viewModel.focusWindow(winState.id) },
                onMove = { dx, dy -> viewModel.moveWindow(winState.id, dx, dy) },
                onResize = { dw, dh -> viewModel.resizeWindow(winState.id, dw, dh) }
            ) {
                when (winState.appType) {
                    WindowAppType.FINDER -> FinderApp(
                        isDarkMode = settings.isDarkMode,
                        onOpenFile = { file ->
                            if (file.name == "Safari.app") viewModel.openWindow(WindowAppType.SAFARI, "Safari")
                            if (file.name == "Terminal.app") viewModel.openWindow(WindowAppType.TERMINAL, "Terminal")
                            if (file.name == "Notes.app") viewModel.openWindow(WindowAppType.NOTES, "Notes")
                            if (file.name == "Calculator.app") viewModel.openWindow(WindowAppType.CALCULATOR, "Calculator")
                            if (file.name == "System Settings.app") viewModel.openWindow(WindowAppType.SETTINGS, "System Settings")
                        }
                    )
                    WindowAppType.SAFARI -> SafariApp(isDarkMode = settings.isDarkMode)
                    WindowAppType.TERMINAL -> TerminalApp(installedPackages = installedApps.map { it.packageName })
                    WindowAppType.NOTES -> NotesApp(
                        notes = notes,
                        isDarkMode = settings.isDarkMode,
                        onSaveNote = { viewModel.saveNote(it) },
                        onDeleteNote = { viewModel.deleteNote(it) }
                    )
                    WindowAppType.CALCULATOR -> CalculatorApp()
                    WindowAppType.SETTINGS -> SettingsApp(
                        wallpaperId = settings.wallpaperId,
                        isDarkMode = settings.isDarkMode,
                        dockSizeDp = settings.dockSizeDp,
                        dockMagnification = settings.dockMagnification,
                        onWallpaperChange = { viewModel.updateWallpaper(it) },
                        onDarkModeToggle = { viewModel.toggleDarkMode(it) },
                        onDockSizeChange = { viewModel.updateDockSize(it) },
                        onDockMagnificationToggle = { viewModel.toggleDockMagnification(it) }
                    )
                    WindowAppType.MESSAGES, WindowAppType.PHOTOS, WindowAppType.MUSIC -> AboutMacDialog(isDarkMode = settings.isDarkMode)
                    WindowAppType.LAUNCHPAD -> {
                        viewModel.setLaunchpadOpen(true)
                        viewModel.closeWindow(winState.id)
                    }
                    WindowAppType.NATIVE_APP -> {
                        NativeAppView(
                            title = winState.title,
                            packageName = winState.packageName,
                            appIcon = winState.appIcon,
                            isDarkMode = settings.isDarkMode
                        )
                    }
                }
            }
        }

        // 3. Top Menu Bar (Fixed at top)
        TopMenuBar(
            activeAppName = activeAppName,
            isDarkMode = settings.isDarkMode,
            onAppleClick = { viewModel.toggleAppleMenu() },
            onControlCenterToggle = { viewModel.toggleControlCenter() },
            onSpotlightClick = { viewModel.toggleLaunchpad() },
            onMenuItemClick = { item ->
                when (item) {
                    "New Window" -> viewModel.openWindow(WindowAppType.FINDER, "Finder")
                    "New Note" -> viewModel.openWindow(WindowAppType.NOTES, "Notes")
                    "Help" -> Toast.makeText(context, "macOS Launcher v1.1.0", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.align(Alignment.TopCenter)
        )

        // 4. Apple Logo Menu Popup
        Box(modifier = Modifier.align(Alignment.TopStart).offset(x = 8.dp, y = 32.dp)) {
            AppleMenuPopup(
                isOpen = isAppleMenuOpen,
                isDarkMode = settings.isDarkMode,
                onDismiss = { viewModel.setAppleMenuOpen(false) },
                onAboutMacClick = { viewModel.openWindow(WindowAppType.MESSAGES, "About This Mac") },
                onSettingsClick = { viewModel.openWindow(WindowAppType.SETTINGS, "System Settings") },
                onLaunchpadClick = { viewModel.setLaunchpadOpen(true) },
                onLockClick = { Toast.makeText(context, "Screen Locked", Toast.LENGTH_SHORT).show() },
                onRestartClick = { Toast.makeText(context, "Restarting Launcher...", Toast.LENGTH_SHORT).show() }
            )
        }

        // 5. Control Center Popup
        Box(modifier = Modifier.align(Alignment.TopEnd).offset(x = (-12).dp, y = 32.dp)) {
            ControlCenterPopup(
                isOpen = isControlCenterOpen,
                isDarkMode = settings.isDarkMode,
                onDarkModeToggle = { viewModel.toggleDarkMode(it) },
                onDismiss = { viewModel.setControlCenterOpen(false) }
            )
        }

        // 6. Floating macOS Dock (Fixed at Bottom)
        MacOSDock(
            dockItems = dockItems,
            activeAppTypes = activeAppTypes,
            activePackageNames = activePackageNames,
            isDarkMode = settings.isDarkMode,
            onDockItemClick = { dockItem ->
                if (dockItem.appType != null) {
                    if (dockItem.appType == WindowAppType.LAUNCHPAD) {
                        viewModel.setLaunchpadOpen(true)
                    } else {
                        viewModel.openWindow(dockItem.appType, dockItem.label)
                    }
                } else if (dockItem.packageName != null) {
                    viewModel.openWindow(
                        appType = WindowAppType.NATIVE_APP,
                        title = dockItem.label,
                        packageName = dockItem.packageName
                    )
                }
            },
            onDockItemLongClick = { },
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        // 7. Launchpad Fullscreen Overlay
        LaunchpadOverlay(
            isOpen = isLaunchpadOpen,
            installedApps = installedApps,
            onDismiss = { viewModel.setLaunchpadOpen(false) },
            onAppClick = { app ->
                viewModel.openWindow(
                    appType = WindowAppType.NATIVE_APP,
                    title = app.label,
                    packageName = app.packageName,
                    appIcon = app.icon
                )
            },
            onBuiltInAppClick = { appType ->
                val title = when (appType) {
                    WindowAppType.FINDER -> "Finder"
                    WindowAppType.SAFARI -> "Safari"
                    WindowAppType.TERMINAL -> "Terminal"
                    WindowAppType.NOTES -> "Notes"
                    WindowAppType.CALCULATOR -> "Calculator"
                    WindowAppType.SETTINGS -> "System Settings"
                    WindowAppType.MESSAGES -> "Messages"
                    WindowAppType.PHOTOS -> "Photos"
                    WindowAppType.MUSIC -> "Music"
                    else -> "App"
                }
                viewModel.openWindow(appType, title)
            }
        )
    }
}
