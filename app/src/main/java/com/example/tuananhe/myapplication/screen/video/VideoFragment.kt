package com.example.tuananhe.myapplication.screen.video

import android.content.Intent
import android.os.Environment
import com.example.tuananhe.myapplication.BaseFragment
import com.example.tuananhe.myapplication.R
import com.example.tuananhe.myapplication.data.model.Video
import com.example.tuananhe.myapplication.screen.detail_video.DetailVideoActivity
import com.example.tuananhe.myapplication.utils.Constant.Companion.VIDEO_FOLDER
import kotlinx.android.synthetic.main.fragment_video.*

class VideoFragment : BaseFragment(), VideoContract.View {

    private var videoAdapter: VideoAdapter? = null
    private var videoRetriever: VideoRetriever? = null

    override fun getLayoutResId(): Int = R.layout.fragment_video

    override fun initViews() {
        videoRetriever = VideoRetriever(this)
        videoRetriever?.loadVideos(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + VIDEO_FOLDER)
    }

    override fun onGetVideoSuccess(videos: ArrayList<Video>) {
        videoAdapter = VideoAdapter(videos)
        videoAdapter?.listener = { video -> gotoDetailVideo(video) }
        recycler_videos.adapter = videoAdapter
    }

    override fun onGetVideoFail() {
    }

    private fun gotoDetailVideo(video: Video) {
        val intent = Intent(context, DetailVideoActivity::class.java)
        intent.putExtra("video", video)
        startActivity(intent)
    }

}