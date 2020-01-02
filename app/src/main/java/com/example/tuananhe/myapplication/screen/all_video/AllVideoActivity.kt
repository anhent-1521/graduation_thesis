package com.example.tuananhe.myapplication.screen.all_video

import android.annotation.SuppressLint
import android.content.Context
import android.view.View.GONE
import com.example.tuananhe.myapplication.BaseActivity
import com.example.tuananhe.myapplication.R
import com.example.tuananhe.myapplication.data.model.Video
import com.example.tuananhe.myapplication.screen.detail_video.DetailVideoActivity
import kotlinx.android.synthetic.main.activity_all_video.*
import kotlinx.android.synthetic.main.layout_header_edit.*

class AllVideoActivity : BaseActivity(), AllVideoContract.View {

    private val presenter = AllVideoPresenter(this)

    override fun getLayoutResId(): Int = R.layout.activity_all_video

    override fun getTitleBarColorId(): Int = R.color.colorPrimary

    @SuppressLint("SetTextI18n")
    override fun initViews() {
        text_title.text = "All Video"
        text_save.visibility = GONE
        image_back.setOnClickListener { onBackPressed() }
    }

    override fun initComponents() {
        presenter.getAllVideo()
    }

    override fun provideContext(): Context = this

    override fun onGetAllVideo(videos: ArrayList<Video>) {
        val adapter = VideoAdapter(videos)
        adapter.listener = {video ->  startActivity(DetailVideoActivity.getDetailVideoActivityIntent(this, video))}
        recycler_video.adapter = adapter
    }
}
