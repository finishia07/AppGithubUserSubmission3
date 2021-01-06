package com.finishia.appgithubusersubmission3.helper

import android.database.Cursor
import com.finishia.appgithubusersubmission3.db.DatabaseContract.FavColumns
import com.finishia.appgithubusersubmission3.entity.Gits

object MappingHelper {

    fun mapCursorToArrayList(gitsCursor: Cursor?): ArrayList<Gits>{
        val listGithub = ArrayList<Gits>()

        gitsCursor?.apply {
            while (moveToNext()){
                val id = getString(getColumnIndexOrThrow(FavColumns._ID))
                val avatar = getString(getColumnIndexOrThrow(FavColumns.AVATAR))
                val username = getString(getColumnIndexOrThrow(FavColumns.USERNAME))
                val name = getString(getColumnIndexOrThrow(FavColumns.NAME))
                val repository = getString(getColumnIndexOrThrow(FavColumns.REPOSITORY))
                val follower = getString(getColumnIndexOrThrow(FavColumns.FOLLOWER))
                val following = getString(getColumnIndexOrThrow(FavColumns.FOLLOWING))
                val company = getString(getColumnIndexOrThrow(FavColumns.COMPANY))
                val location = getString(getColumnIndexOrThrow(FavColumns.LOCATION))

                listGithub.add(Gits(id, avatar, username, name, repository, follower, following, company, location ))
            }
        }
        return listGithub
    }

    fun mapCursorToObject(gitsCursor: Cursor?): Gits {
        var gits = Gits()
        gitsCursor?.apply {
            moveToFirst()
            val id = getString(getColumnIndexOrThrow(FavColumns._ID))
            val avatar = getString(getColumnIndexOrThrow(FavColumns.AVATAR))
            val username = getString(getColumnIndexOrThrow(FavColumns.USERNAME))
            val name = getString(getColumnIndexOrThrow(FavColumns.NAME))
            val repository = getString(getColumnIndexOrThrow(FavColumns.REPOSITORY))
            val follower = getString(getColumnIndexOrThrow(FavColumns.FOLLOWER))
            val following = getString(getColumnIndexOrThrow(FavColumns.FOLLOWING))
            val company = getString(getColumnIndexOrThrow(FavColumns.COMPANY))
            val location = getString(getColumnIndexOrThrow(FavColumns.LOCATION))
            gits = Gits(id, avatar, username, name, repository, follower, following, company, location)
        }
        return gits
    }
}