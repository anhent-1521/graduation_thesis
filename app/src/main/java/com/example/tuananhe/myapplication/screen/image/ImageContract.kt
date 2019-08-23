package com.example.tuananhe.myapplication.screen.image

import android.content.ContentResolver
import com.example.tuananhe.myapplication.data.model.Image

class ImageContract {

    interface View {
        fun onGetImageSuccess(images: ArrayList<Image>)
        fun onGetImageFail()
        fun getContentResolver(): ContentResolver?
    }

    interface Presenter {
        fun loadImages(directory: String)
    }
}