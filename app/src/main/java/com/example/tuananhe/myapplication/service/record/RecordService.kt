package com.example.tuananhe.myapplication.service.record

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder

class RecordService : Service(), RecordContract.View {

    private val presenter = RecordPresenter(this)

    override fun provideContext(): Service = this

    override fun exitRecord() {
        stopForeground(true)
        stopSelf()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        presenter.getRecordHelper()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return if (intent != null)
            presenter.handleIntent(intent)
        else
            super.onStartCommand(intent, flags, startId)
    }

}