package com.example.tuananhe.myapplication.screen.edit.trim

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.os.Handler
import android.view.View
import com.example.tuananhe.myapplication.BaseEditActivity
import com.example.tuananhe.myapplication.EditInfo
import com.example.tuananhe.myapplication.R
import com.example.tuananhe.myapplication.utils.MediaUtil
import kotlinx.android.synthetic.main.activity_trim.*
import kotlinx.android.synthetic.main.layout_progress_edit.*

class TrimActivity : BaseEditActivity(), TrimContract.View {

    private val presenter = TrimPresenter(this)
    private var curMin = 0F
    private var curMax = 0F

    override fun getLayoutResId(): Int = R.layout.activity_trim

    override fun getEditTitle(): String = "Trim Video"

    override fun onClickSave() {
        presenter.doEdit(EditInfo(getTime(curMin),
                duration = getTime(curMax - curMin)))
    }

    override fun onClickCancel() {
    }

    override fun initView() {
        presenter.initFFmpeg()
        range_seekbar.setOnRangeSeekbarChangeListener { minValue, maxValue ->
            val min = minValue.toFloat()
            val max = maxValue.toFloat()
            onSelectTime(min, max)
            if (player != null && !isComplete) {
                val current = if (curMin == min) maxValue else minValue
                player?.seekTo(current.toInt())
            }
            curMin = min
            curMax = max
        }
    }

    override fun setItemChoose() {
        presenter.getVideo(video)
        Handler().postDelayed({ presenter.getListBitmap(video) }, 200)
    }

    override fun onClickPreview() {
    }

    @SuppressLint("SetTextI18n")
    override fun onPlayerReady() {
        super.onPlayerReady()
        val max = player?.duration?.toFloat() ?: video.duration.toFloat()
        range_seekbar.setMaxValue(max)
        text_to.text = MediaUtil.getVideoDuration(max.toLong() / SECOND_UNIT)
        text_length.text = "${MediaUtil.getVideoDuration(max.toLong() / SECOND_UNIT)}s"
    }

    override fun provideContext(): Context = this

    override fun onGetListBitMap(bitmaps: ArrayList<Bitmap>) {
        slide_image.addListBitmap(bitmaps)
    }

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

    override fun onSuccess() {
        Handler().postDelayed({
            finish()
        }, 100)
    }

    @SuppressLint("SetTextI18n")
    private fun onSelectTime(min: Float, max: Float) {
        text_from.text = MediaUtil.getVideoDuration(min.toLong() / SECOND_UNIT)
        text_to.text = MediaUtil.getVideoDuration(max.toLong() / SECOND_UNIT)
        text_length.text = "${MediaUtil.getVideoDuration((max - min).toLong() / SECOND_UNIT)}s"
    }

    private fun getTime(number: Float) = (number.toInt() / 1000).toString()

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }

}