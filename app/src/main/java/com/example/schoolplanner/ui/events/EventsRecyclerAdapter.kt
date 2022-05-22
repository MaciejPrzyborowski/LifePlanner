package com.example.schoolplanner.ui.events

import android.database.sqlite.SQLiteDatabase
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.schoolplanner.R
import java.text.SimpleDateFormat
import java.util.*


class EventsRecyclerAdapter(private val db: SQLiteDatabase) : RecyclerView.Adapter<EventsRecyclerAdapter.ViewHolder>() {
    private lateinit var dbID : ArrayList<String>
    private lateinit var dbTitle : ArrayList<String>
    private lateinit var dbDesc : ArrayList<String>
    private lateinit var dbDate : ArrayList<String>
    private lateinit var dbTime : ArrayList<String>
    private var dateFormat = SimpleDateFormat("EE dd MMM yyyy", Locale.getDefault())
    private var inputDateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

    override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): ViewHolder {

        val layoutInflater = LayoutInflater.from(viewGroup.context)
        val cardView = layoutInflater.inflate(R.layout.cardview_event, viewGroup, false)
        return ViewHolder(cardView)
    }

    override fun getItemCount(): Int {
        val cursor = db.query(DBInfo.TABLE_NAME, null,null, null,null, null, null)
        val cursorCount = cursor.count
        cursor.close()
        return cursorCount
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        getEventsInfo()
        setEventsInfo(viewHolder, position)
    }

    private fun getEventsInfo() {
        dbID = ArrayList()
        dbTitle = ArrayList()
        dbDesc = ArrayList()
        dbDate = ArrayList()
        dbTime = ArrayList()

        val cursor = db.query(
            DBInfo.TABLE_NAME, null, null, null,
            null, null, null)
        if(cursor.count > 0) {
            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
                dbID.add(cursor.getInt(0).toString())
                dbTitle.add(cursor.getString(1))
                dbDesc.add(cursor.getString(2))
                dbDate.add(cursor.getString(3))
                dbTime.add(cursor.getString(4))
                cursor.moveToNext()
            }
        }
        cursor.close()
    }

    private fun setEventsInfo(viewHolder: ViewHolder, position: Int) {
        var date = inputDateFormat.parse(dbDate[position])
        var outputDateString = dateFormat.format(date!!)

        val items1 = outputDateString.split(" ")
        viewHolder.eventDay.text = items1[0]
        viewHolder.eventDate.text = items1[1]
        viewHolder.eventMonth.text = items1[2]
        viewHolder.eventTitle.text = dbTitle[position]
        viewHolder.eventDesc.text = dbDesc[position]
        viewHolder.eventTime.text = dbTime[position]
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var eventDay: TextView = view.findViewById(R.id.day)
        var eventDate: TextView = view.findViewById(R.id.date)
        var eventMonth: TextView = view.findViewById(R.id.month)
        var eventTitle: TextView = view.findViewById(R.id.title)
        var eventDesc: TextView = view.findViewById(R.id.description)
        var eventTime: TextView = view.findViewById(R.id.time)
    }
}