package com.example.tuananhe.myapplication.screen.edit.merge_video

import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.example.tuananhe.myapplication.EditInfo
import com.example.tuananhe.myapplication.data.model.Video
import com.example.tuananhe.myapplication.utils.FileUtil
import com.example.tuananhe.myapplication.utils.MediaUtil
import com.example.tuananhe.myapplication.utils.Settings
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler
import com.github.hiteshsondhi88.libffmpeg.FFmpeg
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.lang.Exception
import java.lang.Long

class MergePresenter(private val view: MergeContract.View) : MergeContract.Presenter {

    private var ffmpeg: FFmpeg? = null
    private lateinit var video: Video
    private var setting: Settings? = null
    private var desPath: String? = null
    var sourcePath: String? = null
    private var isSourceVideoHasAudio = false
    private var extraDuration = 0

    override fun doExtractVideo(uri: Uri?) {
        var video: Video?
        CoroutineScope(Dispatchers.IO).launch {
            val metadataRetriever = MediaMetadataRetriever()
            video = FileUtil.extractVideo(metadataRetriever, File(getPath(uri)))
            metadataRetriever.release()
            withContext(Dispatchers.Main) {
                val isChooseVideoHasAudio = MediaUtil.isVideoHaveAudioTrack(video?.path)
                if (isSourceVideoHasAudio != isChooseVideoHasAudio) {
                    val message = if (isSourceVideoHasAudio) "Source video has audio, so you need to choose a video has audio!"
                    else "Source video has not audio, so you need to choose a video has not audio!"
                    view.showAudioDifferent(message)
                } else {
                    view.onGetVideoSuccess(video)
                }
            }
        }
    }

    override fun goGetExtraDuration(duration: Int) {
        extraDuration = duration
    }

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
        isSourceVideoHasAudio = MediaUtil.isVideoHaveAudioTrack(sourcePath)
    }

    override fun doEdit(editInfo: EditInfo) {
        if (TextUtils.isEmpty(editInfo.introPath) && TextUtils.isEmpty(editInfo.outroPath)) {
            Toast.makeText(view.provideContext(), "Intro and Outro are not chosen", Toast.LENGTH_LONG).show()
            return
        }
        try {
            var count = 1
            val cmd = ArrayList<String?>()
            if (!TextUtils.isEmpty(editInfo.introPath)) {
                cmd.add("-i")
                cmd.add(editInfo.introPath)
                count++
            }
            cmd.add("-i")
            cmd.add(sourcePath)
            if (!TextUtils.isEmpty(editInfo.outroPath)) {
                cmd.add("-i")
                cmd.add(editInfo.outroPath)
                count++
            }
            cmd.add("-filter_complex")
            var filter: String
            if (!TextUtils.isEmpty(editInfo.introPath) && !TextUtils.isEmpty(editInfo.outroPath)) {
                filter = if (isSourceVideoHasAudio) {
                    "[0:v]scale=${video.width}/${video.height},setsar=1/1[v0];" +
                            "[2:v]scale=${video.width}/${video.height},setsar=1/1[v2];" +
                            "[v0][0:a][1:v][1:a][v2][2:a] concat=n=3:v=1:a=1"
                } else {
                    "[0:v]scale=${video.width}/${video.height},setsar=1/1[v0];" +
                            "[2:v]scale=${video.width}/${video.height},setsar=1/1[v2];" +
                            "[v0][1:v][v2] concat=n=3:v=1"
                }
            } else {
                filter = if (isSourceVideoHasAudio) {
                    "[0:v]scale=${video.width}/${video.height},setsar=1/1[v0];" +
                            "[1:v]scale=${video.width}/${video.height},setsar=1/1[v1];" +
                            "[v0][0:a][v1][1:a] concat=n=2:v=1:a=1"
                } else {
                    "[0:v]scale=${video.width}/${video.height},setsar=1/1[v0];" +
                            "[1:v]scale=${video.width}/${video.height},setsar=1/1[v1];" +
                            "[v0][v1] concat=n=2:v=1"
                }
            }
            cmd.add(filter)
            cmd.add("-ab")
            cmd.add("48000")
            cmd.add("-ac")
            cmd.add("2")
            cmd.add("-ar")
            cmd.add("44100")
            cmd.add("-vcodec")
            cmd.add("libx264")
            cmd.add("-crf")
            cmd.add("27")
            cmd.add("-q")
            cmd.add("4")

            cmd.add("-vsync")
            cmd.add("0")
            cmd.add("-preset")
            cmd.add("ultrafast")
            cmd.add(desPath)
            Log.d("==============", cmd.toString())
            ffmpeg?.execute(cmd.toTypedArray(), object : ExecuteBinaryResponseHandler() {

                override fun onStart() {
                    view.showProgress()
                }

                override fun onProgress(message: String?) {
                    if (message!!.contains("time=")) {
                        val time = MediaUtil.extractTime(message)
                        var percent = (time * 1.0f / (video.duration + extraDuration) * 100).toInt()
                        if (percent >= 100) {
                            percent = 99
                        }
                        view.updateProgress(percent)
                    }
                }

                override fun onFailure(message: String?) {
                    Log.d("==============", message)
                    Toast.makeText(view.provideContext(), "Failed", Toast.LENGTH_LONG).show()
                }

                override fun onSuccess(message: String?) {
                    Toast.makeText(view.provideContext(), "Success", Toast.LENGTH_LONG).show()
                    previewEditVideo()
                }

                override fun onFinish() {
                    view.hideProgress()
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(view.provideContext(), "Some thing wrong. Please try again!", Toast.LENGTH_LONG).show()
        }

    }

    override fun cancel() {
        ffmpeg?.killRunningProcesses()
    }

    override fun onDestroy() {
        ffmpeg?.killRunningProcesses()
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

    private fun getPath(uri: Uri?): String? {
        val projection = arrayOf(MediaStore.Video.Media.DATA)
        val cursor =
                view.provideContext().contentResolver.query(uri, projection, null, null, null)
        return if (cursor != null) {
            val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
            cursor.moveToFirst()
            cursor.getString(columnIndex)
        } else
            null
    }
}