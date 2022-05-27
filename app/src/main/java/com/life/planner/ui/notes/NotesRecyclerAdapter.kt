package com.life.planner.ui.notes

import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.life.planner.R
import java.util.*

class NotesRecyclerAdapter(private val db: SQLiteDatabase) :
    RecyclerView.Adapter<NotesRecyclerAdapter.ViewHolder>() {
    private lateinit var dbID: ArrayList<String>
    private lateinit var dbTitle: ArrayList<String>
    private lateinit var dbDesc: ArrayList<String>
    private lateinit var dbUpdated: ArrayList<String>
    private var context: Context? = null

    override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): ViewHolder {
        context = viewGroup.context
        val layoutInflater = LayoutInflater.from(context)
        val cardView = layoutInflater.inflate(R.layout.cardview_notes, viewGroup, false)
        return ViewHolder(cardView)
    }

    override fun getItemCount(): Int {
        val cursor = db.query(DBInfo.TABLE_NAME, null, null, null, null, null, null)
        val cursorCount = cursor.count
        cursor.close()
        return cursorCount
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        getNotesInfo()
        setNotesInfo(viewHolder, position)
        viewHolder.itemView.findViewById<CardView>(R.id.notesCardView).setOnClickListener {
            val intent = Intent(context, AddNote::class.java)
            intent.putExtra("ID", viewHolder.notesId.text.toString())
            context!!.startActivity(intent)
        }
    }

    private fun setNotesInfo(viewHolder: ViewHolder, position: Int) {
        viewHolder.notesId.text = dbID[position]
        viewHolder.notesTitle.text = dbTitle[position]
        viewHolder.notesDesc.text = dbDesc[position]
        viewHolder.notesUpdated.text = getDate(dbUpdated[position])
    }

    private fun getNotesInfo() {
        dbID = ArrayList()
        dbTitle = ArrayList()
        dbDesc = ArrayList()
        dbUpdated = ArrayList()

        val cursor = db.query(
            DBInfo.TABLE_NAME, null, null, null,
            null, null, DBInfo.TABLE_COLUMN_UPDATED + " DESC"
        )
        if (cursor.count > 0) {
            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
                dbID.add(cursor.getInt(0).toString())
                dbTitle.add(cursor.getString(1))
                dbDesc.add(cursor.getString(2))
                dbUpdated.add(cursor.getString(3))
                cursor.moveToNext()
            }
        }
        cursor.close()
    }

    private fun getDate(time: String): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = time.toLong()
        return DateFormat.format("dd.MM.yyyy HH:mm:ss", calendar).toString()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var notesId: TextView = view.findViewById(R.id.idNote)
        var notesTitle: TextView = view.findViewById(R.id.noteTitle)
        var notesDesc: TextView = view.findViewById(R.id.noteDesc)
        var notesUpdated: TextView = view.findViewById(R.id.noteUpdated)
    }
}