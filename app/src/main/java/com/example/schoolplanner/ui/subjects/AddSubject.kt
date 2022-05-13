package com.example.schoolplanner.ui.subjects

import android.content.ContentValues
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.schoolplanner.DBHelper
import com.example.schoolplanner.R
import com.example.schoolplanner.Subject_DBInfo
import com.example.schoolplanner.databinding.ActivityAddSubjectBinding

class AddSubject : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityAddSubjectBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_add_subject)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val dbHelper = DBHelper(applicationContext)
        val db = dbHelper.writableDatabase

        binding.addSubjectButton.setOnClickListener {
            Toast.makeText(this, "Przedmiot został dodany", Toast.LENGTH_SHORT).show()

            val name = binding.subjectName.text.toString()
            val shortcut = binding.subjectShortcut.text.toString()
            val type = binding.subjectType.text.toString()
            val location = binding.subjectLocation.text.toString()
            val teacher = binding.subjectTeacher.text.toString()
            val note = binding.subjectNotes.text.toString()

            val value = ContentValues()
            value.put("name", name)
            value.put("shortcut", shortcut)
            value.put("type", type)
            value.put("location", location)
            value.put("teacher", teacher)
            value.put("note", note)

            db.insertOrThrow(Subject_DBInfo.TABLE_NAME, null, value)

            Toast.makeText(applicationContext, "Przedmiot został dodany", Toast.LENGTH_SHORT).show()
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
}