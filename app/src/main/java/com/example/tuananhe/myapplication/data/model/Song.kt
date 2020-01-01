package com.example.tuananhe.myapplication.data.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Song(var id: Int,
                var title: String?,
                var duration: Long,
                var data: Uri) : Parcelable