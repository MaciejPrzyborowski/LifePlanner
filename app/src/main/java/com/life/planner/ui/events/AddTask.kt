package com.life.planner.ui.events

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.icu.util.Calendar
import android.provider.BaseColumns
import android.text.format.DateFormat
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.life.planner.R
import java.text.SimpleDateFormat
import java.util.*

class AddTask(
    private val recyclerView: RecyclerView,
    private val eventID: Int = -1,
    private val position: Int = -1
) : BottomSheetDialogFragment() {
    private lateinit var addTaskTitle: EditText
    private lateinit var addTaskDesc: EditText
    private lateinit var addTaskDate: TextView
    private lateinit var addTaskTime: TextView
    private lateinit var addTaskButton: Button

    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        val view = View.inflate(context, R.layout.fragment_addtask_calendar, null)
        dialog.setContentView(view)

        val dbHelper = EventsDBHelper(requireContext())
        val db = dbHelper.writableDatabase

        addTaskTitle = view.findViewById(R.id.addTaskTitle)
        addTaskDesc = view.findViewById(R.id.addTaskDescription)
        addTaskDate = view.findViewById(R.id.addTaskDate)
        addTaskTime = view.findViewById(R.id.addTaskTime)
        addTaskButton = view.findViewById(R.id.addTask)
        getData(db)

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
                    resources.getString(R.string.addTaskCalendarError_missing),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun addDateSetListener(
        calendar: Calendar,
        text: TextView
    ): DatePickerDialog.OnDateSetListener {
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, monthOfYear)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                text.text = dateFormat.format(calendar.time)
            }
        return dateSetListener
    }

    private fun addTimeSetListener(
        calendar: Calendar,
        text: TextView
    ): TimePickerDialog.OnTimeSetListener {
        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minute)
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            text.text = timeFormat.format(calendar.time)
        }
        return timeSetListener
    }

    private fun addTask(db: SQLiteDatabase, value: ContentValues) {
        if (eventID == -1) {
            db.insertOrThrow(DBInfo.TABLE_NAME, null, value)
        } else {
            db.update(DBInfo.TABLE_NAME, value, BaseColumns._ID + "=?", arrayOf(eventID.toString()))
        }
        recyclerView.adapter!!.notifyItemChanged(position)
    }

    private fun createContentValue(
        title: String,
        desc: String,
        date: String,
        time: String
    ): ContentValues {
        val contentValue = ContentValues()
        val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        val dateParse = dateFormat.parse("$date $time")
        contentValue.put(DBInfo.TABLE_COLUMN_TITLE, title)
        contentValue.put(DBInfo.TABLE_COLUMN_DESC, desc)
        contentValue.put(DBInfo.TABLE_COLUMN_TIMESTAMP, dateParse?.time.toString())
        return contentValue
    }

    private fun getData(db: SQLiteDatabase) {
        if (eventID != -1) {
            val cursor = db.rawQuery(
                "Select * FROM ${DBInfo.TABLE_NAME} WHERE ${BaseColumns._ID} = " +
                        eventID.toString(), null
            )
            if (cursor.count > 0) {
                cursor.moveToFirst()
                val timestamp = cursor.getString(3).toString()
                val dateItems = getDate(timestamp).split(" ")
                val taskDate = dateItems[0] + "." + dateItems[1] + "." + dateItems[2]
                val taskTime = dateItems[3] + ":" + dateItems[4]

                addTaskTitle.setText(cursor.getString(1).toString())
                addTaskDesc.setText(cursor.getString(2).toString())
                addTaskDate.text = taskDate
                addTaskTime.text = taskTime
            }
            cursor.close()
        }
    }

    private fun getDate(time: String): String {
        val calendar = java.util.Calendar.getInstance()
        calendar.timeInMillis = time.toLong()
        return DateFormat.format("dd MM yyyy HH mm", calendar).toString()
    }

    private fun checkData(title: String, desc: String, date: String, time: String): Boolean {
        if (title.isNotEmpty() && desc.isNotEmpty() && date.isNotEmpty() && time.isNotEmpty()) {
            return true
        }
        return false
    }
}