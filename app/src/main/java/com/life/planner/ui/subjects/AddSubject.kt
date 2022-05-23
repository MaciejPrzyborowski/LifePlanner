package com.life.planner.ui.subjects

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.provider.BaseColumns
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.life.planner.R

class AddSubject : AppCompatActivity() {
    private lateinit var nameEditText: EditText
    private lateinit var shortcutEditText: EditText
    private lateinit var typeEditText: EditText
    private lateinit var locationEditText: EditText
    private lateinit var teacherEditText: EditText
    private lateinit var noteEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_subject)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val dbHelper = SubjectDBHelper(applicationContext)
        val db = dbHelper.writableDatabase

        nameEditText = findViewById(R.id.subjectName)
        shortcutEditText = findViewById(R.id.subjectShortcut)
        typeEditText = findViewById(R.id.subjectType)
        locationEditText = findViewById(R.id.subjectLocation)
        teacherEditText = findViewById(R.id.subjectTeacher)
        noteEditText = findViewById(R.id.subjectNote)
        setVisibilityButton()
        getData(db)

        findViewById<Button>(R.id.addSubjectButton).setOnClickListener {
            if (!intent.hasExtra("ID") && checkData()) {
                val contentValue = createContentValue()
                addSubject(db, contentValue)
                Toast.makeText(applicationContext, R.string.subjectAdded_toast, Toast.LENGTH_SHORT)
                    .show()
                onBackPressed()
            } else {
                Toast.makeText(
                    applicationContext,
                    R.string.addSubjectError_missing,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        findViewById<Button>(R.id.editSubjectButton).setOnClickListener {
            if (intent.hasExtra("ID") && checkData()) {
                val contentValue = createContentValue()
                updateSubject(db, contentValue)
                Toast.makeText(applicationContext, R.string.subjectEdited_toast, Toast.LENGTH_SHORT)
                    .show()
                onBackPressed()
            } else {
                Toast.makeText(
                    applicationContext,
                    R.string.addSubjectError_missing,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        findViewById<Button>(R.id.removeSubjectButton).setOnClickListener {
            removeSubject(db)
            Toast.makeText(applicationContext, R.string.subjectRemoved_toast, Toast.LENGTH_SHORT)
                .show()
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

    private fun addSubject(db: SQLiteDatabase, value: ContentValues) {
        db.insertOrThrow(DBInfo.TABLE_NAME, null, value)
    }

    private fun updateSubject(db: SQLiteDatabase, value: ContentValues) {
        db.update(
            DBInfo.TABLE_NAME, value, BaseColumns._ID + "=?",
            arrayOf(intent.getStringExtra("ID"))
        )
    }

    private fun removeSubject(db: SQLiteDatabase) {
        db.delete(
            DBInfo.TABLE_NAME, BaseColumns._ID + "=?",
            arrayOf(intent.getStringExtra("ID"))
        )
    }

    private fun setVisibilityButton() {
        if (intent.hasExtra("ID")) {
            findViewById<Button>(R.id.addSubjectButton).visibility = View.GONE
            findViewById<Button>(R.id.editSubjectButton).visibility = View.VISIBLE
            findViewById<Button>(R.id.removeSubjectButton).visibility = View.VISIBLE
        } else {
            findViewById<Button>(R.id.addSubjectButton).visibility = View.VISIBLE
            findViewById<Button>(R.id.editSubjectButton).visibility = View.GONE
            findViewById<Button>(R.id.removeSubjectButton).visibility = View.GONE
        }
    }

    private fun getData(db: SQLiteDatabase) {
        if (intent.hasExtra("ID")) {
            val cursor = db.rawQuery(
                "Select * FROM ${DBInfo.TABLE_NAME} WHERE ${BaseColumns._ID} = " +
                        intent.getStringExtra("ID"), null
            )
            if (cursor.count > 0) {
                cursor.moveToFirst()
                nameEditText.setText(cursor.getString(1).toString())
                shortcutEditText.setText(cursor.getString(2).toString())
                typeEditText.setText(cursor.getString(3).toString())
                locationEditText.setText(cursor.getString(4).toString())
                teacherEditText.setText(cursor.getString(5).toString())
                noteEditText.setText(cursor.getString(6).toString())
            }
            cursor.close()
        }
    }

    private fun createContentValue(): ContentValues {
        val contentValue = ContentValues()
        contentValue.put(DBInfo.TABLE_COLUMN_NAME, nameEditText.text.toString())
        contentValue.put(DBInfo.TABLE_COLUMN_SHORTCUT, shortcutEditText.text.toString())
        contentValue.put(DBInfo.TABLE_COLUMN_TYPE, typeEditText.text.toString())
        contentValue.put(DBInfo.TABLE_COLUMN_LOCATION, locationEditText.text.toString())
        contentValue.put(DBInfo.TABLE_COLUMN_TEACHER, teacherEditText.text.toString())
        contentValue.put(DBInfo.TABLE_COLUMN_NOTE, noteEditText.text.toString())
        return contentValue
    }

    private fun checkData(): Boolean {
        if (nameEditText.text.toString().isNotEmpty() && typeEditText.text.toString()
                .isNotEmpty() &&
            teacherEditText.text.toString().isNotEmpty()
        ) {
            return true
        }
        return false
    }
}