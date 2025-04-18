package com.example.thenotesapp.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.thenotesapp.model.Note

@Dao
interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note)

    @Update
    suspend fun updateNote(note: Note)

    @Delete
    suspend fun deleteNote(note: Note)

    @Query("SELECT * FROM notes ORDER BY isPinned DESC, id DESC")
    fun getAllNotes(): LiveData<List<Note>>

    @Query("SELECT * FROM notes WHERE noteTitle LIKE :query OR noteDesc LIKE :query ORDER BY isPinned DESC, id DESC")
    fun searchNote(query: String?): LiveData<List<Note>>

    @Query("SELECT * FROM notes WHERE isLocked = 1 ORDER BY id DESC")
    fun getLockedNotes(): LiveData<List<Note>>

    @Query("SELECT * FROM notes WHERE isPinned = 1 ORDER BY id DESC")
    fun getPinnedNotes(): LiveData<List<Note>>
}
