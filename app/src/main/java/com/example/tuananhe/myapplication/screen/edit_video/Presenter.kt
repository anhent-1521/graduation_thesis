package com.example.tuananhe.myapplication.screen.edit_video

import android.annotation.SuppressLint
import android.os.Environment
import android.util.Log
import android.widget.Toast
import com.example.tuananhe.myapplication.data.model.Video
import com.example.tuananhe.myapplication.utils.Constant
import com.example.tuananhe.myapplication.utils.MediaUtil
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler
import com.github.hiteshsondhi88.libffmpeg.FFmpeg
import java.text.SimpleDateFormat

class Presenter(private val view: Contract.View) : Contract.Presenter {

    private var startTime = 0
    private var endTime = 0
    private var duration = 0
    private var mSourcePath = ""
    private lateinit var video: Video
    val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            .toString() + Constant.VIDEO_FOLDER + "/" + System.currentTimeMillis() + ".mp4"

    private lateinit var ffmpeg: FFmpeg

    override fun initFFmpeg() {
        ffmpeg = FFmpeg.getInstance(view.getContext())
    }

    override fun onGetVideo(video: Video) {
        this.video = video
        mSourcePath = video.path.toString()
    }

    override fun trimVideo() {


        val cmd = arrayOf("-i", mSourcePath, "-ss", startTime.toString(),
                "-t", duration.toString(), "-c", "copy", "-preset", "ultrafast", path)
        ffmpeg.execute(cmd, object : ExecuteBinaryResponseHandler() {

            override fun onStart() {
                Log.w("onStart", "Ffmpeg start  ${cmd.sortedArray()}")
                view.showLoading()
            }

            override fun onProgress(message: String?) {

                if (message!!.indexOf("time=") != -1) {
                    Log.w("onProgress", message)
                    val time = MediaUtil.extractTime(message)
                    var percent = (time * 1.0f / video.duration * 100).toInt()
                    if (percent >= 100) {
                        percent = 99
                    }
                    view.updateProgress(percent)
                }
            }

            override fun onFailure(message: String?) {
                Log.w("onFailure", "FFmpeg failure: " + message!!)
                Toast.makeText(view.getContext(), "Fail", Toast.LENGTH_SHORT).show()
            }

            override fun onSuccess(message: String?) {
                Log.w("onSuccess", "FFmpeg success: " + message!!)
                Toast.makeText(view.getContext(), "Success", Toast.LENGTH_SHORT).show()
                view.updateProgress(100)
            }

            override fun onFinish() {
                view.hideLoading()
                Log.w("onFinish", "FFmpeg finish")
            }
        })
    }

    override fun changeSpeedVideo(speed: String) {
        val cmd = arrayOf("-i", mSourcePath, "-filter_complex", "[0:v]setpts=$speed*PTS[v]", "-map", "[v]", "-vsync",
                "2", "-preset", "ultrafast", path)
        ffmpeg.execute(cmd, object : ExecuteBinaryResponseHandler() {

            override fun onStart() {
                Log.w("onStart", "Ffmpeg start")
                view.showLoading()
            }

            override fun onProgress(message: String?) {

                if (message!!.indexOf("time=") != -1) {
                    Log.w("onProgress", message)
                    val time = MediaUtil.extractTime(message)
                    var percent = (time * 1.0f / video.duration / 1000 * 100).toInt()
                    if (percent >= 100) {
                        percent = 99
                    }
                    view.updateProgress(percent)
                }
            }

            override fun onFailure(message: String?) {
                Log.w("onFailure", "FFmpeg failure: " + message!!)
                Toast.makeText(view.getContext(), "Fail", Toast.LENGTH_SHORT).show()
            }

            override fun onSuccess(message: String?) {
                Log.w("onSuccess", "FFmpeg success: " + message!!)
                Toast.makeText(view.getContext(), "Success", Toast.LENGTH_SHORT).show()
                view.updateProgress(100)
            }

            override fun onFinish() {
                view.hideLoading()
                Log.w("onFinish", "FFmpeg finish")
            }
        })
    }

    @SuppressLint("SimpleDateFormat")
    override fun onTrim(start: String, end: String) {
        val startMinute = start.substring(0, 2).toInt()
        val startSecond = start.substring(0, 2).toInt()
        val endMinute = end.substring(0, 2).toInt()
        val endSecond = end.substring(0, 2).toInt()
        startTime = startMinute * 60 + startSecond
        endTime = endMinute * 60 + endSecond
        duration = endTime - startTime

        trimVideo()
    }

    override fun onChangeEnd() {
    }
}