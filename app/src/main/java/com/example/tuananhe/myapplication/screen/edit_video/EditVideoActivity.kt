package com.example.tuananhe.myapplication.screen.edit_video

import android.content.Context
import android.content.Intent
import com.bumptech.glide.Glide
import com.example.tuananhe.myapplication.BaseActivity
import com.example.tuananhe.myapplication.R
import com.example.tuananhe.myapplication.data.model.Video
import com.example.tuananhe.myapplication.screen.detail_video.DetailVideoActivity
import com.example.tuananhe.myapplication.utils.MediaUtil
import kotlinx.android.synthetic.main.item_video.*

class EditVideoActivity : BaseActivity() {

    companion object {
        const val VIDEO_EXTRA = "video"

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
    }

    override fun initComponents() {
    }

}