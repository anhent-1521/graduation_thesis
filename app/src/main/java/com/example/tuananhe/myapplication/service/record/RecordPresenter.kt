package com.example.tuananhe.myapplication.service.record

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Handler
import com.example.tuananhe.myapplication.evenBus.Event
import com.example.tuananhe.myapplication.record.RecordHelper
import com.example.tuananhe.myapplication.utils.Constant
import com.example.tuananhe.myapplication.utils.MediaUtil
import org.greenrobot.eventbus.EventBus


class RecordPresenter(private val view: RecordContract.View) : RecordContract.Presenter {

    private lateinit var recordHelper: RecordHelper
    private val recordHandler = Handler()
    private var duration = 0L
    private var recordRunnable = object : Runnable {
        override fun run() {
            val time = MediaUtil.getVideoDuration(duration)
            EventBus.getDefault().post(Event(Constant.RECORDING, time))
            duration ++
            recordHandler.postDelayed(this, 1000)
        }
    }

    override fun getRecordHelper() {
        recordHelper = RecordHelper(getContent())
    }

    override fun getContent(): Context = view.provideContext()

    override fun handleIntent(intent: Intent): Int {
        val action = intent.action
        when (action) {
            Constant.START_RECORD -> {
                val data = intent.getParcelableExtra<Intent>(Constant.EXTRAR_DATA_INTENT)
                val resultCode = intent.getIntExtra(Constant.EXTRA_RESULT_CODE, 0)
                recordHelper.startRecord(data, resultCode)
                startTime()
            }
            Constant.PAUSE_RECORD -> {
                recordHelper.stopRecording()
            }
            Constant.STOP_RECORD -> {
                recordHelper.stopRecording()
                stopTime()
            }
        }
        EventBus.getDefault().post(Event(action))
        return Service.START_NOT_STICKY
    }

    private fun startTime() {
        recordHandler.post(recordRunnable)
    }

    private fun stopTime() {
        recordHandler.removeCallbacks(recordRunnable)
        duration = 0
    }
}