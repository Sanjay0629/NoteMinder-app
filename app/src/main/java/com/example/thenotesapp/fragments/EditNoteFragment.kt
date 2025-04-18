package com.example.thenotesapp.fragments

import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.thenotesapp.MainActivity
import com.example.thenotesapp.R
import com.example.thenotesapp.databinding.FragmentEditNoteBinding
import com.example.thenotesapp.model.Note
import com.example.thenotesapp.viewmodel.NoteViewModel

class EditNoteFragment : Fragment(R.layout.fragment_edit_note), MenuProvider {

    private var editNoteBinding: FragmentEditNoteBinding? = null
    private val binding get() = editNoteBinding!!

    private lateinit var notesViewModel: NoteViewModel
    private lateinit var currentNote: Note

    private val args: EditNoteFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        editNoteBinding = FragmentEditNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        notesViewModel = (activity as MainActivity).noteViewModel

        args.note?.let { note ->
            currentNote = note

            if (note.isLocked && !note.pinCode.isNullOrEmpty()) {
                showPinDialog(note)
            } else {
                showNoteContent(note)
            }

            binding.editNoteFab.setOnClickListener {
                saveUpdatedNote()
            }

        } ?: run {
            Toast.makeText(requireContext(), "Error: Note not found", Toast.LENGTH_SHORT).show()
            view.findNavController().popBackStack()
        }
    }

    private fun showPinDialog(note: Note) {
        val input = EditText(requireContext())
        input.hint = "Enter PIN"
        input.inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_VARIATION_PASSWORD

        AlertDialog.Builder(requireContext())
            .setTitle("Unlock Note")
            .setMessage("This note is locked. Please enter the PIN.")
            .setView(input)
            .setPositiveButton("Unlock") { _, _ ->
                val enteredPin = input.text.toString()
                if (enteredPin == note.pinCode) {
                    showNoteContent(note)
                } else {
                    Toast.makeText(context, "Incorrect PIN", Toast.LENGTH_SHORT).show()
                    view?.findNavController()?.popBackStack()
                }
            }
            .setNegativeButton("Cancel") { _, _ ->
                view?.findNavController()?.popBackStack()
            }
            .setCancelable(false)
            .show()
    }

    private fun showNoteContent(note: Note) {
        binding.editNoteTitle.setText(note.noteTitle)
        binding.editNoteDesc.setText(note.noteDesc)

        // Optionally show pin/lock states (if visible in layout)
        binding.editPinCheckBox.isChecked = note.isPinned
        binding.editLockCheckBox.isChecked = note.isLocked
    }

    private fun saveUpdatedNote() {
        val noteTitle = binding.editNoteTitle.text.toString().trim()
        val noteDesc = binding.editNoteDesc.text.toString().trim()
        val isPinned = binding.editPinCheckBox.isChecked
        val isLocked = binding.editLockCheckBox.isChecked

        if (noteTitle.isNotEmpty()) {
            val updatedNote = currentNote.copy(
                noteTitle = noteTitle,
                noteDesc = noteDesc,
                isPinned = isPinned,
                isLocked = isLocked,
                pinCode = if (isLocked) currentNote.pinCode else null
            )
            notesViewModel.updateNote(updatedNote)
            view?.findNavController()?.popBackStack(R.id.homeFragment, false)
        } else {
            Toast.makeText(context, "Please enter a Note Title", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.clear()
        menuInflater.inflate(R.menu.menu_edit_note, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.deleteMenu -> {
                deleteNote()
                true
            }
            else -> false
        }
    }

    private fun deleteNote() {
        AlertDialog.Builder(requireContext()).apply {
            setTitle("Delete Note")
            setMessage("Do you want to delete this note?")
            setPositiveButton("Delete") { _, _ ->
                notesViewModel.deleteNote(currentNote)
                Toast.makeText(context, "Note deleted successfully", Toast.LENGTH_SHORT).show()
                view?.findNavController()?.popBackStack(R.id.homeFragment, false)
            }
            setNegativeButton("Cancel", null)
        }.create().show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        editNoteBinding = null
    }
}
