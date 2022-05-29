package com.life.planner.ui.events

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.life.planner.databinding.FragmentEventsBinding

/**
 * Klasa obsługująca widok fragmentu Events
 *
 */
class EventsFragment : Fragment() {

    private var _binding: FragmentEventsBinding? = null
    private val binding get() = _binding!!

    /**
     * Funkcja wykonywana przy tworzeniu widoku
     *
     * @param inflater - uchwyt LayoutInflater
     * @param container - uchwyt grupy widoków
     * @param savedInstanceState - uchwyt Bundle
     * @return widok fragmentu
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventsBinding.inflate(inflater, container, false)
        binding.calendarMenu.setOnClickListener {
            val showCalendar = ShowCalendar()
            showCalendar.show(parentFragmentManager, "showCalendar")
        }
        binding.addTaskMenu.setOnClickListener {
            val addTask = AddEvent(binding.eventRecyclerView)
            addTask.show(parentFragmentManager, "addTask")
        }
        return binding.root
    }

    /**
     * Funkcja wykonywana przy stworzeniu widoku
     *
     * @param itemView - uchwyt widoku
     * @param savedInstanceState - uchwyt Bundle
     */
    override fun onViewCreated(itemView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(itemView, savedInstanceState)
        binding.eventRecyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = EventsRecyclerAdapter(binding.eventRecyclerView, EventsDBHelper(context).writableDatabase)
        }
    }

    /**
     * Funkcja wykonywana przy wznowieniu aplikacji
     *
     */
    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        binding.eventRecyclerView.adapter?.notifyDataSetChanged()
    }

    /**
     * Funkcja wykonywana przy niszczeniu widoku
     *
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}