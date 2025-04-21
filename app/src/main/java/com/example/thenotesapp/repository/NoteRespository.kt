package com.example.thenotesapp.repository

import com.example.thenotesapp.database.NoteDatabase
import com.example.thenotesapp.model.Note

class NoteRespository(private val db: NoteDatabase) {

    suspend fun insertNote(note: Note) = db.getNoteDao().insertNote(note)
    suspend fun deleteNote(note: Note) = db.getNoteDao().deleteNote(note)
    suspend fun updateNote(note: Note) = db.getNoteDao().updateNote(note)

    fun getAllNotes() = db.getNoteDao().getAllNotes()
    fun searchNote(query: String?) = db.getNoteDao().searchNote(query)

    // 🔒 Get only locked notes
    fun getLockedNotes() = db.getNoteDao().getLockedNotes()

    // 📌 Get only pinned notes
    fun getPinnedNotes() = db.getNoteDao().getPinnedNotes()

    fun getUnlockedNotes() = db.getNoteDao().getUnlockedNotes()

}
