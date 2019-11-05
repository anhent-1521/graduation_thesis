package com.example.tuananhe.myapplication.screen.edit_video

import android.content.Context
import com.example.tuananhe.myapplication.data.model.Video

interface Contract {

    interface View {
        fun getContext(): Context
        fun onChangeSpeed(speed: String)
        fun onTrim(start: String, end: String)
        fun showLoading()
        fun hideLoading()
        fun updateProgress(percent: Int)
    }

    interface Presenter {
        fun onGetVideo(video: Video)
        fun initFFmpeg()
        fun trimVideo()
        fun changeSpeedVideo(speed: String)
        fun onTrim(start: String, end: String)
        fun onChangeEnd()
    }
}