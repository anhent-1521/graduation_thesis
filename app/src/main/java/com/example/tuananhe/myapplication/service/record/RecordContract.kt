package com.example.tuananhe.myapplication.service.record

import android.app.Service
import android.content.Context
import android.content.Intent

interface RecordContract {

    interface View {
        fun provideContext(): Service
        fun exitRecord()
    }

    interface Presenter {
        fun getContent(): Context
        fun getRecordHelper()
        fun handleIntent(intent: Intent) : Int
    }
}