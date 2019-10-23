package com.example.tuananhe.myapplication.screen.edit_video

import android.content.Context

interface Contract {

    interface View {
        fun getContext(): Context
        fun showLoding()
        fun hideLoading()
    }

    interface Presenter {
        fun initFFmpeg()
        fun trimVideo()
        fun chageSpeedVideo()
    }
}