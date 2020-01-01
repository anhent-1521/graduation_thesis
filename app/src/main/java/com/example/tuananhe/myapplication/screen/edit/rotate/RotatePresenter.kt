package com.example.tuananhe.myapplication.screen.edit.rotate

import android.media.MediaMetadataRetriever
import android.widget.Toast
import com.example.tuananhe.myapplication.EditInfo
import com.example.tuananhe.myapplication.data.model.Video
import com.example.tuananhe.myapplication.utils.FileUtil
import com.example.tuananhe.myapplication.utils.MediaUtil
import com.example.tuananhe.myapplication.utils.Settings
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler
import com.github.hiteshsondhi88.libffmpeg.FFmpeg
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.lang.Exception
import java.lang.Long

class RotatePresenter(val view: RotateContract.View) : RotateContract.Presenter {

    private var ffmpeg: FFmpeg? = null
    private lateinit var video: Video
    private var setting: Settings? = null
    private var desPath: String = ""
    var sourcePath: String = ""

    override fun initFFmpeg() {
        ffmpeg = FFmpeg.getInstance(view.provideContext())
        setting = Settings.getSetting()
        val outputFile = File(setting?.rootDirectory, "Edited-"
                + Long.toHexString(System.currentTimeMillis()) + ".mp4")
        desPath = outputFile.canonicalPath
    }

    override fun getVideo(video: Video) {
        this.video = video
        sourcePath = video.path.toString()
    }

    override fun doEdit(editInfo: EditInfo) {
        val rotate = editInfo.rotate.toInt()
        var rotation = ""
        when (rotate % 4) {
            0 -> {
                Toast.makeText(view.provideContext(), "Rotation is not changed", Toast.LENGTH_SHORT).show()
                return
            }
            1 -> {
                rotation = "transpose = 1"
            }
            2 -> {
                rotation = "transpose = 2,togglePlaytranspose = 2"
            }
            3 -> {
                rotation = "transpose = 2"
            }
        }
        try {
            val cmd = arrayOf("-i", sourcePath, "-vf", rotation, "-preset",
                    "ultrafast", "-c:a", "copy", "-vsync", "2", desPath)

            ffmpeg?.execute(cmd, object : ExecuteBinaryResponseHandler() {

                override fun onStart() {
                    view.showProgress()
                }

                override fun onProgress(message: String?) {
                    if (message!!.contains("time=")) {
                        val time = MediaUtil.extractTime(message)
                        var percent = (time * 1.0f / video.duration * 100).toInt()
                        if (percent >= 100) {
                            percent = 99
                        }
                        view.updateProgress(percent)
                    }
                }

                override fun onFailure(message: String?) {
                }

                override fun onSuccess(message: String?) {
                    previewEditVideo()
                }

                override fun onFinish() {
                    view.hideProgress()
                }
            })
        }  catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(view.provideContext(), "Some thing wrong. Please try again!", Toast.LENGTH_LONG).show()
        }


    }

    override fun cancel() {
        ffmpeg?.killRunningProcesses()
    }

    override fun onDestroy() {
        ffmpeg?.killRunningProcesses()
        ffmpeg = null
    }

    override fun previewEditVideo() {
        val file = File(desPath)
        var video: Video?
        val metadataRetriever = MediaMetadataRetriever()
        CoroutineScope(Dispatchers.IO).launch {
            video = FileUtil.extractVideo(metadataRetriever, file)
            metadataRetriever.release()
            withContext(Dispatchers.Main) {
                view.onSuccess(video)
            }
        }
    }

}