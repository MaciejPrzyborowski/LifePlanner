package com.example.schoolplanner.ui.teachers

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.BaseColumns
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.widget.Toast.*
import com.example.schoolplanner.R

class AddTeacher : AppCompatActivity() {
    private lateinit var nameEditText : EditText
    private lateinit var surnameEditText : EditText
    private lateinit var degreeEditText : EditText
    private lateinit var emailEditText : EditText
    private lateinit var phoneEditText : EditText
    private lateinit var locationEditText : EditText
    private lateinit var noteEditText : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_teacher)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val dbHelper = TeacherDBHelper(applicationContext)
        val db = dbHelper.writableDatabase

        nameEditText = findViewById(R.id.teacherName)
        surnameEditText = findViewById(R.id.teacherSurname)
        degreeEditText = findViewById(R.id.teacherDegree)
        emailEditText = findViewById(R.id.teacherEmail)
        phoneEditText = findViewById(R.id.teacherPhone)
        locationEditText = findViewById(R.id.teacherLocation)
        noteEditText = findViewById(R.id.teacherNote)
        setVisibilityButton()
        getData(db)

        findViewById<Button>(R.id.addTeacherButton).setOnClickListener {
            if (!intent.hasExtra("ID") && checkData()) {
                val contentValue = createContentValue()
                addTeacher(db, contentValue)
                makeText(applicationContext, R.string.teacherAdded_toast, LENGTH_SHORT).show()
                onBackPressed()
            }
            else {
                makeText(applicationContext, R.string.addTeacherError_missing, LENGTH_SHORT).show()
            }
        }
        findViewById<Button>(R.id.editTeacherButton).setOnClickListener {
            if (intent.hasExtra("ID") && checkData()) {
                val contentValue = createContentValue()
                updateTeacher(db, contentValue)
                makeText(applicationContext, R.string.teacherEdited_toast, LENGTH_SHORT).show()
                onBackPressed()
            }
            else {
                makeText(applicationContext, R.string.addTeacherError_missing, LENGTH_SHORT).show()
            }
        }
        findViewById<Button>(R.id.removeTeacherButton).setOnClickListener {
            removeTeacher(db)
            makeText(applicationContext, R.string.teacherRemoved_toast, LENGTH_SHORT).show()
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

    private fun addTeacher(db: SQLiteDatabase, value : ContentValues) {
        db.insertOrThrow(DBInfo.TABLE_NAME, null, value)
    }

    private fun updateTeacher(db: SQLiteDatabase, value : ContentValues) {
        db.update(
            DBInfo.TABLE_NAME, value, BaseColumns._ID + "=?",
            arrayOf(intent.getStringExtra("ID")))
    }

    private fun removeTeacher(db: SQLiteDatabase) {
        db.delete(
            DBInfo.TABLE_NAME, BaseColumns._ID + "=?",
            arrayOf(intent.getStringExtra("ID")))
    }

    private fun setVisibilityButton() {
        if(intent.hasExtra("ID")) {
            findViewById<Button>(R.id.addTeacherButton).visibility = View.GONE
            findViewById<Button>(R.id.editTeacherButton).visibility = View.VISIBLE
            findViewById<Button>(R.id.removeTeacherButton).visibility = View.VISIBLE
        }
        else {
            findViewById<Button>(R.id.addTeacherButton).visibility = View.VISIBLE
            findViewById<Button>(R.id.editTeacherButton).visibility = View.GONE
            findViewById<Button>(R.id.removeTeacherButton).visibility = View.GONE
        }
    }

    private fun getData(db : SQLiteDatabase) {
        if(intent.hasExtra("ID")) {
            val cursor = db.rawQuery(
                "Select * FROM ${DBInfo.TABLE_NAME} WHERE ${BaseColumns._ID} = "+
                        intent.getStringExtra("ID"), null
            )
            if(cursor.count > 0) {
                cursor.moveToFirst()
                nameEditText.setText(cursor.getString(1).toString())
                surnameEditText.setText(cursor.getString(2).toString())
                degreeEditText.setText(cursor.getString(3).toString())
                emailEditText.setText(cursor.getString(4).toString())
                phoneEditText.setText(cursor.getString(5).toString())
                locationEditText.setText(cursor.getString(6).toString())
                noteEditText.setText(cursor.getString(7).toString())
            }
            cursor.close()
        }
    }

    private fun createContentValue() : ContentValues {
        val contentValue = ContentValues()
        contentValue.put(DBInfo.TABLE_COLUMN_NAME, nameEditText.text.toString())
        contentValue.put(DBInfo.TABLE_COLUMN_SURNAME, surnameEditText.text.toString())
        contentValue.put(DBInfo.TABLE_COLUMN_DEGREE, degreeEditText.text.toString())
        contentValue.put(DBInfo.TABLE_COLUMN_EMAIL, emailEditText.text.toString())
        contentValue.put(DBInfo.TABLE_COLUMN_PHONE, phoneEditText.text.toString())
        contentValue.put(DBInfo.TABLE_COLUMN_LOCATION, locationEditText.text.toString())
        contentValue.put(DBInfo.TABLE_COLUMN_NOTE, noteEditText.text.toString())
        return contentValue
    }

    private fun checkData() : Boolean {
        if(nameEditText.text.toString().isNotEmpty() && surnameEditText.text.toString().isNotEmpty() &&
            degreeEditText.text.toString().isNotEmpty()) {
            return true
        }
        return false
    }
}