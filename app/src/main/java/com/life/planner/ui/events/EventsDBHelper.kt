package com.life.planner.ui.events

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

object DBInfo : BaseColumns {
    const val TABLE_NAME = "Events"
    const val TABLE_COLUMN_TITLE = "Title"
    const val TABLE_COLUMN_DESC = "Description"
    const val TABLE_COLUMN_TIMESTAMP = "Timestamp"
}

object BasicCommands {
    const val SQL_CREATE_TABLE: String =
        "CREATE TABLE ${DBInfo.TABLE_NAME} (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                "${DBInfo.TABLE_COLUMN_TITLE} TEXT NOT NULL," +
                "${DBInfo.TABLE_COLUMN_DESC} TEXT NOT NULL," +
                "${DBInfo.TABLE_COLUMN_TIMESTAMP} TEXT NOT NULL)"
    const val SQL_DELETE_TABLE = "DROP TABLE IF EXISTS ${DBInfo.TABLE_NAME}"
}

class EventsDBHelper(context: Context) : SQLiteOpenHelper(context, DBInfo.TABLE_NAME, null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(BasicCommands.SQL_CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(BasicCommands.SQL_DELETE_TABLE)
        onCreate(db)
    }
}