package com.example.tuananhe.myapplication.screen.edit.crop

import android.content.Context
import android.os.Handler
import android.view.View
import com.example.tuananhe.myapplication.BaseEditActivity
import com.example.tuananhe.myapplication.R
import com.example.tuananhe.myapplication.data.model.Video
import com.example.tuananhe.myapplication.screen.detail_video.DetailVideoActivity
import kotlinx.android.synthetic.main.activity_crop.*
import kotlinx.android.synthetic.main.layout_progress_edit.*

class CropActivity : BaseEditActivity(), CropContract.View {

    private val presenter = CropPresenter(this)

    override fun initView() {
        presenter.initFFmpeg()
    }

    override fun getEditTitle(): String = "Crop Video"

    override fun setItemChoose() {
        presenter.getVideo(video)
    }

    override fun onClickPreview() {
    }

    override fun onClickCancel() {
    }

    override fun onClickSave() {
        val cropW = (video.width * crop_view.widthRate).toInt()
        val cropH = (video.height * crop_view.heightRate).toInt()
        val xPos = (video.width * crop_view.xRate).toInt()
        val yPos = (video.height * crop_view.yRate).toInt()
        presenter.doEdit(xPos, yPos, cropW, cropH)
    }

    override fun getLayoutResId(): Int = R.layout.activity_crop

    override fun provideContext(): Context = this

    override fun onEditStart() {
    }

    override fun onEditEnd() {
    }

    override fun showProgress() {
        frame_progress.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        frame_progress.visibility = View.GONE
    }

    override fun updateProgress(pro: Int) {
        progress.setCurrentProgress(pro.toDouble())
    }

    override fun onSuccess(video: Video?) {
        if (video != null) {
            Handler().postDelayed({
                startActivity(getVideoActivityIntent(this,
                        video,
                        DetailVideoActivity::class.java))
                finish()
            }, 100)
        }
    }

}