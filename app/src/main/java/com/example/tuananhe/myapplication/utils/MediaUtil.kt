package com.example.tuananhe.myapplication.utils

import android.text.format.DateUtils
import java.io.File

class MediaUtil {

    companion object {

        fun getVideoDuration(durationInLong: Long): String =
            DateUtils.formatElapsedTime(durationInLong)

        fun getVideoSize(path: String?): String {
            return try {
                val file = File(path)
                calculateSize(file.length())
            } catch (e: SecurityException) {
                e.printStackTrace()
                ""
            }
        }

        private fun calculateSize(sizeInByte: Long): String {
            val sizeInKb = sizeInByte / 1024 //change size to KB
            return when {
                sizeInKb > 1024 -> {
                    val sizeInMb = sizeInKb / 1024 //change size to MB
                    if (sizeInMb < 1024) "$sizeInMb MB" else "${sizeInMb / 1024} GB"
                }
                else -> "$sizeInKb KB"
            }
        }
    }
}