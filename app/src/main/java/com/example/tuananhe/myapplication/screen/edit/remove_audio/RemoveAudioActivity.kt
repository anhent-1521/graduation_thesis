package com.example.tuananhe.myapplication.screen.edit.remove_audio

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.view.View
import com.example.tuananhe.myapplication.BaseEditActivity
import com.example.tuananhe.myapplication.EditInfo
import com.example.tuananhe.myapplication.R
import com.example.tuananhe.myapplication.data.model.Video
import com.example.tuananhe.myapplication.screen.detail_video.DetailVideoActivity
import com.example.tuananhe.myapplication.utils.view.dialog.CommonDialog
import kotlinx.android.synthetic.main.activity_trim.*
import kotlinx.android.synthetic.main.layout_progress_edit.*

class RemoveAudioActivity : BaseEditActivity(), RemoveAudioContract.View {

    private var isMute = false

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
                startActivity(
                    getVideoActivityIntent(
                        this,
                        video,
                        DetailVideoActivity::class.java
                    )
                )
                finish()
            }, 100)
        }
    }

    private val presenter = RemoveAudioPresenter(this)

    override fun initView() {
    }

    override fun getEditTitle(): String = "Remove Audio"

    override fun setItemChoose() {
        presenter.getVideo(video)
    }

    @SuppressLint("SetTextI18n")
    override fun onClickPreview() {
        if (isMute) {
            player?.setVolume(1f, 1f)
            text_preview.text = "Mute Video"
        } else {
            player?.setVolume(0f, 0f)
            text_preview.text = "UnMute Video"
        }
        isMute = !isMute
    }

    override fun onClickCancel() {
        presenter.cancel()
        hideProgress()
    }

    override fun onClickSave() {
        if (!isMute) {
            val dialog = CommonDialog(this, "You have to mute video first.",  true)
            dialog.optimisticListener = { dialog.dismiss() }
            dialog.show()
            return
        }
        presenter.doEdit(EditInfo())
    }

    override fun getLayoutResId(): Int = R.layout.activity_remove_audio
}
