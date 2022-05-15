package com.example.schoolplanner.ui.subjects

import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.schoolplanner.R
import com.example.schoolplanner.Subject_DBInfo

class AddSubjectRecyclerAdapter(context: Context, private val db: SQLiteDatabase) : RecyclerView.Adapter<AddSubjectRecyclerAdapter.ViewHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(viewGroup.context)
        val cardView = layoutInflater.inflate(R.layout.cardview_subject, viewGroup, false)
        return ViewHolder(cardView)
    }

    override fun getItemCount(): Int {
        val cursor = db.query(Subject_DBInfo.TABLE_NAME, null,null, null,null, null, null)
        val cursorCount = cursor.count
        cursor.close()
        return cursorCount
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val context : Context = viewHolder.itemView.context
        val cursor = db.query(Subject_DBInfo.TABLE_NAME, null,
            BaseColumns._ID + "=?", arrayOf(viewHolder.adapterPosition.plus(1).toString()),
            null, null, null)
        if(cursor.moveToFirst()) {
            viewHolder.subjectID.text = cursor.getInt(0).toString()
            if(cursor.getString(2).isNullOrEmpty()) {
                viewHolder.subjectName.text = cursor.getString(1)
            }
            else {
                viewHolder.subjectName.text = cursor.getString(2)
            }
            viewHolder.subjectType.text = cursor.getString(3)
            viewHolder.subjectLocation.text = cursor.getString(4)
            viewHolder.subjectTeacher.text = cursor.getString(5)

            viewHolder.itemView.findViewById<CardView>(R.id.cardView).setOnClickListener {
                val intent = Intent(context, AddSubject::class.java)
                intent.putExtra("ID", viewHolder.subjectID.text.toString())
                context.startActivity(intent)
            }
        }
        cursor.close()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var subjectName: TextView = view.findViewById(R.id.subjectName_Card)
        var subjectType: TextView = view.findViewById(R.id.subjectType_Card)
        var subjectTeacher: TextView = view.findViewById(R.id.subjectTeacher_Card)
        var subjectLocation: TextView = view.findViewById(R.id.subjectLocation_Card)
        var subjectID: TextView = view.findViewById(R.id.subjectID_Card)

    }
}

