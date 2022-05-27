package com.life.planner.ui.notes

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.BaseColumns
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.life.planner.R
import java.io.ByteArrayOutputStream

@Suppress("DEPRECATION")
class AddNote : AppCompatActivity() {
    private lateinit var addNoteTitle: EditText
    private lateinit var addNoteDesc: EditText
    private lateinit var addNotePicture: ImageView
    private lateinit var addPictureButton: FloatingActionButton
    private lateinit var addNoteButton: FloatingActionButton
    private var taskID: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = resources.getString(R.string.addNoteTitle)
        setContentView(R.layout.fragment_addnote)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val dbHelper = NotesDBHelper(this)
        val db = dbHelper.writableDatabase

        addNoteTitle = findViewById(R.id.addNoteTitle)
        addNoteDesc = findViewById(R.id.addNotesDesc)
        addNotePicture = findViewById(R.id.addNotesPicture)
        addPictureButton = findViewById(R.id.addPicture)
        addNoteButton = findViewById(R.id.addNote)
        if (intent.hasExtra("ID")) {
            taskID = intent.getStringExtra("ID")!!.toInt()
        }
        getData(db)

        addNotePicture.setOnClickListener {
            Toast.makeText(this, resources.getString(R.string.addPicture_remove_click), Toast.LENGTH_SHORT).show()
        }
        addNotePicture.setOnLongClickListener {
            removePicture()
            true
        }
        addPictureButton.setOnClickListener {
            addPicture()
        }

        addNoteButton.setOnClickListener {
            if (checkData(
                    addNoteTitle.text.toString(),
                    addNoteDesc.text.toString(),
                )
            ) {
                addNote(
                    db,
                    createContentValue(
                        addNoteTitle.text.toString(),
                        addNoteDesc.text.toString(),
                        addNotePicture
                    )
                )
            } else {
                Toast.makeText(
                    this,
                    "Nie wprowadzono wszystkich danych",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun checkData(title: String, desc: String): Boolean {
        if (title.isNotEmpty() && desc.isNotEmpty()) {
            return true
        }
        return false
    }


    private fun addNote(db: SQLiteDatabase, value: ContentValues) {
        if (taskID == -1) {
            db.insertOrThrow(DBInfo.TABLE_NAME, null, value)
        } else {
            db.update(DBInfo.TABLE_NAME, value, BaseColumns._ID + "=?", arrayOf(taskID.toString()))
        }
        onBackPressed()
    }

    private fun createContentValue(
        title: String,
        desc: String,
        picture: ImageView,
    ): ContentValues {
        val contentValue = ContentValues()
        val updated : Long = System.currentTimeMillis()
        contentValue.put(DBInfo.TABLE_COLUMN_TITLE, title)
        contentValue.put(DBInfo.TABLE_COLUMN_DESC, desc)
        contentValue.put(DBInfo.TABLE_COLUMN_UPDATED, updated.toString())
        if (picture.drawable != null) {
            contentValue.put(DBInfo.TABLE_COLUMN_PICTURE, imageToByteArray(picture))
        }
        else
        {
            contentValue.putNull(DBInfo.TABLE_COLUMN_PICTURE)
        }
        return contentValue
    }

    private fun getData(db: SQLiteDatabase) {
        if (taskID != -1) {
            val cursor = db.rawQuery(
                "Select * FROM ${DBInfo.TABLE_NAME} WHERE ${BaseColumns._ID} = " +
                        taskID.toString(), null
            )
            if (cursor.count > 0) {
                cursor.moveToFirst()
                addNoteTitle.setText(cursor.getString(1))
                addNoteDesc.setText(cursor.getString(2))
                val byteArray = cursor.getBlob(4)
                if(byteArray != null)
                {
                    byteArrayToImage(byteArray)
                    addPictureButton.visibility = View.GONE
                    addNotePicture.visibility = View.VISIBLE
                }
            }
            cursor.close()
        }
    }

    private fun addPicture() {
        val pictureDialog = AlertDialog.Builder(this)
        pictureDialog.setTitle(resources.getString(R.string.addPicture_title))
        pictureDialog.setMessage(resources.getString(R.string.addPicture_desc))
        pictureDialog.setPositiveButton(resources.getString(R.string.addPicture_camera)) { _, _ ->
            ImagePicker.with(this).cameraOnly().crop().maxResultSize(2000, 2000).start()
        }
        pictureDialog.setNegativeButton(resources.getString(R.string.addPicture_gallery)) { _, _ ->
            ImagePicker.with(this).galleryOnly().crop().maxResultSize(2000, 2000).start()
        }
        pictureDialog.setNeutralButton(resources.getString(R.string.addPicture_cancel)) { dialog, _ ->
            dialog.cancel()
        }
        pictureDialog.create()
        pictureDialog.show()
    }

    private fun removePicture() {
        val pictureDialog = AlertDialog.Builder(this)
        pictureDialog.setTitle(resources.getString(R.string.addPicture_remove_title))
        pictureDialog.setMessage(resources.getString(R.string.addPicture_remove_desc))
        pictureDialog.setPositiveButton(resources.getString(R.string.addPicture_remove_yes)) { _, _ ->
            addNotePicture.setImageResource(0)
            addNotePicture.visibility = View.GONE
            addPictureButton.visibility = View.VISIBLE
        }
        pictureDialog.setNegativeButton(resources.getString(R.string.addPicture_remove_no)) { dialog, _ ->
            dialog.cancel()
        }
        pictureDialog.create()
        pictureDialog.show()
    }

    private fun imageToByteArray(imageView: ImageView): ByteArray {
        val bitmap = imageView.drawable.toBitmap()
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }

    private fun byteArrayToImage(byteArray: ByteArray) {
        val bm = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        addNotePicture.setImageBitmap(bm)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            ImagePicker.REQUEST_CODE -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        val stream = contentResolver.openInputStream(data?.data!!)
                        val bitmap = BitmapFactory.decodeStream(stream)
                        addNotePicture.setImageBitmap(bitmap)
                        addNotePicture.visibility = View.VISIBLE
                        addPictureButton.visibility = View.GONE
                    }
                }
            }
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