package com.example.schoolplanner.ui.teachers

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

object DBInfo: BaseColumns {
    const val TABLE_NAME = "Teachers"
    const val TABLE_COLUMN_NAME = "Name"
    const val TABLE_COLUMN_SURNAME = "Surname"
    const val TABLE_COLUMN_DEGREE = "Degree"
    const val TABLE_COLUMN_EMAIL = "Email"
    const val TABLE_COLUMN_PHONE = "Phone"
    const val TABLE_COLUMN_LOCATION = "Location"
    const val TABLE_COLUMN_NOTE = "Note"
}

object BasicCommands {
    const val SQL_CREATE_SUBJECT_TABLE: String =
        "CREATE TABLE ${DBInfo.TABLE_NAME} (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                "${DBInfo.TABLE_COLUMN_NAME} TEXT NOT NULL," +
                "${DBInfo.TABLE_COLUMN_SURNAME} TEXT NOT NULL," +
                "${DBInfo.TABLE_COLUMN_DEGREE} TEXT NOT NULL," +
                "${DBInfo.TABLE_COLUMN_EMAIL} TEXT NOT NULL," +
                "${DBInfo.TABLE_COLUMN_PHONE} TEXT NOT NULL," +
                "${DBInfo.TABLE_COLUMN_LOCATION} INTEGER," +
                "${DBInfo.TABLE_COLUMN_NOTE} TEXT NOT NULL)"
    const val SQL_DELETE_SUBJECT_TABLE = "DROP TABLE IF EXISTS ${DBInfo.TABLE_NAME}"
}

class TeacherDBHelper(context: Context) : SQLiteOpenHelper(context, DBInfo.TABLE_NAME, null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(BasicCommands.SQL_CREATE_SUBJECT_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(BasicCommands.SQL_DELETE_SUBJECT_TABLE)
        onCreate(db)
    }
}