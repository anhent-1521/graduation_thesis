package com.example.tuananhe.myapplication.screen.edit.speed

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.view.View
import android.widget.SeekBar
import com.example.tuananhe.myapplication.BaseEditActivity
import com.example.tuananhe.myapplication.EditInfo
import com.example.tuananhe.myapplication.R
import com.example.tuananhe.myapplication.data.ItemEdit
import com.example.tuananhe.myapplication.data.model.Video
import com.example.tuananhe.myapplication.screen.detail_video.DetailVideoActivity
import com.example.tuananhe.myapplication.screen.edit.preview.PreviewActivity
import com.example.tuananhe.myapplication.screen.edit.preview.SpeedPreviewActivity
import kotlinx.android.synthetic.main.activity_speed.*
import kotlinx.android.synthetic.main.layout_progress_edit.*

class SpeedActivity : BaseEditActivity(), SpeedContract.View {

    private val presenter = SpeedPresenter(this)
    private var speed = 0F

    override fun initView() {
        presenter.initFFmpeg()
        seekbar_speed.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            @SuppressLint("SetTextI18n")
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                var position = i
                if (position in 0..50) {
                    position += 50
                } else {
                    position = 100 + 2 * (position - 50)
                }
                val speed = position / 100f
                text_speed.text = "${speed}x"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                var position = seekBar.progress
                if (position in 0..50) {
                    position += 50
                } else {
                    position = 100 + 2 * (position - 50)
                }
                speed = position / 100f
            }

        })
    }

    override fun getEditTitle(): String = "Change Speed"

    override fun setItemChoose() {
        presenter.getVideo(video)
    }

    override fun onClickPreview() {
        Handler().postDelayed({
            startActivity(getVideoActivityIntent(this,
                    video,
                    SpeedPreviewActivity::class.java,
                    EditInfo(speed = speed.toString(), editType = ItemEdit.SPEED)))
            finish()
        }, 100)
    }

    override fun onClickCancel() {
        presenter.cancel()
        hideProgress()
    }

    override fun onClickSave() {
        presenter.doEdit(EditInfo(speed = speed.toString()))
    }

    override fun getLayoutResId(): Int = R.layout.activity_speed

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