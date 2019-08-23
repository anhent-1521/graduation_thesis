package com.example.tuananhe.myapplication.utils

import android.text.format.DateUtils
import java.io.File
import java.util.concurrent.TimeUnit

class MediaUtil {

    companion object {

        fun getDuration(time: Long, isVerbose: Boolean): String {
            val min: Long
            val sec: Long
            val hour: Long = TimeUnit.SECONDS.toHours(time)

            if (hour == 0L) {
                min = TimeUnit.SECONDS.toMinutes(time)
                sec = time - min * 60
            } else {
                min = TimeUnit.SECONDS.toMinutes(time - hour * 60 * 60)
                sec = time - hour * 60 * 60 - min * 60
            }

            val duration: String
            duration = if (hour == 0L) {
                String.format("%02d:%02d", min, sec)
            } else {
                if (isVerbose) {
                    String.format("%02d:%02d:%02d", hour, min, sec)
                } else {
                    String.format("%02d:%02d", hour, min)
                }
            }

            return duration
        }

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