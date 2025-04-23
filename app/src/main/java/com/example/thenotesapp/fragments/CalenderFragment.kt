package com.example.thenotesapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.thenotesapp.MainActivity
import com.example.thenotesapp.adapter.NoteAdapter
import com.example.thenotesapp.databinding.FragmentCalendarBinding
import com.example.thenotesapp.model.Note
import com.example.thenotesapp.viewmodel.NoteViewModel
import java.util.*

class CalendarFragment : Fragment() {

    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: NoteViewModel
    private lateinit var noteAdapter: NoteAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as MainActivity).noteViewModel

        noteAdapter = NoteAdapter(
            onNoteClick = { note ->
                // Optional: navigate to EditNoteFragment
            },
            onNoteUpdate = { viewModel.updateNote(it) }
        )

        binding.notesRecyclerView.apply {
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            adapter = noteAdapter
        }

        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        loadNotesByDate(today)

        binding.calendarView.setOnDateChangeListener { _: CalendarView, year, month, dayOfMonth ->
            val selectedCalendar = Calendar.getInstance()
            selectedCalendar.set(year, month, dayOfMonth, 0, 0, 0)
            selectedCalendar.set(Calendar.MILLISECOND, 0)
            val selectedDate = selectedCalendar.timeInMillis
            loadNotesByDate(selectedDate)
        }
    }

    private fun loadNotesByDate(dateMillis: Long) {
        val oneDayMillis = 24 * 60 * 60 * 1000
        val startOfDay = dateMillis
        val endOfDay = dateMillis + oneDayMillis - 1

        viewModel.getNotesByDateRange(startOfDay, endOfDay).observe(viewLifecycleOwner) { notes: List<Note> ->
            noteAdapter.differ.submitList(notes)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
