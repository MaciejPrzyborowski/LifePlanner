package com.example.schoolplanner.ui.subjects

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.provider.BaseColumns
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.schoolplanner.DBHelper
import com.example.schoolplanner.R
import com.example.schoolplanner.Subject_DBInfo

class AddSubject : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_subject)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val dbHelper = DBHelper(applicationContext)
        val db = dbHelper.writableDatabase

        val nameEditText = findViewById<EditText>(R.id.subjectName)
        val shortcutEditText = findViewById<EditText>(R.id.subjectShortcut)
        val typeEditText = findViewById<EditText>(R.id.subjectType)
        val locationEditText = findViewById<EditText>(R.id.subjectLocation)
        val teacherEditText = findViewById<EditText>(R.id.subjectTeacher)
        val noteEditText = findViewById<EditText>(R.id.subjectNote)

        if(intent.hasExtra("ID")) {
            findViewById<Button>(R.id.addSubjectButton).visibility = View.GONE
            findViewById<Button>(R.id.editSubjectButton).visibility = View.VISIBLE
            findViewById<Button>(R.id.removeSubjectButton).visibility = View.VISIBLE

            val cursor = db.rawQuery(
                "Select * FROM ${Subject_DBInfo.TABLE_NAME} WHERE ${BaseColumns._ID} = "+
                        intent.getStringExtra("ID"), null
            )
            if(cursor.count > 0) {
                cursor.moveToFirst()
                nameEditText.setText(cursor.getString(1).toString())
                shortcutEditText.setText(cursor.getString(2).toString())
                typeEditText.setText(cursor.getString(3).toString())
                locationEditText.setText(cursor.getString(4).toString())
                teacherEditText.setText(cursor.getString(5).toString())
                noteEditText.setText(cursor.getString(6).toString())
            }
        }

        findViewById<Button>(R.id.addSubjectButton).setOnClickListener {
            if (!intent.hasExtra("ID") && checkDataSubject(
                    nameEditText.text.toString(),
                    typeEditText.text.toString(), teacherEditText.text.toString()
                )
            ) {
                val contentValue = createContentValue(
                    nameEditText.text.toString(),
                    shortcutEditText.text.toString(), typeEditText.text.toString(),
                    locationEditText.text.toString(), teacherEditText.text.toString(),
                    noteEditText.text.toString()
                )
                addSubject(db, contentValue)
                Toast.makeText(applicationContext, R.string.subjectAdded_toast, Toast.LENGTH_SHORT).show()
                onBackPressed()
            }
            else {
                Toast.makeText(applicationContext, R.string.addSubjectError_missing, Toast.LENGTH_SHORT).show()
            }
        }
        findViewById<Button>(R.id.editSubjectButton).setOnClickListener {
            if (intent.hasExtra("ID") && checkDataSubject(
                    nameEditText.text.toString(),
                    typeEditText.text.toString(), teacherEditText.text.toString()
                )
            ) {
                val contentValue = createContentValue(
                    nameEditText.text.toString(),
                    shortcutEditText.text.toString(), typeEditText.text.toString(),
                    locationEditText.text.toString(), teacherEditText.text.toString(),
                    noteEditText.text.toString()
                )
                updateSubject(db, contentValue)
                Toast.makeText(applicationContext, R.string.subjectEdited_toast, Toast.LENGTH_SHORT).show()
                onBackPressed()
            }
            else {
                Toast.makeText(applicationContext, R.string.addSubjectError_missing, Toast.LENGTH_SHORT).show()
            }
        }
        findViewById<Button>(R.id.removeSubjectButton).setOnClickListener {
            removeSubject(db, intent.hasExtra("ID").toString())
            Toast.makeText(applicationContext, R.string.subjectRemoved_toast, Toast.LENGTH_SHORT).show()
            onBackPressed()
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun addSubject(db: SQLiteDatabase, value : ContentValues) {
        db.insertOrThrow(Subject_DBInfo.TABLE_NAME, null, value)
    }

    private fun updateSubject(db: SQLiteDatabase, value : ContentValues) {
        db.update(Subject_DBInfo.TABLE_NAME, value, BaseColumns._ID + "=?",
            arrayOf(intent.getStringExtra("ID")))
    }

    private fun removeSubject(db: SQLiteDatabase, ID : String) {
        db.delete(Subject_DBInfo.TABLE_NAME, BaseColumns._ID + "=?",
            arrayOf(intent.getStringExtra("ID")))
    }

    private fun createContentValue(name : String, shortcut : String, type : String,
                                   location: String, teacher : String, note : String) : ContentValues {
        val contentValue = ContentValues()
        contentValue.put(Subject_DBInfo.TABLE_COLUMN_NAME, name)
        contentValue.put(Subject_DBInfo.TABLE_COLUMN_SHORTCUT, shortcut)
        contentValue.put(Subject_DBInfo.TABLE_COLUMN_TYPE, type)
        contentValue.put(Subject_DBInfo.TABLE_COLUMN_LOCATION, location)
        contentValue.put(Subject_DBInfo.TABLE_COLUMN_TEACHER, teacher)
        contentValue.put(Subject_DBInfo.TABLE_COLUMN_NOTE, note)
        return contentValue
    }

    private fun checkDataSubject(name : String, type : String, teacher: String) : Boolean {
        if(!name.isNullOrEmpty() && !type.isNullOrEmpty() && !teacher.isNullOrEmpty()) {
            return true
        }
        return false
    }
}