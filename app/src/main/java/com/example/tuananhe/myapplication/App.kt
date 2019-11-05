package com.example.tuananhe.myapplication

import android.app.Application
import android.os.Environment
import android.util.Log
import com.example.tuananhe.myapplication.utils.Settings
import com.github.hiteshsondhi88.libffmpeg.FFmpeg
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException
import java.io.File
import android.support.v4.content.ContextCompat
import android.support.v4.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.support.v4.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.support.v4.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.support.v4.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import java.util.Arrays.asList
import android.support.v4.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import java.text.SimpleDateFormat
import java.util.*
import android.support.v4.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.support.v4.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.support.v4.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import java.nio.file.Files.size
import android.os.Build
import android.support.v4.os.EnvironmentCompat
import android.support.v4.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T










class App : Application() {

    companion object {
        lateinit var instance: App
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        initFFMpeg()
        createRootFolder()
    }

    private fun initFFMpeg() {
        val ffmpeg = FFmpeg.getInstance(this)
        try {
            ffmpeg.loadBinary(object : LoadBinaryResponseHandler() {

                override fun onStart() {}

                override fun onFailure() {}

                override fun onSuccess() {}

                override fun onFinish() {}
            })
        } catch (e: FFmpegNotSupportedException) {
            // Handle if FFmpeg is not supported by device
        }

    }

    private fun createRootFolder() {
        createFolder(Settings.DEFAULT_ROOT_DIRECTORY)
        createFolder(Settings.DEFAULT_VIDEO_DIRECTORY)
        val secStore = System.getenv("SECONDARY_STORAGE")
        val f_secs = File(secStore)
        Log.d("=========", f_secs.canonicalPath)
        Log.d("=========", "${getExternalStorageDirectories()}")
    }

    private fun createFolder(path: String) {
        val file = File(path)
        if (!file.exists()) {
            file.mkdir()
        }
    }
    fun getExternalStorageDirectories(): Array<String?> {

        val results = ArrayList<String>()

        if (results.isEmpty()) { //Method 2 for all versions
            // better variation of: http://stackoverflow.com/a/40123073/5002496
            var output = ""
            try {
                val process = ProcessBuilder().command("mount | grep /dev/block/vold")
                    .redirectErrorStream(true).start()
                process.waitFor()
                val `is` = process.inputStream
                val buffer = ByteArray(1024)
                while (`is`.read(buffer) !== -1) {
                    output = output + String(buffer)
                }
                `is`.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            if (!output.trim { it <= ' ' }.isEmpty()) {
                val devicePoints =
                    output.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                for (voldPoint in devicePoints) {
                    results.add(voldPoint.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[2])
                }
            }
        }

        //Below few lines is to remove paths which may not be external memory card, like OTG (feel free to comment them out)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var i = 0
            while (i < results.size) {
                if (!results[i].toLowerCase().matches(".*[0-9a-f]{4}[-][0-9a-f]{4}".toRegex())) {
                    Log.d("======", results.get(i) + " might not be extSDcard")
                    results.removeAt(i--)
                }
                i++
            }
        } else {
            var i = 0
            while (i < results.size) {
                if (!results.get(i).toLowerCase().contains("ext") && !results.get(i).toLowerCase().contains(
                        "sdcard"
                    )
                ) {
                    Log.d("=======", results.get(i) + " might not be extSDcard")
                    results.removeAt(i--)
                }
                i++
            }
        }

        val storageDirectories = arrayOfNulls<String>(results.size)
        for (i in results.indices) storageDirectories[i] = results.get(i)

        return storageDirectories
    }

}
