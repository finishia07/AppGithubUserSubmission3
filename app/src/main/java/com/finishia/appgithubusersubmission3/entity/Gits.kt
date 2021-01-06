package com.finishia.appgithubusersubmission3.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Gits(

    var id: String? =  null,
    var avatar: String?= null,
    var username: String? = null,
    var name: String? = null,
    var repository: String? = null,
    var follower: String?= null,
    var following: String?= null,
    var company: String? = null,
    var location: String?= null

): Parcelable
