package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.db.LauncherSettingsEntity
import com.example.data.db.NoteEntity
import com.example.model.DesktopIconModel
import com.example.model.DockItemModel
import com.example.model.InstalledApp
import com.example.model.WindowAppType
import com.example.model.WindowState
import com.example.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class LauncherViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val appRepository = AppRepository(application)

    val notes: StateFlow<List<NoteEntity>> = db.noteDao().getAllNotes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val launcherSettings: StateFlow<LauncherSettingsEntity?> = db.settingsDao().getSettings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), LauncherSettingsEntity())

    private val _installedApps = MutableStateFlow<List<InstalledApp>>(emptyList())
    val installedApps: StateFlow<List<InstalledApp>> = _installedApps.asStateFlow()

    private val _openWindows = MutableStateFlow<List<WindowState>>(emptyList())
    val openWindows: StateFlow<List<WindowState>> = _openWindows.asStateFlow()

    private val _activeFocusedWindowId = MutableStateFlow<String?>(null)
    val activeFocusedWindowId: StateFlow<String?> = _activeFocusedWindowId.asStateFlow()

    private val _dockItems = MutableStateFlow<List<DockItemModel>>(emptyList())
    val dockItems: StateFlow<List<DockItemModel>> = _dockItems.asStateFlow()

    private val _desktopIcons = MutableStateFlow<List<DesktopIconModel>>(emptyList())
    val desktopIcons: StateFlow<List<DesktopIconModel>> = _desktopIcons.asStateFlow()

    private val _isLaunchpadOpen = MutableStateFlow(false)
    val isLaunchpadOpen: StateFlow<Boolean> = _isLaunchpadOpen.asStateFlow()

    private val _isControlCenterOpen = MutableStateFlow(false)
    val isControlCenterOpen: StateFlow<Boolean> = _isControlCenterOpen.asStateFlow()

    private val _isAppleMenuOpen = MutableStateFlow(false)
    val isAppleMenuOpen: StateFlow<Boolean> = _isAppleMenuOpen.asStateFlow()

    init {
        loadInstalledApps()
        initDefaultDockAndDesktop()
        seedSampleNote()
    }

    private fun loadInstalledApps() {
        viewModelScope.launch {
            val apps = appRepository.getInstalledApps()
            _installedApps.value = apps
        }
    }

    private fun initDefaultDockAndDesktop() {
        val defaultDock = listOf(
            DockItemModel("dock_finder", "File Explorer", appType = WindowAppType.FINDER),
            DockItemModel("dock_safari", "Microsoft Edge", appType = WindowAppType.SAFARI),
            DockItemModel("dock_notes", "Notepad", appType = WindowAppType.NOTES),
            DockItemModel("dock_terminal", "Terminal", appType = WindowAppType.TERMINAL),
            DockItemModel("dock_settings", "Settings", appType = WindowAppType.SETTINGS)
        )
        _dockItems.value = defaultDock

        _desktopIcons.value = emptyList()
    }

    private fun seedSampleNote() {
        viewModelScope.launch {
            db.noteDao().insertNote(
                NoteEntity(
                    title = "Welcome to Windows 11 Launcher!",
                    content = "This is a full Windows 11 desktop experience for Android.\n\nKey Features:\n- Windows 11 Taskbar, Start Menu, Quick Settings & Calendar\n- Freeform multi-windowing with Windows titlebar controls (— 🗖 ✕)\n- Clean, full-canvas window content without clutter\n- Built-in File Explorer, Edge, Notepad, Calculator, Terminal, Settings\n- Native Android app support!",
                    isPinned = true
                )
            )
        }
    }

    fun openWindow(
        appType: WindowAppType,
        title: String,
        extraData: String? = null,
        packageName: String? = null,
        appIcon: android.graphics.drawable.Drawable? = null
    ) {
        val existingWindow = _openWindows.value.find { it.appType == appType && (packageName == null || it.packageName == packageName) }
        if (existingWindow != null) {
            // Un-minimize and focus
            _openWindows.value = _openWindows.value.map { win ->
                if (win.id == existingWindow.id) {
                    win.copy(isMinimized = false, isFocused = true, zIndex = getNextMaxZIndex())
                } else {
                    win.copy(isFocused = false)
                }
            }
            _activeFocusedWindowId.value = existingWindow.id
            return
        }

        val newId = UUID.randomUUID().toString()
        val nextZ = getNextMaxZIndex()
        val offset = (_openWindows.value.size * 30) % 150

        val newWindow = WindowState(
            id = newId,
            appType = appType,
            title = title,
            xDp = 100f + offset,
            yDp = 60f + offset,
            widthDp = if (appType == WindowAppType.CALCULATOR) 320f else 680f,
            heightDp = if (appType == WindowAppType.CALCULATOR) 420f else 460f,
            isFocused = true,
            packageName = packageName,
            appIcon = appIcon,
            extraData = extraData,
            zIndex = nextZ
        )

        _openWindows.value = _openWindows.value.map { it.copy(isFocused = false) } + newWindow
        _activeFocusedWindowId.value = newId
    }

    private fun getNextMaxZIndex(): Int {
        return (_openWindows.value.maxOfOrNull { it.zIndex } ?: 0) + 1
    }

    fun focusWindow(windowId: String) {
        val nextZ = getNextMaxZIndex()
        _openWindows.value = _openWindows.value.map { win ->
            if (win.id == windowId) {
                win.copy(isFocused = true, isMinimized = false, zIndex = nextZ)
            } else {
                win.copy(isFocused = false)
            }
        }
        _activeFocusedWindowId.value = windowId
    }

    fun handleTaskbarClick(item: DockItemModel) {
        val matchingWindows = _openWindows.value.filter { win ->
            if (item.appType != null) win.appType == item.appType
            else if (item.packageName != null) win.packageName == item.packageName
            else false
        }

        if (matchingWindows.isEmpty()) {
            if (item.appType != null) {
                openWindow(item.appType, item.label)
            } else if (item.packageName != null) {
                openWindow(
                    appType = WindowAppType.NATIVE_APP,
                    title = item.label,
                    packageName = item.packageName
                )
            }
        } else if (matchingWindows.size == 1) {
            val win = matchingWindows.first()
            if (!win.isMinimized && win.id == _activeFocusedWindowId.value) {
                minimizeWindow(win.id)
            } else {
                focusWindow(win.id)
            }
        } else {
            val activeWin = matchingWindows.find { it.id == _activeFocusedWindowId.value && !it.isMinimized }
            if (activeWin != null) {
                val currentIndex = matchingWindows.indexOf(activeWin)
                val nextIndex = (currentIndex + 1) % matchingWindows.size
                val nextWin = matchingWindows[nextIndex]
                if (nextWin.id == activeWin.id) {
                    minimizeWindow(activeWin.id)
                } else {
                    focusWindow(nextWin.id)
                }
            } else {
                val targetWin = matchingWindows.maxByOrNull { it.zIndex } ?: matchingWindows.first()
                focusWindow(targetWin.id)
            }
        }
    }

    fun handleWindowTaskbarClick(win: WindowState) {
        if (!win.isMinimized && win.id == _activeFocusedWindowId.value) {
            minimizeWindow(win.id)
        } else {
            focusWindow(win.id)
        }
    }

    fun toggleShowDesktop() {
        val nonMinimized = _openWindows.value.filter { !it.isMinimized }
        if (nonMinimized.isNotEmpty()) {
            _openWindows.value = _openWindows.value.map { it.copy(isMinimized = true, isFocused = false) }
            _activeFocusedWindowId.value = null
        } else {
            val maxZWin = _openWindows.value.maxByOrNull { it.zIndex }
            _openWindows.value = _openWindows.value.map { win ->
                if (win.id == maxZWin?.id) win.copy(isMinimized = false, isFocused = true)
                else win.copy(isMinimized = false, isFocused = false)
            }
            _activeFocusedWindowId.value = maxZWin?.id
        }
    }

    fun closeWindow(windowId: String) {
        _openWindows.value = _openWindows.value.filter { it.id != windowId }
        _activeFocusedWindowId.value = _openWindows.value.maxByOrNull { it.zIndex }?.id
    }

    fun minimizeWindow(windowId: String) {
        _openWindows.value = _openWindows.value.map { win ->
            if (win.id == windowId) {
                win.copy(isMinimized = true, isFocused = false)
            } else win
        }
        _activeFocusedWindowId.value = _openWindows.value.filter { !it.isMinimized }.maxByOrNull { it.zIndex }?.id
    }

    fun maximizeWindow(windowId: String) {
        _openWindows.value = _openWindows.value.map { win ->
            if (win.id == windowId) {
                win.copy(isMaximized = !win.isMaximized)
            } else win
        }
    }

    fun moveWindow(windowId: String, dx: Float, dy: Float) {
        _openWindows.value = _openWindows.value.map { win ->
            if (win.id == windowId && !win.isMaximized) {
                win.copy(xDp = (win.xDp + dx).coerceAtLeast(0f), yDp = (win.yDp + dy).coerceAtLeast(30f))
            } else win
        }
    }

    fun resizeWindow(windowId: String, dw: Float, dh: Float) {
        _openWindows.value = _openWindows.value.map { win ->
            if (win.id == windowId && !win.isMaximized) {
                win.copy(
                    widthDp = (win.widthDp + dw).coerceAtLeast(300f),
                    heightDp = (win.heightDp + dh).coerceAtLeast(200f)
                )
            } else win
        }
    }

    fun toggleLaunchpad() {
        _isLaunchpadOpen.value = !_isLaunchpadOpen.value
    }

    fun setLaunchpadOpen(isOpen: Boolean) {
        _isLaunchpadOpen.value = isOpen
    }

    fun toggleControlCenter() {
        _isControlCenterOpen.value = !_isControlCenterOpen.value
    }

    fun setControlCenterOpen(isOpen: Boolean) {
        _isControlCenterOpen.value = isOpen
    }

    fun toggleAppleMenu() {
        _isAppleMenuOpen.value = !_isAppleMenuOpen.value
    }

    fun setAppleMenuOpen(isOpen: Boolean) {
        _isAppleMenuOpen.value = isOpen
    }

    fun saveNote(note: NoteEntity) {
        viewModelScope.launch {
            db.noteDao().insertNote(note)
        }
    }

    fun deleteNote(noteId: Long) {
        viewModelScope.launch {
            db.noteDao().deleteNoteById(noteId)
        }
    }

    fun updateWallpaper(wallpaperId: String) {
        viewModelScope.launch {
            val current = launcherSettings.value ?: LauncherSettingsEntity()
            db.settingsDao().saveSettings(current.copy(wallpaperId = wallpaperId))
        }
    }

    fun toggleDarkMode(isDark: Boolean) {
        viewModelScope.launch {
            val current = launcherSettings.value ?: LauncherSettingsEntity()
            db.settingsDao().saveSettings(current.copy(isDarkMode = isDark))
        }
    }

    fun updateDockSize(sizeDp: Int) {
        viewModelScope.launch {
            val current = launcherSettings.value ?: LauncherSettingsEntity()
            db.settingsDao().saveSettings(current.copy(dockSizeDp = sizeDp))
        }
    }

    fun toggleDockMagnification(enabled: Boolean) {
        viewModelScope.launch {
            val current = launcherSettings.value ?: LauncherSettingsEntity()
            db.settingsDao().saveSettings(current.copy(dockMagnification = enabled))
        }
    }

    fun launchNativeApp(packageName: String): Boolean {
        return appRepository.launchApp(packageName)
    }
}
