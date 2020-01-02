package com.example.tuananhe.myapplication.screen.all_image

import android.content.Context
import com.example.tuananhe.myapplication.data.model.Image

interface AllImageContract {
    interface View {
        fun onGetImages(images: ArrayList<Image>)
    }

    interface Presenter {
        fun getImages(context: Context)
    }
}
