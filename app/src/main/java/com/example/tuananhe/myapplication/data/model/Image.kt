package com.example.tuananhe.myapplication.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Image(
    var name: String,
    var path: String,
    var width: Int,
    var height: Int
) : Parcelable