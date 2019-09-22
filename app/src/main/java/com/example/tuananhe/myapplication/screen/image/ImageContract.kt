package com.example.tuananhe.myapplication.screen.image

import android.app.Activity
import com.example.tuananhe.myapplication.data.model.Image

class ImageContract {

    interface View{

        fun onGetImage()

        fun onGetImageSuccess(images: ArrayList<Image>)

        fun onGetImageFail()

        fun getContext(): Activity?

        fun showRemindPermission()

        fun hideRemindPermission()
    }

    interface Presenter {
        fun loadImages(directory: String)

        fun checkPermission(permissions: Array<String>)

        fun requestPermission(permissions: Array<String>, requestCode: Int)
    }
}