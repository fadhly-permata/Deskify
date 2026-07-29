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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.data.db.LauncherSettingsEntity
import com.example.model.WindowAppType
import com.example.ui.apps.AboutMacDialog
import com.example.ui.apps.FinderApp
import com.example.ui.apps.NativeAppView
import com.example.ui.apps.NotesApp
import com.example.ui.apps.SafariApp
import com.example.ui.apps.SettingsApp
import com.example.ui.apps.TerminalApp
import com.example.ui.components.DesktopArea
import com.example.ui.components.FreeformWindowFrame
import com.example.ui.components.WindowsCalendarFlyout
import com.example.ui.components.WindowsQuickSettings
import com.example.ui.components.WindowsStartMenu
import com.example.ui.components.WindowsTaskbar
import com.example.ui.theme.MacOSLauncherTheme
import com.example.viewmodel.LauncherViewModel

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

    var isStartMenuOpen by remember { mutableStateOf(false) }
    var isQuickSettingsOpen by remember { mutableStateOf(false) }
    var isCalendarOpen by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                isStartMenuOpen = false
                isQuickSettingsOpen = false
                isCalendarOpen = false
            }
    ) {
        // 1. Desktop Area (Wallpaper & Shortcuts)
        DesktopArea(
            wallpaperId = settings.wallpaperId,
            desktopIcons = desktopIcons,
            onDesktopIconClick = { icon ->
                if (icon.appType != null) {
                    val title = when (icon.appType) {
                        WindowAppType.FINDER -> "File Explorer"
                        WindowAppType.SAFARI -> "Microsoft Edge"
                        WindowAppType.NOTES -> "Notepad"
                        WindowAppType.TERMINAL -> "Terminal"
                        WindowAppType.CALCULATOR -> "Calculator"
                        WindowAppType.SETTINGS -> "Settings"
                        else -> icon.label
                    }
                    viewModel.openWindow(icon.appType, title)
                }
            },
            onNewNoteClick = {
                viewModel.openWindow(WindowAppType.NOTES, "Notepad")
            },
            onOpenTerminalClick = {
                viewModel.openWindow(WindowAppType.TERMINAL, "Terminal")
            },
            onLaunchpadClick = {
                isStartMenuOpen = true
            },
            onChangeWallpaperClick = {
                viewModel.openWindow(WindowAppType.SETTINGS, "Settings")
            }
        )

        // 2. Interactive Freeform Window Layer
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
                            if (file.name == "Edge.exe") viewModel.openWindow(WindowAppType.SAFARI, "Microsoft Edge")
                            if (file.name == "Notepad.exe") viewModel.openWindow(WindowAppType.NOTES, "Notepad")
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
                    WindowAppType.CALCULATOR, WindowAppType.MESSAGES, WindowAppType.PHOTOS, WindowAppType.MUSIC -> AboutMacDialog(isDarkMode = settings.isDarkMode)
                    WindowAppType.LAUNCHPAD -> {
                        isStartMenuOpen = true
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

        // 3. Windows 11 Start Menu Flyout
        if (isStartMenuOpen) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(bottom = 56.dp, start = 8.dp)
            ) {
                WindowsStartMenu(
                    isDarkMode = settings.isDarkMode,
                    installedApps = installedApps,
                    onDismiss = { isStartMenuOpen = false },
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
                            WindowAppType.FINDER -> "File Explorer"
                            WindowAppType.SAFARI -> "Microsoft Edge"
                            WindowAppType.TERMINAL -> "Terminal"
                            WindowAppType.NOTES -> "Notepad"
                            WindowAppType.CALCULATOR -> "Calculator"
                            WindowAppType.SETTINGS -> "Settings"
                            else -> "App"
                        }
                        viewModel.openWindow(appType, title)
                    }
                )
            }
        }

        // 4. Windows 11 Quick Settings Flyout
        if (isQuickSettingsOpen) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 56.dp, end = 12.dp)
            ) {
                WindowsQuickSettings(
                    isDarkMode = settings.isDarkMode,
                    onToggleDarkMode = { viewModel.toggleDarkMode(it) },
                    onDismiss = { isQuickSettingsOpen = false }
                )
            }
        }

        // 5. Windows 11 Calendar & Notification Flyout
        if (isCalendarOpen) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 56.dp, end = 12.dp)
            ) {
                WindowsCalendarFlyout(
                    isDarkMode = settings.isDarkMode,
                    onDismiss = { isCalendarOpen = false }
                )
            }
        }

        // 6. Windows 11 Bottom Taskbar
        WindowsTaskbar(
            isDarkMode = settings.isDarkMode,
            openWindows = openWindows,
            activeWindowId = activeWindowId,
            dockItems = dockItems,
            isStartMenuOpen = isStartMenuOpen,
            isQuickSettingsOpen = isQuickSettingsOpen,
            isCalendarOpen = isCalendarOpen,
            onToggleStartMenu = {
                isStartMenuOpen = !isStartMenuOpen
                isQuickSettingsOpen = false
                isCalendarOpen = false
            },
            onToggleQuickSettings = {
                isQuickSettingsOpen = !isQuickSettingsOpen
                isStartMenuOpen = false
                isCalendarOpen = false
            },
            onToggleCalendar = {
                isCalendarOpen = !isCalendarOpen
                isStartMenuOpen = false
                isQuickSettingsOpen = false
            },
            onTaskbarItemClick = { item ->
                viewModel.handleTaskbarClick(item)
            },
            onWindowClick = { win ->
                viewModel.handleWindowTaskbarClick(win)
            },
            onShowDesktop = {
                viewModel.toggleShowDesktop()
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

