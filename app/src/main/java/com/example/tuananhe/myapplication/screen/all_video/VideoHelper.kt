package com.example.tuananhe.myapplication.screen.all_video

import android.content.Context
import android.media.MediaMetadataRetriever
import android.provider.MediaStore
import com.example.tuananhe.myapplication.data.model.Video
import com.example.tuananhe.myapplication.utils.FileUtil
import java.io.File


class VideoHelper {
    companion object {

        fun getVideos(context: Context): ArrayList<Video> {
            val videos = ArrayList<Video>()
            try {
                val uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                val projection = arrayOf(MediaStore.Video.VideoColumns.DATA)
                val cursor = context.contentResolver.query(uri, projection, null, null, null)
                if (cursor != null) {
                    val metadataRetriever = MediaMetadataRetriever()
                    while (cursor.moveToNext()) {
                        val path = cursor.getString(0)
                        val video = FileUtil.extractVideo(metadataRetriever, File(path), path)
                        if (video != null && video.duration > 3L) {
                            videos.add(video)
                        }
                    }
                    cursor.close()
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            return videos
        }
    }
}
