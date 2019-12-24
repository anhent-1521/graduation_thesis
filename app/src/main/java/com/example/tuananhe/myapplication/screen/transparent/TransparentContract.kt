package com.example.tuananhe.myapplication.screen.transparent

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import com.example.tuananhe.myapplication.data.model.Image

interface TransparentContract {

    interface View{
        fun provideActivity(): Activity
        fun onScreenShotSuccess(image: Image?, bitmap: Bitmap)
    }

    interface Presenter{
        fun getActivity(): Activity
        fun getProjectionManager()
        fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
        fun handleIntent(intent: Intent)
        fun startRequestRecord()
        fun startRecord(data: Intent, resultCode: Int)
        fun stopRecord()
    }
}