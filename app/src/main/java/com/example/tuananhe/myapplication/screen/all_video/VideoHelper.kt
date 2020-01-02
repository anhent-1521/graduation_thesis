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
                val pathArrList = ArrayList<String>()
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        pathArrList.add(cursor.getString(0))
                    }
                    cursor.close()
                }
                val metadataRetriever = MediaMetadataRetriever()
                for (a in pathArrList) {
                    val video = FileUtil.extractVideo(metadataRetriever, File(a), a)
                    if (video != null && video.duration > 3L) {
                        videos.add(video)
                    }
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            return videos
        }
    }
}
