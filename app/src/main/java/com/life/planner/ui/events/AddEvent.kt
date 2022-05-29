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

/**
 * Klasa obsługująca dodawanie / aktualizowanie wydarzenia
 *
 * @property recyclerView - uchwyt recyclerView
 * @property eventID - identyfikator wydarzenia (przy aktualizacji)
 * @property position - pozycja na liście (przy aktualizacji)
 */
class AddEvent(
    private val recyclerView: RecyclerView,
    private val eventID: Int = -1,
    private val position: Int = -1
) : BottomSheetDialogFragment() {
    private lateinit var addTaskTitle: EditText
    private lateinit var addTaskDesc: EditText
    private lateinit var addTaskDate: TextView
    private lateinit var addTaskTime: TextView
    private lateinit var addTaskButton: Button

    /**
     * Konfiguracja dialogu wyświetlanego na dole ekranu
     *
     * @param dialog - uchwyt dialogu
     * @param style - styl dialogu
     */
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
                addEvent(
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

    /**
     * Nasłuchuje wybór daty z okna kalendarza
     *
     * @param calendar - uchwyt kalendarza
     * @param text - uchwyt TextView
     * @return wybrana data w poprawnym formacie
     */
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

    /**
     * Nasłuchuje wybór godziny z okna kalendarza
     *
     * @param calendar - uchwyt kalendarza
     * @param text - uchwyt TextView
     * @return wybrana godzina w poprawnym formacie
     */
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

    /**
     * Zapisuje wydarzenie do bazy danych
     *
     * @param db - uchwyt bazy danych
     * @param value - wartości do zapisania
     */
    private fun addEvent(db: SQLiteDatabase, value: ContentValues) {
        if (eventID == -1) {
            db.insertOrThrow(DBInfo.TABLE_NAME, null, value)
        } else {
            db.update(DBInfo.TABLE_NAME, value, BaseColumns._ID + "=?", arrayOf(eventID.toString()))
        }
        recyclerView.adapter!!.notifyItemChanged(position)
    }

    /**
     * Zwraca wartości, które mają zostać zapisane w bazie danych
     *
     * @param title - tytuł wydarzenia
     * @param desc - opis wydarzenia
     * @param date - data wydarzenia
     * @param time - godzina wydarzenia
     * @return wartości, które mają zostać zapisane w bazie danych
     */
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

    /**
     * Pobiera dane istniejącego już wydarzenia z bazy danych
     *
     * @param db - uchwyt bazy danych
     */
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

    /**
     * Formatuje datę na podstawie podanego czasu w formacie Unix Timestamp
     *
     * @param time - czas wyrażony w formacie Unix Timestamp
     * @return sformatowana data
     */
    private fun getDate(time: String): String {
        val calendar = java.util.Calendar.getInstance()
        calendar.timeInMillis = time.toLong()
        return DateFormat.format("dd MM yyyy HH mm", calendar).toString()
    }

    /**
     * Sprawdza czy wymagane dane zostały wprowadzone
     *
     * @param title - tytuł wydarzenia
     * @param desc - opis wydarzenia
     * @param date - data wydarzenia
     * @param time - godzina wydarzenia
     * @return true - wprowadzono wszystkie dane
     */
    private fun checkData(title: String, desc: String, date: String, time: String): Boolean {
        if (title.isNotEmpty() && desc.isNotEmpty() && date.isNotEmpty() && time.isNotEmpty()) {
            return true
        }
        return false
    }
}