package com.example.schoolplanner.ui.events

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.schoolplanner.databinding.FragmentAddtaskCalendarBinding
import com.example.schoolplanner.databinding.FragmentCalendarViewBinding
import com.example.schoolplanner.databinding.FragmentEventsBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.text.SimpleDateFormat
import java.util.*

class EventsFragment : Fragment() {

    private var _binding: FragmentEventsBinding? = null
    private val binding get() = _binding!!

    private var _addTaskBinding: FragmentAddtaskCalendarBinding? = null
    private val addTaskBinding get() = _addTaskBinding!!

    private var _calendarBinding: FragmentCalendarViewBinding? = null
    private val calendarBinding get() = _calendarBinding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventsBinding.inflate(inflater, container, false)
        _addTaskBinding = FragmentAddtaskCalendarBinding.inflate(inflater, container, false)
        _calendarBinding = FragmentCalendarViewBinding.inflate(inflater, container, false)

        val dbHelper = EventsDBHelper(requireContext())
        val db = dbHelper.writableDatabase

        binding.calendarMenu.setOnClickListener {
            val dialog = BottomSheetDialog(requireContext())
            dialog.setContentView(calendarBinding.root)
            dialog.show()
        }
        binding.addTaskMenu.setOnClickListener {
            val dialog = BottomSheetDialog(requireContext())
            dialog.setContentView(addTaskBinding.root)
            dialog.show()

            val addTaskTitle = addTaskBinding.addTaskTitle
            val addTaskDesc = addTaskBinding.addTaskDescription
            val addTaskDate = addTaskBinding.addTaskDate
            val addTaskTime = addTaskBinding.addTaskTime
            val addTaskButton = addTaskBinding.addTask

            addTaskDate.setOnClickListener {
                val calendar = Calendar.getInstance()
                val datePickerDialog = DatePickerDialog(requireContext(), addDateSetListener(calendar, addTaskDate),
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
                datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
                datePickerDialog.show()
            }

            addTaskTime.setOnClickListener {
                val calendar = Calendar.getInstance()
                val timePickerDialog = TimePickerDialog(requireContext(), addTimeSetListener(calendar, addTaskTime),
                    calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true)
                timePickerDialog.show()
            }

            addTaskButton.setOnClickListener {
                if(checkData(addTaskTitle.text.toString(), addTaskDesc.text.toString(), addTaskDate.text.toString(), addTaskTime.text.toString()))
                {
                    addTask(db, createContentValue(addTaskTitle.text.toString(), addTaskDesc.text.toString(), addTaskDate.text.toString(), addTaskTime.text.toString()))
                    dialog.dismiss()
                }
                else
                {
                    Toast.makeText(requireContext(), "Nie wprowadzono wszystkich danych", Toast.LENGTH_SHORT).show()
                }
            }
        }
        return binding.root
    }

    private fun checkData(title : String, desc : String, date : String, time : String): Boolean {
        if(title.isNotEmpty() && desc.isNotEmpty() && date.isNotEmpty() && time.isNotEmpty()) {
            return true
        }
        return false
    }

    private fun addTask(db: SQLiteDatabase, value : ContentValues) {
        db.insertOrThrow(DBInfo.TABLE_NAME, null, value)
    }

    private fun createContentValue(title : String, desc : String, date : String, time : String) : ContentValues {
        val contentValue = ContentValues()
        contentValue.put(DBInfo.TABLE_COLUMN_TITLE, title)
        contentValue.put(DBInfo.TABLE_COLUMN_DESC, desc)
        contentValue.put(DBInfo.TABLE_COLUMN_DATE, date)
        contentValue.put(DBInfo.TABLE_COLUMN_TIME, time)
        return contentValue
    }

    private fun addDateSetListener(calendar: Calendar, text: EditText) : OnDateSetListener {
        val dateSetListener = OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            text.setText(sdf.format(calendar.time))
        }
        return dateSetListener
    }

    private fun addTimeSetListener(calendar: Calendar, text: EditText) : TimePickerDialog.OnTimeSetListener {
        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minute)
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            text.setText(sdf.format(calendar.time))
        }
        return timeSetListener
    }

    override fun onViewCreated(itemView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(itemView, savedInstanceState)
        binding.taskRecyclerMenu.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = EventsRecyclerAdapter(EventsDBHelper(context).writableDatabase)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        binding.taskRecyclerMenu.adapter?.notifyDataSetChanged()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}