package com.example.schoolplanner.ui.teachers

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

class AddTeacherRecyclerAdapter(private val db: SQLiteDatabase) : RecyclerView.Adapter<AddTeacherRecyclerAdapter.ViewHolder>() {
    private lateinit var dbID : ArrayList<String>
    private lateinit var dbName : ArrayList<String>
    private lateinit var dbDegree : ArrayList<String>

    override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(viewGroup.context)
        val cardView = layoutInflater.inflate(R.layout.cardview_teacher, viewGroup, false)
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
        getTeacherInfo()
        setTeacherInfo(viewHolder, position)

        viewHolder.itemView.findViewById<CardView>(R.id.teacher_CardView).setOnClickListener {
            val intent = Intent(context, AddTeacher::class.java)
            intent.putExtra("ID", viewHolder.teacherID.text.toString())
            context.startActivity(intent)
        }
    }

    private fun getTeacherInfo() {
        dbID = ArrayList()
        dbName = ArrayList()
        dbDegree = ArrayList()
        val cursor = db.query(
            DBInfo.TABLE_NAME, null, null, null,
            null, null, null)
        if(cursor.count > 0) {
            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
                dbID.add(cursor.getInt(0).toString())
                dbName.add(cursor.getString(1) + " " + cursor.getString(2))
                dbDegree.add(cursor.getString(3))
                cursor.moveToNext()
            }
        }
        cursor.close()
    }

    private fun setTeacherInfo(viewHolder: ViewHolder, position: Int) {
        viewHolder.teacherID.text = dbID[position]
        viewHolder.teacherName.text = dbName[position]
        viewHolder.teacherDegree.text = dbDegree[position]
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var teacherID: TextView = view.findViewById(R.id.teacherID_Card)
        var teacherName: TextView = view.findViewById(R.id.teacherNameAndSurname_Card)
        var teacherDegree: TextView = view.findViewById(R.id.teacherDegree_Card)
    }
}
