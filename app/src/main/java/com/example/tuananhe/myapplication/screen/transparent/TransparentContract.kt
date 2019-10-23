package com.example.tuananhe.myapplication.screen.transparent

import android.app.Activity
import android.content.Intent

interface TransparentContract {

    interface View{
        fun provideActivity(): Activity
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