package com.example.tuananhe.myapplication.screen.all_video

import android.content.Context
import com.example.tuananhe.myapplication.data.model.Video

interface AllVideoContract {
    interface View {
        fun provideContext() : Context
        fun onGetAllVideo(videos: ArrayList<Video>)
    }

    interface Presenter {
        fun getAllVideo()
    }
}
