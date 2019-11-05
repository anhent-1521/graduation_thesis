package com.example.tuananhe.myapplication.screen.edit_video

import android.content.Context
import android.content.Intent

import android.view.View.GONE
import android.view.View.VISIBLE
import com.bumptech.glide.Glide
import com.example.tuananhe.myapplication.BaseActivity
import com.example.tuananhe.myapplication.R
import com.example.tuananhe.myapplication.data.model.Video
import com.example.tuananhe.myapplication.screen.detail_video.DetailVideoActivity
import com.example.tuananhe.myapplication.utils.MediaUtil
import kotlinx.android.synthetic.main.activity_edit_video.*
import kotlinx.android.synthetic.main.item_video.*

class EditVideoActivity : BaseActivity(), Contract.View {

    private val presenter = Presenter(this)

    companion object {
        private const val VIDEO_EXTRA = "video"

        fun getEditVideoActivityIntent(context: Context?, video: Video): Intent {
            val intent = Intent(context, EditVideoActivity::class.java)
            intent.putExtra(VIDEO_EXTRA, video)
            return intent
        }
    }

    private lateinit var video: Video

    override fun getLayoutResId(): Int = R.layout.activity_edit_video

    override fun initViews() {
        video = intent.getParcelableExtra(DetailVideoActivity.VIDEO_EXTRA)
        Glide.with(this)
                .load(video.path)
                .centerCrop()
                .into(image_thumbnail)
        text_duration.text = MediaUtil.getVideoDuration(video.duration)
        text_name.text = video.name
        text_size.text = MediaUtil.getVideoSize(video.path)

        presenter.initFFmpeg()
        presenter.onGetVideo(video)
        button_trim.setOnClickListener {
            onTrim(edit_trim_start.text.toString(), edit_trim_end.text.toString())
        }
        button_change_speed.setOnClickListener {
            onChangeSpeed(edit_speed.text.toString())
        }
    }

    override fun initComponents() {
    }

    override fun getContext(): Context = this

    override fun onChangeSpeed(speed: String) {
        presenter.changeSpeedVideo(speed)
    }

    override fun onTrim(start: String, end: String) {
        presenter.onTrim(start, end)
    }

    override fun showLoading() {
        progress.visibility = VISIBLE
        text_progress.visibility = VISIBLE
    }

    override fun updateProgress(percent: Int) {
        text_progress.text = "$percent %"
    }


    override fun hideLoading() {
        progress.visibility = GONE
        text_progress.visibility = GONE
    }

}