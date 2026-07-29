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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.NoteEntity
import com.example.ui.theme.MacOSBlue

@Composable
fun NotesApp(
    notes: List<NoteEntity>,
    isDarkMode: Boolean,
    onSaveNote: (NoteEntity) -> Unit,
    onDeleteNote: (Long) -> Unit
) {
    var selectedNoteId by remember { mutableStateOf<Long?>(notes.firstOrNull()?.id) }
    var searchQuery by remember { mutableStateOf("") }

    val activeNote = notes.find { it.id == selectedNoteId } ?: notes.firstOrNull() ?: NoteEntity(
        id = 0,
        title = "New Note",
        content = ""
    )

    var noteTitle by remember(activeNote.id) { mutableStateOf(activeNote.title) }
    var noteContent by remember(activeNote.id) { mutableStateOf(activeNote.content) }

    val sidebarBg = if (isDarkMode) Color(0xFF252525) else Color(0xFFF6F6F6)
    val editorBg = if (isDarkMode) Color(0xFF1E1E1E) else Color.White
    val textColor = if (isDarkMode) Color.White else Color(0xFF1D1D1F)

    val filteredNotes = notes.filter {
        it.title.contains(searchQuery, ignoreCase = true) || it.content.contains(searchQuery, ignoreCase = true)
    }

    Row(modifier = Modifier.fillMaxSize().background(editorBg)) {
        // Left Notes Sidebar
        Column(
            modifier = Modifier
                .width(220.dp)
                .fillMaxHeight()
                .background(sidebarBg)
                .padding(10.dp)
        ) {
            // Header Row with New Note Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Notes",
                    color = textColor,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                IconButton(
                    onClick = {
                        val newNote = NoteEntity(title = "Untitled Note", content = "")
                        onSaveNote(newNote)
                    },
                    modifier = Modifier.size(32.dp).testTag("notes_add_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.EditNote,
                        contentDescription = "New Note",
                        tint = MacOSBlue
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search Notes", fontSize = 11.sp, color = textColor.copy(alpha = 0.5f)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = textColor.copy(alpha = 0.5f),
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
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth().height(32.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Notes List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(filteredNotes) { note ->
                    val isSelected = note.id == activeNote.id
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) MacOSBlue else Color.Transparent)
                            .clickable {
                                selectedNoteId = note.id
                                noteTitle = note.title
                                noteContent = note.content
                            }
                            .padding(8.dp)
                            .testTag("note_item_${note.id}")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = note.title.ifEmpty { "Untitled" },
                                color = if (isSelected) Color.White else textColor,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                            if (note.isPinned) {
                                Icon(
                                    imageVector = Icons.Default.PushPin,
                                    contentDescription = "Pinned",
                                    tint = if (isSelected) Color.White else MacOSBlue,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = note.content.ifEmpty { "No additional text" },
                            color = if (isSelected) Color.White.copy(alpha = 0.8f) else textColor.copy(alpha = 0.6f),
                            fontSize = 11.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        // Right Note Editor Pane
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(16.dp)
        ) {
            // Action Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            val updated = activeNote.copy(
                                title = noteTitle,
                                content = noteContent,
                                isPinned = !activeNote.isPinned
                            )
                            onSaveNote(updated)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.PushPin,
                            contentDescription = "Pin Note",
                            tint = if (activeNote.isPinned) MacOSBlue else textColor.copy(alpha = 0.4f)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (activeNote.id != 0L) {
                                onDeleteNote(activeNote.id)
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Note",
                            tint = Color.Red.copy(alpha = 0.7f)
                        )
                    }
                }

                Text(
                    text = "Saved",
                    color = textColor.copy(alpha = 0.4f),
                    fontSize = 11.sp
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = textColor.copy(alpha = 0.1f))

            // Note Title Input
            TextField(
                value = noteTitle,
                onValueChange = {
                    noteTitle = it
                    onSaveNote(activeNote.copy(title = noteTitle, content = noteContent))
                },
                placeholder = { Text("Note Title", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = textColor.copy(alpha = 0.4f)) },
                textStyle = androidx.compose.ui.text.TextStyle(
                    color = textColor,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                ),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier.fillMaxWidth().testTag("note_title_input")
            )

            // Note Body Content Input
            TextField(
                value = noteContent,
                onValueChange = {
                    noteContent = it
                    onSaveNote(activeNote.copy(title = noteTitle, content = noteContent))
                },
                placeholder = { Text("Type note content here...", fontSize = 14.sp, color = textColor.copy(alpha = 0.4f)) },
                textStyle = androidx.compose.ui.text.TextStyle(
                    color = textColor,
                    fontSize = 14.sp
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .testTag("note_content_input")
            )
        }
    }
}
