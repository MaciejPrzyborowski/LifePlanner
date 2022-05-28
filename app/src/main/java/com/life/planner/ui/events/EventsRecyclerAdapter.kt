package com.life.planner.ui.events

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.provider.BaseColumns
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.life.planner.R
import java.util.*


class EventsRecyclerAdapter(private val recyclerView: RecyclerView, private val db: SQLiteDatabase) :
    RecyclerView.Adapter<EventsRecyclerAdapter.ViewHolder>() {
    private lateinit var dbID: ArrayList<String>
    private lateinit var dbTitle: ArrayList<String>
    private lateinit var dbDesc: ArrayList<String>
    private lateinit var dbTimestamp: ArrayList<String>
    private var context: Context? = null

    override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): ViewHolder {
        context = viewGroup.context
        val layoutInflater = LayoutInflater.from(context)
        val cardView = layoutInflater.inflate(R.layout.cardview_event, viewGroup, false)
        return ViewHolder(cardView)
    }

    override fun getItemCount(): Int {
        val cursor = db.query(DBInfo.TABLE_NAME, null, null, null, null, null, null)
        val cursorCount = cursor.count
        cursor.close()
        return cursorCount
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        getEventsInfo()
        setEventsInfo(viewHolder, position)
        viewHolder.options.setOnClickListener {
            showPopUpMenu(it, viewHolder.eventId.text.toString().toInt(), position)
        }
    }

    private fun showPopUpMenu(view: View?, eventID: Int, position: Int) {
        val popupMenu = PopupMenu(context, view)
        popupMenu.menuInflater.inflate(R.menu.events_menu_options, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.menuUpdate -> {
                    val addTask = AddTask(recyclerView, eventID, position)
                    addTask.show((context as AppCompatActivity).supportFragmentManager, "addTask")
                }
                R.id.menuComplete -> {
                    val completeAlertDialog = AlertDialog.Builder(context)
                    completeAlertDialog.setTitle(R.string.dialog_title_confirm)
                        .setMessage(R.string.dialog_desc_config)
                        .setPositiveButton(R.string.dialog_option_yes) { _, _ ->
                            showCompleteDialog(eventID, position)
                        }
                        .setNegativeButton(R.string.dialog_option_no) { dialog, _ -> dialog.cancel() }.show()
                }
            }
            false
        }
        popupMenu.show()
    }

    private fun setEventsInfo(viewHolder: ViewHolder, position: Int) {
        val dateItems = getDate(dbTimestamp[position]).split(" ")
        viewHolder.eventId.text = dbID[position]
        viewHolder.eventDay.text = dateItems[0]
        viewHolder.eventDate.text = dateItems[1]
        viewHolder.eventMonth.text = dateItems[2]
        viewHolder.eventTitle.text = dbTitle[position]
        viewHolder.eventDesc.text = dbDesc[position]
        val timeText = dateItems[3] + ":" + dateItems[4]
        viewHolder.eventTime.text = timeText
    }

    private fun showCompleteDialog(eventID: Int, position: Int) {
        val dialog = Dialog(context!!, android.R.style.Theme_Light_NoTitleBar_Fullscreen)
        dialog.setContentView(R.layout.dialog_completed)
        dialog.findViewById<Button>(R.id.closeButton).setOnClickListener {
            db.delete(
                DBInfo.TABLE_NAME, BaseColumns._ID + "=?",
                arrayOf(eventID.toString())
            )
            notifyItemRemoved(position)
            dialog.dismiss()
        }
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    private fun getEventsInfo() {
        dbID = ArrayList()
        dbTitle = ArrayList()
        dbDesc = ArrayList()
        dbTimestamp = ArrayList()

        val cursor = db.query(
            DBInfo.TABLE_NAME, null, null, null,
            null, null, DBInfo.TABLE_COLUMN_TIMESTAMP + " ASC"
        )
        if (cursor.count > 0) {
            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
                dbID.add(cursor.getInt(0).toString())
                dbTitle.add(cursor.getString(1))
                dbDesc.add(cursor.getString(2))
                dbTimestamp.add(cursor.getString(3))
                cursor.moveToNext()
            }
        }
        cursor.close()
    }

    private fun getDate(time: String): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = time.toLong()
        return DateFormat.format("EE dd MMM HH mm", calendar).toString()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var eventId: TextView = view.findViewById(R.id.idEvent)
        var eventDay: TextView = view.findViewById(R.id.day)
        var eventDate: TextView = view.findViewById(R.id.date)
        var eventMonth: TextView = view.findViewById(R.id.month)
        var eventTitle: TextView = view.findViewById(R.id.title)
        var eventDesc: TextView = view.findViewById(R.id.description)
        var eventTime: TextView = view.findViewById(R.id.time)
        var options: ImageView = view.findViewById(R.id.options)
    }
}