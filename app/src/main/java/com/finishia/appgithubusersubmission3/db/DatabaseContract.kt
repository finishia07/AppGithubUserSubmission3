package com.finishia.appgithubusersubmission3.db

import android.net.Uri
import android.provider.BaseColumns

object DatabaseContract {

    const val AUTHORITY= "com.finishia.appgithubusersubmission3"
    const val SCHEME= "content"

    class  FavColumns : BaseColumns {
        companion object {
            const val TABLE_NAME = "favorite_user"
            const val _ID = "_id"
            const val AVATAR = "avatar"
            const val USERNAME = "username"
            const val NAME = "name"
            const val REPOSITORY = "repository"
            const val FOLLOWER = "follower"
            const val FOLLOWING = "following"
            const val COMPANY = "company"
            const val LOCATION = "location"

            // untuk membuat URI content://com.appgithubusersubmission3/fav
            val CONTENT_URI: Uri = Uri.Builder().scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath(TABLE_NAME)
                .build()
        }
    }
}