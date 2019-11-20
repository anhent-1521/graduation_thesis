package com.example.tuananhe.myapplication.screen.edit.trim

import android.content.Context
import android.graphics.Bitmap
import com.example.tuananhe.myapplication.data.model.Video

interface TrimContract {
    interface View {
        fun provideContext(): Context
        fun onGetListBitMap(bitmaps: ArrayList<Bitmap>)
        fun onTrimStart()
        fun onTrimEnd()
        fun showProgress()
        fun hideProgress()
    }

    interface Presenter {
        fun getListBitmap(video: Video)
        fun trimVideo()
    }
}
