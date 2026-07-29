package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY isPinned DESC, timestamp DESC")
    fun getAllNotes(): Flow<List<NoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity): Long

    @Update
    suspend fun updateNote(note: NoteEntity)

    @Delete
    suspend fun deleteNote(note: NoteEntity)

    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun deleteNoteById(id: Long)
}

@Dao
interface DockDao {
    @Query("SELECT * FROM dock_items ORDER BY position ASC")
    fun getAllDockItems(): Flow<List<DockItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDockItems(items: List<DockItemEntity>)

    @Query("DELETE FROM dock_items")
    suspend fun deleteAllDockItems()
}

@Dao
interface DesktopDao {
    @Query("SELECT * FROM desktop_icons")
    fun getAllDesktopIcons(): Flow<List<DesktopIconEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDesktopIcon(icon: DesktopIconEntity): Long

    @Delete
    suspend fun deleteDesktopIcon(icon: DesktopIconEntity)
}

@Dao
interface SettingsDao {
    @Query("SELECT * FROM launcher_settings WHERE id = 1")
    fun getSettings(): Flow<LauncherSettingsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSettings(settings: LauncherSettingsEntity)
}
