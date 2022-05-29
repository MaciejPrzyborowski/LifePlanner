package com.life.planner.ui.events

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

/**
 * Informacje o kolumnach bazy danych [DBInfo.TABLE_NAME]
 */
object DBInfo : BaseColumns {
    const val TABLE_NAME = "Events"
    const val TABLE_COLUMN_TITLE = "Title"
    const val TABLE_COLUMN_DESC = "Description"
    const val TABLE_COLUMN_TIMESTAMP = "Timestamp"
}

/**
 * Podstawowe komendy do tworzenia i usuwania bazy danych
 */
object BasicCommands {
    const val SQL_CREATE_TABLE: String =
        "CREATE TABLE ${DBInfo.TABLE_NAME} (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                "${DBInfo.TABLE_COLUMN_TITLE} TEXT NOT NULL," +
                "${DBInfo.TABLE_COLUMN_DESC} TEXT NOT NULL," +
                "${DBInfo.TABLE_COLUMN_TIMESTAMP} TEXT NOT NULL)"
    const val SQL_DELETE_TABLE = "DROP TABLE IF EXISTS ${DBInfo.TABLE_NAME}"
}

/**
 * Klasa bazy danych [DBInfo.TABLE_NAME]
 *
 * @param context - kontekst aktualnego stanu aplikacji/obiektu
 */
class EventsDBHelper(context: Context) : SQLiteOpenHelper(context, DBInfo.TABLE_NAME, null, 1) {
    /**
     * Tworzenie bazy danych
     *
     * @param db - uchwyt bazy danych
     */
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(BasicCommands.SQL_CREATE_TABLE)
    }

    /**
     * Aktualizacja bazy danych
     *
     * @param db - uchwyt bazy danych
     * @param oldVersion - stara wersja bazy danych
     * @param newVersion - nowa wersja bazy danych
     */
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(BasicCommands.SQL_DELETE_TABLE)
        onCreate(db)
    }
}