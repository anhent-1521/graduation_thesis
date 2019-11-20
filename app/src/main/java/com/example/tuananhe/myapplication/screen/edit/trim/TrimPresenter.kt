package com.example.tuananhe.myapplication.screen.edit.trim

import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import com.example.tuananhe.myapplication.EditInfo
import com.example.tuananhe.myapplication.data.model.Video
import com.example.tuananhe.myapplication.utils.MediaUtil
import com.example.tuananhe.myapplication.utils.Settings
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler
import com.github.hiteshsondhi88.libffmpeg.FFmpeg
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import wseemann.media.FFmpegMediaMetadataRetriever
import java.io.File
import java.lang.Long

class TrimPresenter(val view: TrimContract.View) : TrimContract.Presenter {

    private lateinit var ffmpeg: FFmpeg
    private lateinit var video: Video
    private var setting: Settings? = null
    private var desPath: String? = null
    private var sourcePath: String? = null

    override fun initFFmpeg() {
        ffmpeg = FFmpeg.getInstance(view.provideContext())
        setting = Settings.getSetting()
        val outputFile = File(setting?.rootDirectory, "Edited-"
                + Long.toHexString(System.currentTimeMillis()) + ".mp4")
        desPath = outputFile.canonicalPath
    }

    override fun getVideo(video: Video) {
        this.video = video
        sourcePath = video.path
    }

    private fun getBitmaps(video: Video): ArrayList<Bitmap> {
        var retriever: FFmpegMediaMetadataRetriever? = null
        val bitmaps = ArrayList<Bitmap>()
        try {
            retriever = FFmpegMediaMetadataRetriever()
            retriever.setDataSource(video.path)
            for (i in 0..10) {
                var timeUs = i * video.duration * 100000
                if (timeUs == 0L) {
                    timeUs = 100000L
                }
                val bitmap = retriever.getFrameAtTime(timeUs,
                        FFmpegMediaMetadataRetriever.OPTION_CLOSEST_SYNC)
                if (bitmap != null) {
                    bitmaps.add(bitmap)
                }
            }
        } catch (e: IllegalArgumentException) {
        }
        retriever?.release()
        return bitmaps
    }

    override fun getListBitmap(video: Video) {
        var bitmaps: ArrayList<Bitmap>
        CoroutineScope(Dispatchers.IO).launch {
            bitmaps = getBitmaps(video)
            withContext(Dispatchers.Main) {
                view.onGetListBitMap(bitmaps)
            }
        }
    }

    override fun doEdit(editInfo: EditInfo) {
        val cmd = arrayOf("-i", sourcePath, "-ss", editInfo.start,
                "-t", editInfo.duration, "-c", "copy", "-preset", "ultrafast", desPath)
        ffmpeg.execute(cmd, object : ExecuteBinaryResponseHandler() {

            override fun onStart() {
                Log.w("onStart", "Ffmpeg start  ${cmd.toList()}")
                view.showProgress()
            }

            override fun onProgress(message: String?) {

                if (message!!.indexOf("time=") != -1) {
                    val time = MediaUtil.extractTime(message)
                    var percent = (time * 1.0f / video.duration * 100).toInt()
                    if (percent >= 100) {
                        percent = 99
                    }
                    Log.w("onProgressonProgrs", "$percent")
                    view.updateProgress(percent)
                }
            }

            override fun onFailure(message: String?) {
                Log.w("onFailure", "FFmpeg failure: " + message!!)
                Toast.makeText(view.provideContext(), "Fail", Toast.LENGTH_SHORT).show()
            }

            override fun onSuccess(message: String?) {
                Log.w("onSuccess", "FFmpeg success: " + message!!)
                Toast.makeText(view.provideContext(), "Success", Toast.LENGTH_SHORT).show()
                view.updateProgress(100)
                view.onSuccess();
            }

            override fun onFinish() {
                view.hideProgress()
                Log.w("onFinish", "FFmpeg finish")
            }
        })
    }

    override fun cancel() {
        ffmpeg.killRunningProcesses()
    }


    override fun onDestroy() {
    }

}