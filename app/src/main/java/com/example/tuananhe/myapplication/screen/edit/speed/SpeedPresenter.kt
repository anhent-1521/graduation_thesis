package com.example.tuananhe.myapplication.screen.edit.speed

import android.media.MediaMetadataRetriever
import android.util.Log
import android.widget.Toast
import com.example.tuananhe.myapplication.EditInfo
import com.example.tuananhe.myapplication.data.model.Video
import com.example.tuananhe.myapplication.utils.FileUtil
import com.example.tuananhe.myapplication.utils.MediaUtil
import com.example.tuananhe.myapplication.utils.MediaUtil.Companion.isVideoHaveAudioTrack
import com.example.tuananhe.myapplication.utils.Settings
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler
import com.github.hiteshsondhi88.libffmpeg.FFmpeg
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.lang.Long
import kotlin.math.log

class SpeedPresenter(val view: SpeedContract.View) : SpeedContract.Presenter {

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
        val audioSpeed = editInfo.speed?.toFloat() ?: 1f
        val speed = 1 / audioSpeed

        val videoSpeed = "[0:v]setpts=$speed*PTS[v]"
        val hasAudio = isVideoHaveAudioTrack(video.path.toString())
        val speedExecute = if (hasAudio) {
            val audioSpeed1 = "[0:a]atempo=$audioSpeed[a]"
            "$videoSpeed;$audioSpeed1"
        } else {
            videoSpeed
        }

        val cmd: Array<String>
        cmd = if (hasAudio) {
            arrayOf("-i", sourcePath,"-c:v", "libx264", "-filter_complex", speedExecute,
                    "-map", "[v]", "-map", "[a]", "-vsync", "2", "-preset", "ultrafast", desPath)
        } else {
            arrayOf("-i", sourcePath, "-filter_complex", speedExecute,
                    "-map", "[v]", "-vsync", "2", "-preset", "ultrafast", desPath)
        }
        val duration = video.duration * speed
        ffmpeg?.execute(cmd, object : ExecuteBinaryResponseHandler() {

            override fun onStart() {
                view.showProgress()
            }

            override fun onProgress(message: String?) {
                Log.w("onProgressonProgrs", "$message")
                if (message!!.indexOf("time=") != -1) {
                    val time = MediaUtil.extractTime(message)
                    var percent = (time * 1.0f / duration * 100).toInt()
                    if (percent >= 100) {
                        percent = 99
                    }
                    view.updateProgress(percent)
                }
            }

            override fun onFailure(message: String?) {
                Toast.makeText(view.provideContext(), "Fail", Toast.LENGTH_SHORT).show()
            }

            override fun onSuccess(message: String?) {
                Toast.makeText(view.provideContext(), "Success", Toast.LENGTH_SHORT).show()
                view.updateProgress(100)
                previewEditVideo()
            }

            override fun onFinish() {
                view.hideProgress()
            }
        })
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