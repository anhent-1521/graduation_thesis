package com.example.tuananhe.myapplication.screen.transparent

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import com.example.tuananhe.myapplication.data.model.Image
import com.example.tuananhe.myapplication.evenBus.Event
import com.example.tuananhe.myapplication.utils.Constant
import com.example.tuananhe.myapplication.utils.view.dialog.ScreenShotPreviewDialog
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class TransparentActivity : Activity(), TransparentContract.View {

    private val presenter = TransparentPresenter(this)

    override fun provideActivity(): Activity = this

    override fun onScreenShotSuccess(image: Image?, bitmap: Bitmap) {
        if (image != null) {
            val dialog = ScreenShotPreviewDialog(this, image, bitmap)
            dialog.listener = { finish() }
            dialog.show()
        } else {
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.getProjectionManager()
        presenter.handleIntent(intent)
        EventBus.getDefault().register(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_OK) {
            finish()
            return
        }
        presenter.onActivityResult(requestCode, resultCode, data)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: Event) {
        when (event.action) {
            Constant.START_RECORD -> {
                finish()
            }
            Constant.STOP_RECORD -> {

            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

}
