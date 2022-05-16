package com.example.schoolplanner.ui.subjects

import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.schoolplanner.R

class AddSubjectRecyclerAdapter(private val db: SQLiteDatabase) : RecyclerView.Adapter<AddSubjectRecyclerAdapter.ViewHolder>() {
    private lateinit var dbID : ArrayList<String>
    private lateinit var dbsubjectName : ArrayList<String>
    private lateinit var dbsubjectType : ArrayList<String>
    private lateinit var dbsubjectLocation : ArrayList<String>
    private lateinit var dbsubjectTeacher : ArrayList<String>

    override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(viewGroup.context)
        val cardView = layoutInflater.inflate(R.layout.cardview_subject, viewGroup, false)
        return ViewHolder(cardView)
    }

    override fun getItemCount(): Int {
        val cursor = db.query(DBInfo.TABLE_NAME, null,null, null,null, null, null)
        val cursorCount = cursor.count
        cursor.close()
        return cursorCount
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val context : Context = viewHolder.itemView.context
        getSubjectInfo()
        setSubjectInfo(viewHolder, position)

        viewHolder.itemView.findViewById<CardView>(R.id.cardView).setOnClickListener {
            val intent = Intent(context, AddSubject::class.java)
            intent.putExtra("ID", viewHolder.subjectID.text.toString())
            context.startActivity(intent)
        }
    }

    private fun getSubjectInfo() {
        dbID = ArrayList()
        dbsubjectName = ArrayList()
        dbsubjectType = ArrayList()
        dbsubjectLocation = ArrayList()
        dbsubjectTeacher = ArrayList()
        val cursor = db.query(
            DBInfo.TABLE_NAME, null, null, null,
            null, null, null)
        if(cursor.count > 0) {
            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
                dbID.add(cursor.getInt(0).toString())
                if(cursor.getString(2).isNullOrEmpty()) {
                    dbsubjectName.add(cursor.getString(1))
                }
                else {
                    dbsubjectName.add(cursor.getString(2))
                }
                dbsubjectType.add(cursor.getString(3))
                dbsubjectLocation.add(cursor.getString(4))
                dbsubjectTeacher.add(cursor.getString(5))
                cursor.moveToNext()
            }
        }
        cursor.close()
    }

    private fun setSubjectInfo(viewHolder: ViewHolder, position: Int) {
        viewHolder.subjectID.text = dbID[position]
        viewHolder.subjectName.text = dbsubjectName[position]
        viewHolder.subjectType.text = dbsubjectType[position]
        viewHolder.subjectLocation.text = dbsubjectLocation[position]
        viewHolder.subjectTeacher.text = dbsubjectTeacher[position]
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var subjectID: TextView = view.findViewById(R.id.subjectID_Card)
        var subjectName: TextView = view.findViewById(R.id.subjectName_Card)
        var subjectType: TextView = view.findViewById(R.id.subjectType_Card)
        var subjectTeacher: TextView = view.findViewById(R.id.subjectTeacher_Card)
        var subjectLocation: TextView = view.findViewById(R.id.subjectLocation_Card)
    }
}

