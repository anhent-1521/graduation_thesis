package com.example.tuananhe.myapplication

import android.app.Application
import android.content.Context
import android.os.Build
import android.util.Log
import com.example.tuananhe.myapplication.utils.FileUtil
import com.example.tuananhe.myapplication.utils.Settings
import com.github.hiteshsondhi88.libffmpeg.FFmpeg
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException
import java.io.File

class App : Application() {

    companion object {
        lateinit var instance: App
            private set

        fun getSDCardRoot() : String {
            if (FileUtil.checkIfHaveSDCard()) {
                val files = instance.getExternalFilesDirs(null)
                return files[1].absolutePath.plus("/video")
            }
            return ""
        }
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
        createSDCardRoot()
    }

    private fun createFolder(path: String) {
        val file = File(path)
        if (!file.exists()) {
            file.mkdir()
        }
    }

    private fun createSDCardRoot() {
        if (FileUtil.checkIfHaveSDCard()) {
            createFolder(getSDCardRoot())
        }
    }

}
