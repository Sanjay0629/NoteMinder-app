package com.example.thenotesapp.fragments

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import com.example.thenotesapp.MainActivity
import com.example.thenotesapp.R
import com.example.thenotesapp.databinding.FragmentAddNoteBinding
import com.example.thenotesapp.model.Note
import com.example.thenotesapp.viewmodel.NoteViewModel

class AddNoteFragment : Fragment(R.layout.fragment_add_note), MenuProvider {

    private var addNoteBinding: FragmentAddNoteBinding? = null
    private val binding get() = addNoteBinding!!

    private lateinit var notesViewModel: NoteViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        addNoteBinding = FragmentAddNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        notesViewModel = (activity as MainActivity).noteViewModel

        // 👇 Show/Hide PIN input based on Lock checkbox
        binding.lockCheckBox.setOnCheckedChangeListener { _, isChecked ->
            binding.pinEditText.visibility = if (isChecked) View.VISIBLE else View.GONE
        }
    }

    private fun saveNote() {
        val noteTitle = binding.addNoteTitle.text?.toString()?.trim() ?: ""
        val noteDesc = binding.addNoteDesc.text?.toString()?.trim() ?: ""

        val isPinned = binding.pinCheckBox.isChecked
        val isLocked = binding.lockCheckBox.isChecked
        val pinCode = if (isLocked) binding.pinEditText.text?.toString()?.trim() else null

        if (noteTitle.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter note title", Toast.LENGTH_SHORT).show()
            return
        }

        if (isLocked && pinCode.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Please set a PIN for locked note", Toast.LENGTH_SHORT).show()
            return
        }

        val note = Note(
            noteTitle = noteTitle,
            noteDesc = noteDesc,
            isPinned = isPinned,
            isLocked = isLocked,
            pinCode = pinCode
        )

        notesViewModel.addNote(note)
        Toast.makeText(requireContext(), "Note Saved", Toast.LENGTH_SHORT).show()
        requireView().findNavController().popBackStack(R.id.homeFragment, false)
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.clear()
        menuInflater.inflate(R.menu.menu_add_note, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.saveMenu -> {
                saveNote()
                true
            }
            else -> false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        addNoteBinding = null
    }
}
