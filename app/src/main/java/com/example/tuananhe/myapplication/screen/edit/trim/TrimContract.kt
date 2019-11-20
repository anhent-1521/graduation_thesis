package com.example.tuananhe.myapplication.screen.edit.trim

import android.graphics.Bitmap
import com.example.tuananhe.myapplication.data.model.Video

interface TrimContract {
    interface View : com.example.tuananhe.myapplication.View {
        fun onGetListBitMap(bitmaps: ArrayList<Bitmap>)
    }

    interface Presenter : com.example.tuananhe.myapplication.Presenter {
        fun getListBitmap(video: Video)
    }
}
