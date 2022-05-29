package com.life.planner.ui.events

import android.annotation.SuppressLint
import android.app.Dialog
import android.database.sqlite.SQLiteDatabase
import android.view.View
import android.widget.Toast
import com.applandeo.materialcalendarview.CalendarView
import com.applandeo.materialcalendarview.EventDay
import com.applandeo.materialcalendarview.listeners.OnDayClickListener
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.life.planner.R
import java.util.*


/**
 * Klasa obsługjąca wyświetlanie kalendarza z zaplowanymi wydarzeniami
 *
 */
class ShowCalendar : BottomSheetDialogFragment() {

    private lateinit var dbDate: ArrayList<String>

    /**
     * Konfiguracja dialogu wyświetlanego na dole ekranu
     *
     * @param dialog - uchwyt dialogu
     * @param style - styl dialogu
     */
    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        val view = View.inflate(context, R.layout.fragment_calendar_view, null)
        dialog.setContentView(view)

        val dbHelper = EventsDBHelper(requireContext())
        val db = dbHelper.writableDatabase
        getData(db)

        val calendarView = view.findViewById<CalendarView>(R.id.calendarView)
        calendarView.setOnDayClickListener(object : OnDayClickListener {
            override fun onDayClick(eventDay: EventDay) {
                val picked = eventDay.calendar.timeInMillis
                val count = countDate(db, picked)
                if (count == 0) {
                    Toast.makeText(
                        requireContext(),
                        resources.getString(R.string.event_no_events),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        requireContext(),
                        resources.getString(R.string.event_schedulded) + count,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
        calendarView.setEvents(setHighlightDays())
    }

    /**
     * Ustawia widoczne zaplanowane wydarzenia w kalendarzu
     *
     * @return zaplanowane wydarzenia w kalendarzu
     */
    private fun setHighlightDays(): MutableList<EventDay> {
        val events: MutableList<EventDay> = ArrayList()
        for (data in dbDate) {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = data.toLong()
            events.add(EventDay(calendar, R.drawable.dot))
        }
        return events
    }

    /**
     * Pobiera wszystkie daty zaplanowanych wydarzeń
     *
     * @param db - uchwyt bazy danych
     */
    private fun getData(db: SQLiteDatabase) {
        dbDate = ArrayList()
        val cursor = db.query(
            DBInfo.TABLE_NAME, arrayOf(DBInfo.TABLE_COLUMN_TIMESTAMP), null, null,
            null, null, null
        )
        if (cursor.count > 0) {
            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
                dbDate.add(cursor.getString(0))
                cursor.moveToNext()
            }
        }
        cursor.close()
    }

    /**
     * Zlicza ilość wydarzeń zaplanowanych na konkretny dzień
     *
     * @param db - uchwyt bazy danych
     * @param timestamp - czas wyrażony w formacie Unix Timestamp
     * @return ilość wydarzeń zaplowanych na konkretny dzień
     */
    private fun countDate(db: SQLiteDatabase, timestamp: Long): Int {
        val timestamp24h = timestamp + 24 * 60 * 60 * 1000
        val cursor = db.rawQuery(
            "Select * FROM ${DBInfo.TABLE_NAME} WHERE (${DBInfo.TABLE_COLUMN_TIMESTAMP} >= " +
                    timestamp.toString() + " AND ${DBInfo.TABLE_COLUMN_TIMESTAMP} < " + timestamp24h.toString() + ")",
            null
        )
        val cursorCount = cursor.count
        cursor.close()
        return cursorCount
    }
}