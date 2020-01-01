package com.example.tuananhe.myapplication.screen.edit.add_music

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Handler
import android.view.View
import com.example.tuananhe.myapplication.BaseEditActivity
import com.example.tuananhe.myapplication.EditInfo
import com.example.tuananhe.myapplication.R
import com.example.tuananhe.myapplication.data.model.Song
import com.example.tuananhe.myapplication.data.model.Video
import com.example.tuananhe.myapplication.screen.detail_video.DetailVideoActivity
import com.example.tuananhe.myapplication.screen.edit.add_music.SongActivity.Companion.EXTRA_SONG
import com.example.tuananhe.myapplication.utils.MediaUtil
import com.example.tuananhe.myapplication.utils.view.dialog.CommonDialog
import kotlinx.android.synthetic.main.activity_trim.*
import kotlinx.android.synthetic.main.layout_progress_edit.*

class AddMusicActivity : BaseEditActivity(), AddMusicContract.View {

    companion object {
        const val SONG_REQUEST_CODE = 21
    }

    private var curMin = 0F
    private var curMax = 0F
    private val presenter = AddMusicPresenter(this)

    override fun initView() {
        presenter.initFFmpeg()
        range_seekbar.setOnRangeSeekbarFinalValueListener { minValue, maxValue ->
            val min = minValue.toFloat()
            val max = maxValue.toFloat()
            if (player != null && !isComplete) {
                val current = if (curMin == min) maxValue else minValue
                player?.seekTo(current.toInt())
            }
            onSelectTime(min, max)
            curMin = min
            curMax = max
        }
    }

    override fun getEditTitle(): String = "Add Music"

    override fun setItemChoose() {
        presenter.getVideo(video)
        Handler().postDelayed({ presenter.getListBitmap(video) }, 200)
    }

    override fun onClickPreview() {
        presenter.getSongs()
    }

    override fun onClickCancel() {
        presenter.cancel()
        hideProgress()
    }

    override fun onClickSave() {
        presenter.doEdit(EditInfo(getTime(curMin),
                duration = getTime(curMax)))
    }

    override fun getLayoutResId(): Int = R.layout.activity_add_music

    override fun onGetsSong(songs: ArrayList<Song>) {
        if (songs.size > 0) {
            startActivityForResult(SongActivity.getSongIntent(this, songs), SONG_REQUEST_CODE)
        } else {
            val dialog = CommonDialog(this, "Not found any mp3 file!", true)
            dialog.optimisticListener = { dialog.dismiss() }
            dialog.show()
        }
    }

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

    override fun onGetListBitMap(bitmaps: ArrayList<Bitmap>) {
        slide_image.addListBitmap(bitmaps)
    }

    @SuppressLint("SetTextI18n")
    override fun onPlayerReady() {
        super.onPlayerReady()
        val max = player?.duration?.toFloat() ?: video.duration.toFloat()
        range_seekbar.setMaxValue(max)
        text_to.text = MediaUtil.getVideoDuration(max.toLong() / SECOND_UNIT)
        text_length.text = "${MediaUtil.getVideoDuration(max.toLong() / SECOND_UNIT)}s"
    }

    @SuppressLint("SetTextI18n")
    private fun onSelectTime(min: Float, max: Float) {
        text_from.text = MediaUtil.getVideoDuration(min.toLong() / SECOND_UNIT)
        text_to.text = MediaUtil.getVideoDuration(max.toLong() / SECOND_UNIT)
        text_length.text = "${MediaUtil.getVideoDuration((max - min).toLong() / SECOND_UNIT)}s"
    }

    private fun getTime(number: Float) = (number.toInt() / 1000).toString()

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data == null || resultCode != RESULT_OK || requestCode != SONG_REQUEST_CODE) {
            return
        }
        presenter.chooseSong(data.getParcelableExtra(EXTRA_SONG))
    }
}