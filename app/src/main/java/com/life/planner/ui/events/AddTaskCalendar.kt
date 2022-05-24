package com.life.planner.ui.events

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.icu.util.Calendar
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.life.planner.R
import java.text.SimpleDateFormat
import java.util.*

class AddTaskCalendar : BottomSheetDialogFragment() {


    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        val view = View.inflate(context, R.layout.fragment_addtask_calendar, null)
        dialog.setContentView(view)

        val dbHelper = EventsDBHelper(requireContext())
        val db = dbHelper.writableDatabase

        val addTaskTitle = view.findViewById<EditText>(R.id.addTaskTitle)
        val addTaskDesc = view.findViewById<EditText>(R.id.addTaskDescription)
        val addTaskDate = view.findViewById<EditText>(R.id.addTaskDate)
        val addTaskTime = view.findViewById<EditText>(R.id.addTaskTime)
        val addTaskButton = view.findViewById<Button>(R.id.addTask)

        addTaskDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                addDateSetListener(calendar, addTaskDate),
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
            datePickerDialog.show()
        }

        addTaskTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            val timePickerDialog = TimePickerDialog(
                requireContext(), addTimeSetListener(calendar, addTaskTime),
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true
            )
            timePickerDialog.show()
        }

        addTaskButton.setOnClickListener {
            if (checkData(
                    addTaskTitle.text.toString(),
                    addTaskDesc.text.toString(),
                    addTaskDate.text.toString(),
                    addTaskTime.text.toString()
                )
            ) {
                addTask(
                    db,
                    createContentValue(
                        addTaskTitle.text.toString(),
                        addTaskDesc.text.toString(),
                        addTaskDate.text.toString(),
                        addTaskTime.text.toString()
                    )
                )
                dialog.dismiss()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Nie wprowadzono wszystkich danych",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }
    private fun addDateSetListener(calendar: Calendar, text: EditText): DatePickerDialog.OnDateSetListener {
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, monthOfYear)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                text.setText(sdf.format(calendar.time))
            }
        return dateSetListener
    }
    private fun checkData(title: String, desc: String, date: String, time: String): Boolean {
        if (title.isNotEmpty() && desc.isNotEmpty() && date.isNotEmpty() && time.isNotEmpty()) {
            return true
        }
        return false
    }

    private fun addTask(db: SQLiteDatabase, value: ContentValues) {
        db.insertOrThrow(DBInfo.TABLE_NAME, null, value)
    }

    private fun createContentValue(
        title: String,
        desc: String,
        date: String,
        time: String
    ): ContentValues {
        val contentValue = ContentValues()
        contentValue.put(DBInfo.TABLE_COLUMN_TITLE, title)
        contentValue.put(DBInfo.TABLE_COLUMN_DESC, desc)
        contentValue.put(DBInfo.TABLE_COLUMN_DATE, date)
        contentValue.put(DBInfo.TABLE_COLUMN_TIME, time)
        return contentValue
    }

    private fun addTimeSetListener(
        calendar: Calendar,
        text: EditText
    ): TimePickerDialog.OnTimeSetListener {
        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minute)
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            text.setText(sdf.format(calendar.time))
        }
        return timeSetListener
    }

}