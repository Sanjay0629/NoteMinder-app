package com.example.thenotesapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.thenotesapp.R
import com.example.thenotesapp.databinding.NoteLayoutBinding
import com.example.thenotesapp.model.Note
import java.text.SimpleDateFormat
import java.util.*

class NoteAdapter(
    private val onNoteClick: (Note) -> Unit,
    private val onNoteUpdate: (Note) -> Unit
) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    inner class NoteViewHolder(val binding: NoteLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    private val differCallback = object : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem == newItem
        }
    }

    val differ: AsyncListDiffer<Note> = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = NoteLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteViewHolder(binding)
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val currentNote = differ.currentList[position]
        with(holder.binding) {
            noteTitle.text = currentNote.noteTitle
            noteDesc.text = currentNote.noteDesc

            // 🗓️ Show formatted created date
            val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            noteCreatedDate.text = "Created: ${sdf.format(Date(currentNote.createdDate))}"

            // 📌 Show pin icon
            pinIcon.visibility = if (currentNote.isPinned) View.VISIBLE else View.GONE

            // 🔒 Show lock icon
            lockIcon.visibility = View.VISIBLE
            lockIcon.setImageResource(
                if (currentNote.isLocked) R.drawable.baseline_lock_24
                else R.drawable.baseline_lock_open_24
            )

            // 🔁 Click listeners
            root.setOnClickListener {
                onNoteClick(currentNote)
            }

            pinIcon.setOnClickListener {
                val updatedNote = currentNote.copy(isPinned = !currentNote.isPinned)
                onNoteUpdate(updatedNote)
            }

            lockIcon.setOnClickListener {
                val updatedNote = currentNote.copy(isLocked = !currentNote.isLocked)
                onNoteUpdate(updatedNote)
            }
        }
    }

    fun getNoteAt(position: Int): Note = differ.currentList[position]
}
