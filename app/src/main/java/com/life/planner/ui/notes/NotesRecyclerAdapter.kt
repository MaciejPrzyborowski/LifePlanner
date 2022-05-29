package com.life.planner.ui.notes

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.life.planner.R
import java.util.*

/**
 * Obsługuje RecyplerView.Adapter dla klasy Notes
 *
 * @property db - uchwyt bazy danych
 */
class NotesRecyclerAdapter(private val db: SQLiteDatabase) :
    RecyclerView.Adapter<NotesRecyclerAdapter.ViewHolder>() {
    private lateinit var dbID: ArrayList<String>
    private lateinit var dbTitle: ArrayList<String>
    private lateinit var dbDesc: ArrayList<String>
    private lateinit var dbUpdated: ArrayList<String>
    private var context: Context? = null

    /**
     * Funkcja wykonywana przy tworzeniu widoku
     *
     * @param viewGroup - uchwyt grupy widoków
     * @param position - pozycja na liście
     * @return widok adaptera
     */
    override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): ViewHolder {
        context = viewGroup.context
        val layoutInflater = LayoutInflater.from(context)
        val cardView = layoutInflater.inflate(R.layout.cardview_notes, viewGroup, false)
        return ViewHolder(cardView)
    }

    /**
     * Zlicza ilość elementów do wyświetlenia na liście
     *
     * @return ilość elementów do wyświetlenia na liście
     */
    override fun getItemCount(): Int {
        val cursor = db.query(DBInfo.TABLE_NAME, null, null, null, null, null, null)
        val cursorCount = cursor.count
        cursor.close()
        return cursorCount
    }

    /**
     * Zarządza elementami ViewHoldera
     *
     * @param viewHolder - uchwyt recyclerView
     * @param position - pozycja na liście
     */
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        getNotesInfo()
        setNotesInfo(viewHolder, position)
        viewHolder.itemView.findViewById<CardView>(R.id.notesCardView).setOnClickListener {
            val intent = Intent(context, AddNote::class.java)
            intent.putExtra("ID", viewHolder.notesId.text.toString())
            context!!.startActivity(intent)
        }
        viewHolder.itemView.findViewById<CardView>(R.id.notesCardView).setOnLongClickListener {
            removeNote(viewHolder.notesId.text.toString().toInt(), position)
            true
        }
    }

    /**
     * Wyświetla komunikat potwierdzający usunięcie zadania
     *
     * @param noteId - identyfikator notatki
     * @param position - pozycja na liście
     */
    private fun removeNote(noteId : Int, position: Int)
    {
        val removeAlertDialog = AlertDialog.Builder(context)
        removeAlertDialog.setTitle(R.string.removeNote_confirm_title)
            .setMessage(R.string.removeNote_confirm_desc)
            .setPositiveButton(R.string.removeNote_confirm_yes) { _, _ ->
                db.delete(
                    DBInfo.TABLE_NAME, BaseColumns._ID + "=?",
                    arrayOf(noteId.toString())
                )
                notifyItemRemoved(position)
            }
            .setNegativeButton(R.string.removeNote_confirm_no) { dialog, _ -> dialog.cancel() }.show()
    }

    /**
     * Ustawia informacje o notatkach
     *
     * @param viewHolder - uchwyt recyclerView
     * @param position - pozycja na liście
     */
    private fun setNotesInfo(viewHolder: ViewHolder, position: Int) {
        viewHolder.notesId.text = dbID[position]
        viewHolder.notesTitle.text = dbTitle[position]
        viewHolder.notesDesc.text = dbDesc[position]
        viewHolder.notesUpdated.text = getDate(dbUpdated[position])
    }

    /**
     * Pobiera informacje o notatkach
     *
     */
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

    /**
     * Formatuje datę na podstawie podanego czasu w formacie Unix Timestamp
     *
     * @param time - czas wyrażony w formacie Unix Timestamp
     * @return sformatowana data
     */
    private fun getDate(time: String): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = time.toLong()
        return DateFormat.format("dd.MM.yyyy HH:mm:ss", calendar).toString()
    }

    /**
     * ViewHolder klasy RecyclerView
     *
     * @param view - uchwyt widoku
     */
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var notesId: TextView = view.findViewById(R.id.idNote)
        var notesTitle: TextView = view.findViewById(R.id.noteTitle)
        var notesDesc: TextView = view.findViewById(R.id.noteDesc)
        var notesUpdated: TextView = view.findViewById(R.id.noteUpdated)
    }
}