package com.finishia.appgithubusersubmission3.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.finishia.appgithubusersubmission3.db.DatabaseContract.FavColumns
import com.finishia.appgithubusersubmission3.db.DatabaseContract.FavColumns.Companion.TABLE_NAME

internal class DatabaseHelper(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val  DATABASE_NAME = "dbfavoritegithub"
        private const val DATABASE_VERSION = 1
        private const val SQL_CREATE_TABLE_FAVORITE = "CREATE TABLE $TABLE_NAME" +
                "(${FavColumns._ID} TEXT PRIMARY KEY," +
                "${FavColumns.AVATAR} TEXT NOT NULL," +
                "${FavColumns.USERNAME} TEXT NOT NULL," +
                "${FavColumns.NAME} TEXT NOT NULL,"+
                "${FavColumns.REPOSITORY} TEXT NOT NULL,"+
                "${FavColumns.FOLLOWER} TEXT NOT NULL,"+
                "${FavColumns.FOLLOWING} TEXT NOT NULL,"+
                "${FavColumns.COMPANY} TEXT NOT NULL,"+
                "${FavColumns.LOCATION} TEXT NOT NULL)"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_TABLE_FAVORITE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }
}