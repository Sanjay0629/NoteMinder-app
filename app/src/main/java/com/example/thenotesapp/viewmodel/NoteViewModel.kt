package com.example.thenotesapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.thenotesapp.model.Note
import com.example.thenotesapp.repository.NoteRespository
import kotlinx.coroutines.launch

class NoteViewModel(
    app: Application,
    private val noteRespository: NoteRespository
) : AndroidViewModel(app) {

    // 🚀 Insert a new note
    fun addNote(note: Note) = viewModelScope.launch {
        noteRespository.insertNote(note)
    }

    // 🗑️ Delete a note
    fun deleteNote(note: Note) = viewModelScope.launch {
        noteRespository.deleteNote(note)
    }

    // ✏️ Update existing note
    fun updateNote(note: Note) = viewModelScope.launch {
        noteRespository.updateNote(note)
    }

    // 📋 Get all notes (pinned + normal, sorted)
    fun getAllNotes() = noteRespository.getAllNotes()

    // 🔍 Search notes by title or description
    fun searchNote(query: String?) = noteRespository.searchNote(query)

    // 📌 Get only pinned notes
    fun getPinnedNotes() = noteRespository.getPinnedNotes()

    // 🔒 Get only locked notes
    fun getLockedNotes() = noteRespository.getLockedNotes()

    // 📂 Optional future addition for exporting only unlocked notes
    fun getUnlockedNotes() = noteRespository.getUnlockedNotes() // Only if you add this in DAO

    // ✅ NEW FUNCTION: Get notes created between date range
    fun getNotesByDateRange(startDate: Long, endDate: Long) =
        noteRespository.getNotesByDateRange(startDate, endDate)
}
