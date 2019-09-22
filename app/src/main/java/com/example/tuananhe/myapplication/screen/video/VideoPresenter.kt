package com.example.tuananhe.myapplication.screen.video

import android.media.MediaMetadataRetriever
import android.os.Build
import com.example.tuananhe.myapplication.data.model.Video
import com.example.tuananhe.myapplication.utils.AppUtil
import com.example.tuananhe.myapplication.utils.FileUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileFilter

class VideoPresenter(private var view: VideoContract.View) : VideoContract.Presenter {

    private val fileFilter = FileFilter { file ->
        file.name.toLowerCase().endsWith("mp4") or file.name.toLowerCase().endsWith("mpeg")
    }

    override fun loadVideos(directory: String) {
        try {
            val outputFile = File(directory)
            val files: Array<File> =
                    outputFile.listFiles(fileFilter) //crash nếu không xin quyền trước

            var videos: ArrayList<Video>
            CoroutineScope(Dispatchers.IO).launch {
                videos = extractVideos(files)
                withContext(Dispatchers.Main) {
                    view.onGetVideoSuccess(videos)
                }
            }
        } catch (e: Exception) {
            view.onGetVideoFail()
            e.printStackTrace()
        }
    }

    override fun startVideo(video: Video) {
        view.showVideoView(video)
    }

    override fun shareVideo(path: String?) {
        FileUtil.shareVideo(view.provideContext(), path)
    }

    override fun deleteVideo(video: Video) {
        view.showDeleteDialog(video)
    }

    override fun renameVideo(video: Video, pos: Int) {
        view.showRenameDialog(video, pos)
    }

    override fun checkPermission(permissions: Array<String>) {
        view.provideContext()?.let {
            if (AppUtil.hasPermission(it, permissions)) {
                view.hideRemindPermission()
                view.onGetVideo()
                return
            }
            view.showRemindPermission()
        }
    }

    override fun requestPermission(permissions: Array<String>, requestCode: Int) {
        view.provideContext()?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !it.shouldShowRequestPermissionRationale(permissions[0])) {
                AppUtil.openAppPermissionSetting(it)
            }
            AppUtil.requestPermission(it, permissions, requestCode)
        }
    }

    private fun extractVideos(files: Array<File>): ArrayList<Video> {
        val videos = ArrayList<Video>()
        val metadataRetriever = MediaMetadataRetriever()
        for (file in files) {
            try {
                metadataRetriever.setDataSource(file.absolutePath)
                val name = file.name
                val duration =
                        metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                val width =
                        metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)
                val height =
                        metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)
                val path = file.path

                val videoDuration = if (duration == null) 0 else duration.toLong() / 1000
                val videoWidth = width?.toInt() ?: 0
                val videoHeight = height?.toInt() ?: 0
                val video = Video(
                        name,
                        path,
                        videoDuration,
                        videoWidth,
                        videoHeight
                )
                videos.add(video)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        metadataRetriever.release()
        return videos
    }
}
