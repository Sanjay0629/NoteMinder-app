package com.example.thenotesapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.thenotesapp.model.Note
import com.example.thenotesapp.repository.NoteRespository
import kotlinx.coroutines.launch

class NoteViewModel(app: Application , private val noteRespository: NoteRespository): AndroidViewModel(app) {

    fun addNote(note: Note) =
        viewModelScope.launch {
            noteRespository.insertNote(note)
        }

    fun deleteNote(note: Note) =
        viewModelScope.launch {
            noteRespository.deleteNote(note)
        }

    fun updateNote(note: Note) =
        viewModelScope.launch {
            noteRespository.updateNote(note)
        }

    fun getAllNotes() = noteRespository.getAllNotes()
    fun searchNote(query: String?) = noteRespository.searchNote(query)
}