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

/**
 * Klasa obsługująca dodawanie / aktualizowanie notatki
 *
 */
@Suppress("DEPRECATION")
class AddNote : AppCompatActivity() {
    private lateinit var addNoteTitle: EditText
    private lateinit var addNoteDesc: EditText
    private lateinit var addNotePicture: ImageView
    private lateinit var addPictureButton: FloatingActionButton
    private lateinit var addNoteButton: FloatingActionButton
    private var taskID: Int = -1

    /**
     * Funkcja wykonywana przy tworzeniu widoku
     *
     * @param savedInstanceState - uchwyt Bundle
     */
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
                    resources.getString(R.string.addNoteError_missing),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    /**
     * Zapisuje notatkę do bazy danych
     *
     * @param db - uchwyt bazy danych
     * @param value - wartości do zapisania
     */
    private fun addNote(db: SQLiteDatabase, value: ContentValues) {
        if (taskID == -1) {
            db.insertOrThrow(DBInfo.TABLE_NAME, null, value)
        } else {
            db.update(DBInfo.TABLE_NAME, value, BaseColumns._ID + "=?", arrayOf(taskID.toString()))
        }
        onBackPressed()
    }

    /**
     * Dodaje zdjęcie do pola ImageView
     *
     */
    private fun addPicture() {
        val pictureDialog = AlertDialog.Builder(this)
        pictureDialog.setTitle(resources.getString(R.string.addPicture_title))
        pictureDialog.setMessage(resources.getString(R.string.addPicture_desc))
        pictureDialog.setPositiveButton(resources.getString(R.string.addPicture_camera)) { _, _ ->
            ImagePicker.with(this).cameraOnly().crop().maxResultSize(1200, 1000).start()
        }
        pictureDialog.setNegativeButton(resources.getString(R.string.addPicture_gallery)) { _, _ ->
            ImagePicker.with(this).galleryOnly().crop().maxResultSize(1200, 1000).start()
        }
        pictureDialog.setNeutralButton(resources.getString(R.string.addPicture_cancel)) { dialog, _ ->
            dialog.cancel()
        }
        pictureDialog.create()
        pictureDialog.show()
    }

    /**
     * Usuwa zdjęcie z pola ImageView
     *
     */
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

    /**
     * Zwraca wartości, które mają zostać zapisane w bazie danych
     *
     * @param title - tytuł notatki
     * @param desc - opis notatki
     * @param picture - łańcuch Byte zdjęcia
     * @return wartości, które mają zostać zapisane w bazie danych
     */
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

    /**
     * Pobiera dane istniejącej już notatki z bazy danych
     *
     * @param db - uchwyt bazy danych
     */
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

    /**
     * Sprawdza czy wymagane dane zostały wprowadzone
     *
     * @param title - tytuł notatki
     * @param desc - opis notatki
     * @return true - wprowadzono wszystkie dane
     */
    private fun checkData(title: String, desc: String): Boolean {
        if (title.isNotEmpty() && desc.isNotEmpty()) {
            return true
        }
        return false
    }

    /**
     * Zamienia zdjęcie na łańcuch Byte
     *
     * @param imageView - zdjęcie ImageView
     * @return łańcuch byte zdjęcia
     */
    private fun imageToByteArray(imageView: ImageView): ByteArray {
        val bitmap = imageView.drawable.toBitmap()
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }

    /**
     * Zamienia łańcuch Byte na zdjęcie
     *
     * @param byteArray - łańcuch Byte zdjęcia
     */
    private fun byteArrayToImage(byteArray: ByteArray) {
        val bm = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        addNotePicture.setImageBitmap(bm)
    }

    /**
     * Przetwarza wybrane przez użytkownika zdjęcie
     *
     * @param requestCode - kod komunikatu wyboru zdjęcia
     * @param resultCode - kod rezultatu
     * @param data - dane zdjęcia
     */
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

    /**
     * Listener obsługujący wybór opcji z pola nawigacji
     *
     * @param item - menu item
     * @return true
     */
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