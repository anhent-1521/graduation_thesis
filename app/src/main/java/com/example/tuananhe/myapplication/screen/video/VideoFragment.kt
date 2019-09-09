package com.example.tuananhe.myapplication.screen.video

import android.content.Context
import android.os.Environment
import android.support.v7.widget.LinearLayoutManager
import com.example.tuananhe.myapplication.BaseFragment
import com.example.tuananhe.myapplication.R
import com.example.tuananhe.myapplication.data.model.Video
import com.example.tuananhe.myapplication.screen.detail_video.DetailVideoActivity
import com.example.tuananhe.myapplication.utils.Constant.Companion.VIDEO_FOLDER
import com.example.tuananhe.myapplication.utils.FileUtil
import com.example.tuananhe.myapplication.utils.view.dialog.CommonDialog
import com.example.tuananhe.myapplication.utils.view.dialog.EditNameDialog
import kotlinx.android.synthetic.main.fragment_video.*

class VideoFragment : BaseFragment(), VideoContract.View {

    private var videoAdapter: VideoAdapter? = null
    private var videoRetriever: VideoRetriever? = null
    private var lastListPosition = 0

    override fun getLayoutResId(): Int = R.layout.fragment_video

    override fun initViews() {
        videoRetriever = VideoRetriever(this)
    }

    override fun onGetVideoSuccess(videos: ArrayList<Video>) {
        setupAdapter(videos)
    }

    override fun onGetVideoFail() {
    }

    private fun setupAdapter(videos: ArrayList<Video>) {
        if (videoAdapter == null) {
            videoAdapter = VideoAdapter(videos)
            videoAdapter?.listener = { video -> gotoDetailVideo(video) }
            videoAdapter?.shareListener = { path -> FileUtil.shareVideo(context, path) }
            videoAdapter?.deleteListener = { video -> deleteVideo(video) }
            videoAdapter?.renameListener = { video, pos -> renameVideo(video, pos) }
            recycler_videos.adapter = videoAdapter
        } else {
            videoAdapter?.update(videos)
            recycler_videos.scrollToPosition(lastListPosition)
        }
    }

    private fun gotoDetailVideo(video: Video) {
        startActivity(DetailVideoActivity.getDetailVideoActivityIntent(context, video))
    }

    override fun onResume() {
        super.onResume()
        videoRetriever?.loadVideos(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + VIDEO_FOLDER)
    }

    private fun deleteVideo(video: Video) {
        context.let {
            val dialog = CommonDialog(
                context as Context,
                context?.getString(R.string.dialog_delete_video_title)
            )
            dialog.optimisticListener = {
                FileUtil.deleteFile(video.path)
                videoAdapter?.delete(video)
            }
            dialog.show()
        }
    }

    override fun onPause() {
        super.onPause()
        lastListPosition = (recycler_videos.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
    }

    private fun renameVideo(video: Video, pos: Int) {
        context.let {
            val lastDot = video.name?.lastIndexOf(".") ?: -1
            if (lastDot < 0) return
            val name = video.name?.substring(0, lastDot)
            val extension = video.name?.substring(lastDot)
            val dialog = EditNameDialog(context as Context, name)
            dialog.renameListener =
                { newName ->
                    val resultFile = FileUtil.renameVideo(video.path, "$newName$extension")
                    video.name = "$newName$extension"
                    video.path = resultFile.path
                    videoAdapter?.rename(video, pos)
                }
            dialog.show()
        }
    }

}