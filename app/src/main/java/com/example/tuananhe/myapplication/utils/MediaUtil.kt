package com.example.tuananhe.myapplication.utils

import android.net.ParseException
import android.text.format.DateUtils
import java.io.File
import java.util.regex.Pattern

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

        fun extractTime(log: String): Int {
            val pattern = Pattern.compile("(\\d\\d):(\\d\\d):(\\d\\d)")
            val matcher = pattern.matcher(log)
            var time = 0
            if (matcher.find()) {
                val times = matcher.group(0).split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                for (i in times.indices) {
                    try {
                        val timeStr = times[i]
                        val timeInt = Integer.parseInt(timeStr)
                        when (i) {
                            0 -> time += timeInt * 60 * 60
                            1 -> time += timeInt * 60
                            2 -> time += timeInt
                        }

                    } catch (e: ParseException) {
                    }

                }
            }

            return time
        }
    }
}