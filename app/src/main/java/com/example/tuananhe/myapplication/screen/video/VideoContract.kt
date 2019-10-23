package com.example.tuananhe.myapplication.screen.video

import android.app.Activity
import com.example.tuananhe.myapplication.data.model.Video

class VideoContract {
    interface View {
        fun provideContext(): Activity?

        fun onGetVideo()

        fun onGetVideoSuccess(videos: ArrayList<Video>)

        fun onGetVideoFail()

        fun showVideoView(video: Video)

        fun showDeleteDialog(video: Video)

        fun showRenameDialog(video: Video, pos: Int)

        fun goToEditVideo(video: Video)

        fun showPermissionSetting(permissions: Array<String>?)

        fun showRemindPermission()

        fun hideRemindPermission()
    }

    interface Presenter {
        fun loadVideos(directory: String)

        fun startVideo(video: Video)

        fun shareVideo(path: String?)

        fun deleteVideo(video: Video)

        fun renameVideo(video: Video, pos: Int)

        fun editVideo(video: Video)

        fun checkPermission(permissions: Array<String>)

        fun requestPermission(permissions: Array<String>, requestCode: Int)
    }
}
