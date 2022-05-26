package com.life.planner.ui.events

import android.annotation.SuppressLint
import android.app.Dialog
import android.database.sqlite.SQLiteDatabase
import android.view.View
import com.applandeo.materialcalendarview.CalendarView
import com.applandeo.materialcalendarview.EventDay
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.life.planner.R
import java.util.*

class ShowCalendar : BottomSheetDialogFragment() {

    private lateinit var dbDate: ArrayList<String>

    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        val view = View.inflate(context, R.layout.fragment_calendar_view, null)
        dialog.setContentView(view)

        val dbHelper = EventsDBHelper(requireContext())
        val db = dbHelper.writableDatabase
        val calendarView = view.findViewById<CalendarView>(R.id.calendarView)
        calendarView.setEvents(setHighlightDays(db))
    }

    private fun setHighlightDays(db: SQLiteDatabase): MutableList<EventDay> {
        val events: MutableList<EventDay> = ArrayList()
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

        for (data in dbDate) {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = data.toLong()
            events.add(EventDay(calendar, R.drawable.dot))
        }
        return events
    }
}