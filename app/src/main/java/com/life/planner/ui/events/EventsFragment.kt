package com.life.planner.ui.events

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.life.planner.databinding.FragmentEventsBinding

class EventsFragment : Fragment() {

    private var _binding: FragmentEventsBinding? = null
    private val binding get() = _binding!!

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
            val addTask = AddTask(binding.eventRecyclerView)
            addTask.show(parentFragmentManager, "addTask")
        }
        return binding.root
    }

    override fun onViewCreated(itemView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(itemView, savedInstanceState)
        binding.eventRecyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = EventsRecyclerAdapter(binding.eventRecyclerView, EventsDBHelper(context).writableDatabase)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        binding.eventRecyclerView.adapter?.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}