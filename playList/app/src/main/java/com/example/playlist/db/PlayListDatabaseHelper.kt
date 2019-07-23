package com.example.playlist.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class PlayListDatabaseHelper (context: Context): SQLiteOpenHelper(context, DbSettings.DB_NAME, null, DbSettings.DB_VERSION) {
    override fun onCreate(db: SQLiteDatabase?) {
        val createPlayListTableQuery = "CREATE TABLE " + DbSettings.DBPlayListEntry.TABLE + " ( " +
                DbSettings.DBPlayListEntry.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DbSettings.DBPlayListEntry.COL_TRACK_TITLE + " TEXT NULL, " +
                DbSettings.DBPlayListEntry.COL_TRACK_PLAYCOUNT + " TEXT NULL, " +
                DbSettings.DBPlayListEntry.COL_TRACK_DURATION + " TEXT NULL, " +
                DbSettings.DBPlayListEntry.COL_TRACK_ARTIST + " TEXT NULL, " +
                DbSettings.DBPlayListEntry.COL_TRACK_LISTENERS + " TEXT NULL);"

        val createImageAssetTableQuery = "CREATE TABLE " + DbSettings.DBImageAssetEntry.TABLE + " ( " +
                //TODO: Insert code to set up the three columns in the images table
                DbSettings.DBImageAssetEntry.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DbSettings.DBImageAssetEntry.PLAY_ID + " INTEGER NOT NULL, " +
                DbSettings.DBImageAssetEntry.COL_URL + " TEXT NULL, " +
                "CONSTRAINT fk_favorites FOREIGN KEY(" + DbSettings.DBImageAssetEntry.PLAY_ID + ") " +
                "REFERENCES " + DbSettings.DBPlayListEntry.TABLE + "(" + DbSettings.DBPlayListEntry.ID + ") ON DELETE CASCADE);"

        db?.execSQL(createPlayListTableQuery)
        db?.execSQL(createImageAssetTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS " + DbSettings.DBPlayListEntry.TABLE)
        onCreate(db)
    }
}