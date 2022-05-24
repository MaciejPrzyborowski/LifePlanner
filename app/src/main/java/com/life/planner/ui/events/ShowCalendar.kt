package com.life.planner.ui.events

import android.annotation.SuppressLint
import android.app.Dialog
import android.database.sqlite.SQLiteDatabase
import android.view.View
import com.applandeo.materialcalendarview.CalendarView
import com.applandeo.materialcalendarview.EventDay
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.life.planner.R
import java.util.ArrayList

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
        calendarView.setEvents(getHighlitedDays(db))

    }

    private fun getHighlitedDays(db: SQLiteDatabase): MutableList<EventDay> {
        val events: MutableList<EventDay> = ArrayList()
        dbDate = ArrayList()

        val cursor = db.query(
            DBInfo.TABLE_NAME, null, null, null,
            null, null, null
        )
        if (cursor.count > 0) {
            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
                dbDate.add(cursor.getString(3))
                cursor.moveToNext()
            }
        }
        cursor.close()

        for (data in dbDate) {
            val calendar = java.util.Calendar.getInstance()
            val items1: List<String> = data.split(".")
            val dd = items1[0]
            val month = items1[1]
            val year = items1[2]
            calendar[java.util.Calendar.DAY_OF_MONTH] = dd.toInt()
            calendar[java.util.Calendar.MONTH] = month.toInt() - 1
            calendar[java.util.Calendar.YEAR] = year.toInt()
            events.add(EventDay(calendar, R.drawable.dot))
        }
        return events
    }
}