package com.example.tuananhe.myapplication.screen.transparent

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import com.example.tuananhe.myapplication.service.record.RecordService
import com.example.tuananhe.myapplication.utils.Constant

class TransparentPresenter(private val view: TransparentContract.View) : TransparentContract.Presenter {

    override fun getActivity(): Activity = view.provideActivity()

    private lateinit var mediaProjectionManager: MediaProjectionManager

    override fun getProjectionManager() {
        mediaProjectionManager = getActivity()
                .getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Constant.RECORD_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            startRecord(data, resultCode)
        }
    }

    override fun handleIntent(intent: Intent) {
        when (intent.action) {
            Constant.START_RECORD -> {
                startRequestRecord()
            }
            Constant.STOP_RECORD -> {

            }
        }
    }

    override fun startRequestRecord() {
        getActivity().startActivityForResult(
                mediaProjectionManager.createScreenCaptureIntent(),
                Constant.RECORD_REQUEST_CODE)
    }

    override fun startRecord(data: Intent, resultCode: Int) {
        val intent = Intent(getActivity(), RecordService::class.java)
        intent.action = Constant.START_RECORD
        intent.putExtra(Constant.EXTRA_RESULT_CODE, resultCode)
        intent.putExtra(Constant.EXTRAR_DATA_INTENT, data)
        getActivity().startService(intent)
    }

    override fun stopRecord() {
    }

}