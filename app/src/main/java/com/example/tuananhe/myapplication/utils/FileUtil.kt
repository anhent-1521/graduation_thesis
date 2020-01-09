package com.example.tuananhe.myapplication.utils

import android.content.Context
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.os.Environment
import androidx.core.content.FileProvider
import android.text.TextUtils
import android.util.Log
import com.example.tuananhe.myapplication.data.model.Video
import java.io.File

class FileUtil {
    companion object {

        private fun getIntent(): Intent {
            val intent = Intent(Intent.ACTION_SEND)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            return intent
        }

        fun shareVideo(context: Context?, path: String?) {
            if (path == null || context == null) return
            val uri = FileProvider.getUriForFile(context, "com.file.provider", File(path))
            val intent = getIntent()
            intent.type = "video/*"
            intent.putExtra(Intent.EXTRA_STREAM, uri)
            context.startActivity(Intent.createChooser(intent, "Share video using"))
        }

        fun shareImage(context: Context, path: String?) {
            if (path == null) return
            val uri = FileProvider.getUriForFile(context, "com.file.provider", File(path))
            val intent = getIntent()
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_STREAM, uri)
            context.startActivity(Intent.createChooser(intent, "Share image using"))
        }

        fun deleteFile(path: String?) {
            if (path == null) return
            val file = File(path)
            if (file.exists()) {
                file.delete()
            }
        }

        fun renameVideo(path: String?, newName: String?): File {
            val directory = Settings.getSetting().rootDirectory
            val old = File(path)
            val file = File(directory, newName)
            if (old.exists()) {
                old.renameTo(file)
                return file
            }
            return old
        }

        fun checkIfHaveSDCard(): Boolean {
            return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
        }

        fun extractVideos(files: Array<File>): ArrayList<Video> {
            val videos = ArrayList<Video>()
            val metadataRetriever = MediaMetadataRetriever()
            for (file in files) {
                try {
                    val video = extractVideo(metadataRetriever, file)
                    if (video != null) {
                        videos.add(video)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            metadataRetriever.release()
            return videos
        }

        fun extractVideo(metadataRetriever: MediaMetadataRetriever, file: File, path1: String = ""): Video? {
            var video: Video? = null
            try {
                if (TextUtils.isEmpty(path1)) {
                    metadataRetriever.setDataSource(file.absolutePath)
                } else {
                    metadataRetriever.setDataSource(path1)
                }
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
                video = Video(
                        name,
                        path,
                        videoDuration,
                        videoWidth,
                        videoHeight
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return video
        }
    }
}