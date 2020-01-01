package com.example.tuananhe.myapplication.screen.edit.add_music

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.example.tuananhe.myapplication.EditInfo
import com.example.tuananhe.myapplication.data.model.Song
import com.example.tuananhe.myapplication.data.model.Video
import com.example.tuananhe.myapplication.utils.MediaUtil
import com.example.tuananhe.myapplication.utils.Settings
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler
import com.github.hiteshsondhi88.libffmpeg.FFmpeg
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import wseemann.media.FFmpegMediaMetadataRetriever
import java.io.File
import java.lang.Long

class AddMusicPresenter(private val view: AddMusicContract.View) : AddMusicContract.Presenter {

    private var ffmpeg: FFmpeg? = null
    private lateinit var video: Video
    private var setting: Settings? = null
    private var desPath: String = ""
    var sourcePath: String = ""
    private var song = Song(1, "title", 0, Uri.EMPTY)

    override fun getSongs() {
        val songs = SongHelper.getSongs(view.provideContext())
        view.onGetsSong(songs)
    }

    override fun chooseSong(song: Song) {
        this.song = song
    }

    override fun initFFmpeg() {
        ffmpeg = FFmpeg.getInstance(view.provideContext())
        setting = Settings.getSetting()
        val outputFile = File(
                setting?.rootDirectory, "Edited-"
                + Long.toHexString(System.currentTimeMillis()) + ".mp4"
        )
        desPath = outputFile.canonicalPath
    }

    override fun getVideo(video: Video) {
        this.video = video
        sourcePath = video.path.toString()
    }

    override fun doEdit(editInfo: EditInfo) {
        if (song.duration < editInfo.start.toInt() || song.duration < editInfo.start.toInt() + editInfo.duration.toInt()) {
            Toast.makeText(view.provideContext(), "Some thing wrong. Please try again!", Toast.LENGTH_LONG).show()
        }
        val end = video.duration - editInfo.duration.toInt()
        val songFilter = if (MediaUtil.isVideoHaveAudioTrack(video.path)) {
            "[1]atrim=0:${editInfo.duration.toInt() - editInfo.start.toInt()}" +
                    ",adelay=${editInfo.start.toInt() * 1000}|${editInfo.start.toInt() * 1000}" +
                    ",volume=1.0[a1];[0:a]volume=1.0[v0];" +
                    "[v0][a1]amix=inputs=2:duration=first[a]"
        } else {
            "[1]atrim=0:${editInfo.duration.toInt() - editInfo.start.toInt()}" +
                    ",adelay=${editInfo.start.toInt() * 1000}|${editInfo.start.toInt() * 1000}"+
                    ",volume=1.0[a]"
        }
        try {
            val cmd =
                    arrayOf("-i", sourcePath, "-i", song.data.path, "-filter_complex", songFilter, "-map", "0:v", "-map",
                            "[a]", "-vsync", "2", "-c:v", "copy", "-c:a", "aac", "-preset", "ultrafast", desPath)
            ffmpeg?.execute(cmd, object : ExecuteBinaryResponseHandler() {

                override fun onStart() {
                    view.showProgress()
                }

                override fun onProgress(message: String?) {
                    Log.w("onProgressonProgrs", "$message")
                    if (message!!.indexOf("time=") != -1) {
                        val time = MediaUtil.extractTime(message)
                        var percent = (time * 1.0f / video.duration * 100).toInt()
                        if (percent >= 100) {
                            percent = 99
                        }
                        view.updateProgress(percent)
                    }
                }

                override fun onFailure(message: String?) {
                    Log.d("===========", message)
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
        ffmpeg = null
    }

    override fun previewEditVideo() {

    }

    fun getListBitmap(video: Video) {
        var bitmaps: ArrayList<Bitmap>
        CoroutineScope(Dispatchers.IO).launch {
            bitmaps = getBitmaps(video)
            withContext(Dispatchers.Main) {
                view.onGetListBitMap(bitmaps)
            }
        }
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

}