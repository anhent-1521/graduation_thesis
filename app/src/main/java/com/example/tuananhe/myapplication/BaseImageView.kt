package com.example.tuananhe.myapplication

import android.app.Activity
import com.example.tuananhe.myapplication.data.model.Image

interface BaseImageView {

    fun onGetImageSuccess(images: ArrayList<Image>)

    fun onGetImageFail()

    fun getContext(): Activity?
}
