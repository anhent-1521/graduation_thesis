package com.example.tuananhe.myapplication.screen.edit.trim

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.os.Handler
import android.util.Log
import android.widget.Toast
import com.example.tuananhe.myapplication.EditInfo
import com.example.tuananhe.myapplication.data.ItemEdit
import com.example.tuananhe.myapplication.data.model.Video
import com.example.tuananhe.myapplication.utils.FileUtil
import com.example.tuananhe.myapplication.utils.FileUtil.Companion.extractVideo
import com.example.tuananhe.myapplication.utils.MediaUtil
import com.example.tuananhe.myapplication.utils.Settings
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler
import com.github.hiteshsondhi88.libffmpeg.FFmpeg
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import wseemann.media.FFmpegMediaMetadataRetriever
import java.io.*
import java.lang.Long

class TrimPresenter(val view: TrimContract.View) : TrimContract.Presenter {

    private var ffmpeg: FFmpeg? = null
    private lateinit var video: Video
    private var setting: Settings? = null
    private var desPath: String? = null
    private var desPath1: String? = null
    private var desPath2: String? = null
    var sourcePath: String? = null

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

    override fun doEdit(info: EditInfo) {

        val cmd: Array<String?> = if (info.editType == ItemEdit.TRIM) {
            arrayOf("-i", sourcePath, "-ss", info.start,
                    "-t", "${info.duration.toInt() + 1}", "-c", "copy", "-preset", "ultrafast", desPath)
        } else {
            val end = info.duration.toInt() + 1
            val hasAudio = MediaUtil.isVideoHaveAudioTrack(video.path.toString())
            if (hasAudio)
                arrayOf("-i", sourcePath, "-filter_complex",
                        "[0:v]select='not(between(t,${info.start},$end))',setpts=N/30/TB[v];[0:a]aselect='not(between(t,${info.start},${info.duration}))',asetpts=N/SR/TB[a]",
                        "-map", "[v]", "-map", "[a]", "-preset", "ultrafast", desPath)
            else {
//                arrayOf("-i", sourcePath, "-filter_complex",
//                        "[0:v]select='not(between(t,${info.start},$end))',setpts=N/30/TB [v]",
//                        "-map", "[v]", "-vsync", "0", "-preset", "ultrafast", desPath)
                removeWhenNoAudio(info)
                return
            }
        }
        try {
            ffmpeg?.execute(cmd, object : ExecuteBinaryResponseHandler() {

                override fun onStart() {
                    Log.w("onStart", "Ffmpeg start  ${cmd.toList()}")
                    view.showProgress()
                }

                override fun onProgress(message: String?) {
                    Log.w("onStart", "Ffmpeg onProgress  $message")
                    if (message!!.indexOf("time=") != -1) {
                        val time = MediaUtil.extractTime(message)
                        var percent = if (info.editType == ItemEdit.TRIM)
                            (time * 1.0f / video.duration * 100).toInt()
                        else
                            (time * 1.0f / (video.duration -
                                    (info.duration.toInt() + 1 - info.start.toInt())) * 100).toInt()
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
                    previewEditVideo()
                }

                override fun onFinish() {
                    view.hideProgress()
                    Log.w("onFinish", "FFmpeg finish")
                }
            })
        } catch (e: FFmpegCommandAlreadyRunningException) {
            e.printStackTrace()
        }

    }

    override fun previewEditVideo() {
        FileUtil.deleteFile(desPath1)
        FileUtil.deleteFile(desPath2)
        val file = File(desPath)
        var video: Video?
        val metadataRetriever = MediaMetadataRetriever()
        CoroutineScope(Dispatchers.IO).launch {
            video = extractVideo(metadataRetriever, file)
            metadataRetriever.release()
            withContext(Dispatchers.Main) {
                view.onSuccess(video)
            }
        }
    }

    override fun cancel() {
        FileUtil.deleteFile(desPath1)
        FileUtil.deleteFile(desPath2)
        ffmpeg?.killRunningProcesses()
    }

    override fun onDestroy() {
        FileUtil.deleteFile(desPath1)
        FileUtil.deleteFile(desPath2)
        ffmpeg?.killRunningProcesses()
        ffmpeg = null
    }

    private fun removeWhenNoAudio(info: EditInfo) {
        val outputFile1 = File(setting?.rootDirectory, "first" + ".mp4")
        desPath1 = outputFile1.canonicalPath
        val outputFile2 = File(setting?.rootDirectory, "second" + ".mp4")
        desPath2 = outputFile2.canonicalPath
        val cmd1 = arrayOf("-i", sourcePath, "-ss", "0",
                "-t", "${info.start.toInt()}", "-c", "copy", "-preset", "ultrafast", desPath1)
        ffmpeg?.execute(cmd1, object : ExecuteBinaryResponseHandler() {

            override fun onStart() {
                Log.w("onStart", "Ffmpeg onStart  ${cmd1.toList()}")
            }

            override fun onProgress(message: String?) {
                Log.w("onProgress", "Ffmpeg onProgress  $message")
            }

            override fun onFailure(message: String?) {
                FileUtil.deleteFile(desPath1)
                FileUtil.deleteFile(desPath2)
                Toast.makeText(view.provideContext(), "Fail", Toast.LENGTH_SHORT).show()
            }

            override fun onSuccess(message: String?) {
                Handler().postDelayed({
                    val cmd2 = arrayOf("-i", sourcePath, "-ss", "${info.duration.toInt()}",
                            "-t", "${video.duration}", "-c", "copy", desPath2)
                    ffmpeg?.execute(cmd2, object : ExecuteBinaryResponseHandler() {

                        override fun onStart() {
                            Log.w("onStart", "Ffmpeg onStart  ${cmd2.toList()}")
                        }

                        override fun onProgress(message: String?) {
                        }

                        override fun onFailure(message: String?) {
                            FileUtil.deleteFile(desPath1)
                            FileUtil.deleteFile(desPath2)
                            Toast.makeText(view.provideContext(), "Fail", Toast.LENGTH_SHORT).show()
                        }

                        override fun onSuccess(message: String?) {
                            val cmd = arrayOf("-i", desPath1, "-i", desPath2, "-filter_complex",
                                    "[0:v][1:v]concat=2:v=1 [v]",
                                    "-map", "[v]",
                                    "-vsync", "0", "-preset", "ultrafast", desPath)
                            ffmpeg?.execute(cmd, object : ExecuteBinaryResponseHandler() {

                                override fun onStart() {
                                    Log.w("onStart", "Ffmpeg start  ${cmd.toList()}")
                                    view.showProgress()
                                }

                                override fun onProgress(message: String?) {
                                    Log.w("onProgress", "Ffmpeg onProgress  $message")
                                    if (message!!.indexOf("time=") != -1) {
                                        val time = MediaUtil.extractTime(message)
                                        var percent = if (info.editType == ItemEdit.TRIM)
                                            (time * 1.0f / video.duration * 100).toInt()
                                        else
                                            (time * 1.0f / (video.duration -
                                                    (info.duration.toInt() + 1 - info.start.toInt())) * 100).toInt()
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
                                    previewEditVideo()
                                }

                                override fun onFinish() {
                                    view.hideProgress()
                                    Log.w("onFinish", "FFmpeg finish")
                                }
                            })
                        }

                        override fun onFinish() {

                        }
                    })
                }
                        , 100)
            }

            override fun onFinish() {
            }
        })
    }

}