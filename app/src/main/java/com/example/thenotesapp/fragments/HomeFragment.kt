package com.example.thenotesapp.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.thenotesapp.MainActivity
import com.example.thenotesapp.R
import com.example.thenotesapp.adapter.NoteAdapter
import com.example.thenotesapp.databinding.FragmentHomeBinding
import com.example.thenotesapp.model.Note
import com.example.thenotesapp.viewmodel.NoteViewModel
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment(R.layout.fragment_home),
    SearchView.OnQueryTextListener, MenuProvider {

    private var homeBinding: FragmentHomeBinding? = null
    private val binding get() = homeBinding!!

    private lateinit var notesViewModel: NoteViewModel
    private lateinit var noteAdapter: NoteAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeBinding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        notesViewModel = (activity as MainActivity).noteViewModel

        setupHomeRecyclerView()

        binding.addNoteFab.setOnClickListener {
            it.findNavController().navigate(R.id.action_homeFragment_to_addNoteFragment)
        }

        // 🔐 Request storage permission for Android 6–10
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 101)
            }
        }
    }

    private fun setupHomeRecyclerView() {
        noteAdapter = NoteAdapter(
            onNoteClick = { note ->
                view?.findNavController()
                    ?.navigate(HomeFragmentDirections.actionHomeFragmentToEditNoteFragment(note))
            },
            onNoteUpdate = { note ->
                notesViewModel.updateNote(note)
            }
        )

        binding.homeRecyclerView.apply {
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            setHasFixedSize(true)
            adapter = noteAdapter
        }

        notesViewModel.getAllNotes().observe(viewLifecycleOwner) { notes ->
            noteAdapter.differ.submitList(notes)
            updateUI(notes)
        }
    }

    private fun updateUI(notes: List<Note>?) {
        if (!notes.isNullOrEmpty()) {
            binding.emptyNotesImage.visibility = View.GONE
            binding.homeRecyclerView.visibility = View.VISIBLE
        } else {
            binding.emptyNotesImage.visibility = View.VISIBLE
            binding.homeRecyclerView.visibility = View.GONE
        }
    }

    private fun searchNote(query: String?) {
        val searchQuery = "%$query%"
        notesViewModel.searchNote(searchQuery).observe(viewLifecycleOwner) { list ->
            noteAdapter.differ.submitList(list)
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean = false

    override fun onQueryTextChange(newText: String?): Boolean {
        if (!newText.isNullOrEmpty()) {
            searchNote(newText)
        }
        return true
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.clear()
        menuInflater.inflate(R.menu.home_menu, menu)

        val searchItem = menu.findItem(R.id.searchMenu)
        val searchView = searchItem.actionView as? SearchView
        searchView?.isSubmitButtonEnabled = false
        searchView?.setOnQueryTextListener(this)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.exportNotes -> {
                exportNotesToFile()
                true
            }

            else -> false
        }
    }

    private fun exportNotesToFile() {
        val notes = noteAdapter.differ.currentList
        if (notes.isEmpty()) {
            Toast.makeText(requireContext(), "No notes to export", Toast.LENGTH_SHORT).show()
            return
        }

        val exportText = StringBuilder()
        for ((index, note) in notes.withIndex()) {
            exportText.append("Note ${index + 1}:\n")
            exportText.append("Title: ${note.noteTitle}\n")
            exportText.append("Description: ${note.noteDesc}\n\n")
        }

        try {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "Exported_Notes_$timeStamp.txt"
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDir, fileName)

            FileOutputStream(file).use { output ->
                output.write(exportText.toString().toByteArray())
            }

            Toast.makeText(requireContext(), "Exported to Downloads: ${file.name}", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Export failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        homeBinding = null
    }
}
