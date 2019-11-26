package com.example.tuananhe.myapplication.screen.video

import android.Manifest
import android.support.v7.widget.LinearLayoutManager
import android.view.View.GONE
import android.view.View.VISIBLE
import com.example.tuananhe.myapplication.BaseFragment
import com.example.tuananhe.myapplication.R
import com.example.tuananhe.myapplication.data.model.Video
import com.example.tuananhe.myapplication.evenBus.Event
import com.example.tuananhe.myapplication.screen.detail_video.DetailVideoActivity
import com.example.tuananhe.myapplication.screen.edit.choose.ChooseEditActivity
import com.example.tuananhe.myapplication.utils.*
import com.example.tuananhe.myapplication.utils.Constant.Companion.COMMON_PERMISSION
import com.example.tuananhe.myapplication.utils.view.dialog.CommonDialog
import com.example.tuananhe.myapplication.utils.view.dialog.EditNameDialog
import kotlinx.android.synthetic.main.fragment_video.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class VideoFragment : BaseFragment(), VideoContract.View {

    private var videoAdapter: VideoAdapter? = null
    private val presenter: VideoPresenter = VideoPresenter(this)
    private var lastListPosition = 0
    private val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private var isRecording = false

    override fun getLayoutResId(): Int = R.layout.fragment_video

    override fun initViews() {
        context?.let {
            with(ExtensionUtil()) {
                button_turn_on.changePrimaryStyle(it)
            }
        }
        button_turn_on.setOnClickListener {
            presenter.requestPermission(
                permissions,
                COMMON_PERMISSION
            )
        }
    }

    override fun provideContext() = activity

    override fun onGetVideoSuccess(videos: ArrayList<Video>) {
        setupAdapter(videos)
    }

    override fun onGetVideoFail() {
    }

    override fun onGetVideo() {
        val setting = Settings.getSetting()
        presenter.loadVideos(setting.rootDirectory)

    }

    override fun showPermissionSetting(permissions: Array<String>?) {
        context?.let {
            AppUtil.openAppPermissionSetting(it)
        }
    }

    override fun showVideoView(video: Video) {
        startActivity(DetailVideoActivity.getDetailVideoActivityIntent(context, video))
    }

    override fun showDeleteDialog(video: Video) {
        context?.let { context ->
            val dialog = CommonDialog(
                context,
                context.getString(R.string.dialog_delete_video_title)
            )
            dialog.optimisticListener = {
                FileUtil.deleteFile(video.path)
                videoAdapter?.delete(video)
            }
            dialog.show()
        }
    }

    override fun showRenameDialog(video: Video, pos: Int) {
        context?.let { context ->
            val lastDot = video.name?.lastIndexOf(".") ?: -1
            if (lastDot < 0) return
            val name = video.name?.substring(0, lastDot)
            val extension = video.name?.substring(lastDot)
            val dialog = EditNameDialog(context, name)
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

    override fun goToEditVideo(video: Video) {
        startActivity(getVideoActivityIntent(context, video, ChooseEditActivity::class.java))
    }

    override fun showRemindPermission() {
        layout_remind_permission.visibility = VISIBLE
        recycler_videos.visibility = GONE

    }

    override fun hideRemindPermission() {
        layout_remind_permission.visibility = GONE
        recycler_videos.visibility = VISIBLE
    }

    override fun onResume() {
        super.onResume()
        if (!isRecording) {
            presenter.checkPermission(permissions)
        }
        EventBus.getDefault().register(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public fun onMessageEvent(event: Event) {
        when (event.action) {
            Constant.START_RECORD -> {
                isRecording = true
            }
            Constant.STOP_RECORD -> {
                isRecording = false
                onGetVideo()
            }
            Constant.LOCATION_CHANGED -> {
                onGetVideo()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        lastListPosition =
            (recycler_videos.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
        EventBus.getDefault().unregister(this)
    }

    private fun setupAdapter(videos: ArrayList<Video>) {
        if (videoAdapter == null) {
            videoAdapter = VideoAdapter(videos)
            videoAdapter?.listener = { video -> presenter.startVideo(video) }
            videoAdapter?.shareListener = { path -> presenter.shareVideo(path) }
            videoAdapter?.deleteListener = { video -> presenter.deleteVideo(video) }
            videoAdapter?.renameListener = { video, pos -> presenter.renameVideo(video, pos) }
            videoAdapter?.editListener = { video -> presenter.editVideo(video) }
            recycler_videos.adapter = videoAdapter
        } else {
            videoAdapter?.update(videos)
            recycler_videos.scrollToPosition(lastListPosition)
        }
    }

}