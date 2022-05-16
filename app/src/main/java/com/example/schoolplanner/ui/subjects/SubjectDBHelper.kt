package com.example.schoolplanner.ui.subjects

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

object DBInfo: BaseColumns {
    const val TABLE_NAME = "Subjects"
    const val TABLE_COLUMN_NAME = "Name"
    const val TABLE_COLUMN_SHORTCUT = "Shortcut"
    const val TABLE_COLUMN_TYPE = "Type"
    const val TABLE_COLUMN_LOCATION = "Location"
    const val TABLE_COLUMN_TEACHER = "Teacher"
    const val TABLE_COLUMN_NOTE = "Note"
}

object BasicCommands {
    const val SQL_CREATE_SUBJECT_TABLE: String =
        "CREATE TABLE ${DBInfo.TABLE_NAME} (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                "${DBInfo.TABLE_COLUMN_NAME} TEXT NOT NULL," +
                "${DBInfo.TABLE_COLUMN_SHORTCUT} TEXT NOT NULL," +
                "${DBInfo.TABLE_COLUMN_TYPE} TEXT NOT NULL," +
                "${DBInfo.TABLE_COLUMN_LOCATION} TEXT NOT NULL," +
                "${DBInfo.TABLE_COLUMN_TEACHER} TEXT NOT NULL," +
                "${DBInfo.TABLE_COLUMN_NOTE} TEXT NOT NULL)"
    const val SQL_DELETE_SUBJECT_TABLE = "DROP TABLE IF EXISTS ${DBInfo.TABLE_NAME}"
}

class SubjectDBHelper(context: Context) : SQLiteOpenHelper(context, DBInfo.TABLE_NAME, null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(BasicCommands.SQL_CREATE_SUBJECT_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(BasicCommands.SQL_DELETE_SUBJECT_TABLE)
        onCreate(db)
    }
}