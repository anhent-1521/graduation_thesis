package com.example.tuananhe.myapplication.screen.video

import com.example.tuananhe.myapplication.data.model.Video

class VideoContract {
    interface View {
        fun onGetVideoSuccess(videos: ArrayList<Video>)
        fun onGetVideoFail()
    }

    interface Presenter {
        fun loadVideos(directory: String)
    }
}
