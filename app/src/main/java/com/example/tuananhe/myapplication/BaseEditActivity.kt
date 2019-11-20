package com.example.tuananhe.myapplication

import android.media.MediaPlayer
import android.os.Handler
import com.example.tuananhe.myapplication.data.ItemEdit
import com.example.tuananhe.myapplication.data.model.Video
import com.example.tuananhe.myapplication.screen.detail_video.DetailVideoActivity
import com.example.tuananhe.myapplication.utils.MediaUtil
import kotlinx.android.synthetic.main.activity_choose_edit.*
import kotlinx.android.synthetic.main.activity_choose_edit.image_back
import kotlinx.android.synthetic.main.activity_choose_edit.image_play_pause
import kotlinx.android.synthetic.main.activity_choose_edit.seekbar_progress
import kotlinx.android.synthetic.main.activity_choose_edit.video_view

abstract class BaseEditActivity : BaseActivity() {

    lateinit var video: Video
    var player: MediaPlayer? = null
    var progressHandler = Handler()
    var isComplete = false
    var progressRunnable = object : Runnable {
        override fun run() {
            runOnUiThread {
                updateProgress()
            }
            progressHandler.post(this)
        }
    }

    override fun getTitleBarColorId(): Int = R.color.colorPrimary

    override fun initViews() {
        image_back.setOnClickListener { onBackPressed() }
        image_play_pause.setOnClickListener { onPlayPauseClicked() }
        initView()
    }

    override fun initComponents() {
        getVideo()
        playVideo()
        setItemChoose()
    }

    abstract fun initView()

    abstract fun setItemChoose()

    private fun getVideo() {
        video = intent.getParcelableExtra(VIDEO_EXTRA)
        text_end.text = MediaUtil.getVideoDuration(video.duration)
    }

    private fun playVideo() {
        video_view.setVideoPath(video.path)
        video_view?.setOnPreparedListener {
            player = it
            video_view.start()
            seekbar_progress.max = player?.duration ?: 0
            image_play_pause.setBackgroundResource(R.drawable.ic_pause_video)
            progressHandler.post(progressRunnable)
            player?.setOnCompletionListener { completeVideo() }
        }
    }

    private fun onPlayPauseClicked() {
        if (isComplete) {
            isComplete = false
            playVideo()
        } else {
            playPause()
        }
    }

    private fun playPause() {
        player?.let { player ->
            if (player.isPlaying) {
                player.pause()
                progressHandler.removeCallbacks(progressRunnable)
                image_play_pause.setBackgroundResource(R.drawable.ic_resume_record)
            } else {
                progressHandler.post(progressRunnable)
                player.start()
                image_play_pause.setBackgroundResource(R.drawable.ic_pause_video)
            }
        }
    }

    private fun updateProgress() {
        val current = player?.currentPosition ?: 0
        seekbar_progress.progress = current
        text_start.text = MediaUtil.getVideoDuration(current / DetailVideoActivity.SECOND_UNIT)
    }

    private fun completeVideo() {
        progressHandler.removeCallbacks(progressRunnable)
        isComplete = true
        player?.reset()
        image_play_pause.setBackgroundResource(R.drawable.ic_resume_record)
    }

    private fun gotoEdit(item: ItemEdit) {
        when (item.title) {
            ItemEdit.TRIM -> {

            }
            ItemEdit.ADD_IMAGE -> {

            }
            ItemEdit.ADD_INTRO -> {

            }
            ItemEdit.ADD_MUSIC -> {

            }
            ItemEdit.REMOVE_MIDDLE -> {

            }
            ItemEdit.CROP -> {

            }
            ItemEdit.SPEED -> {

            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        progressHandler.removeCallbacks(progressRunnable)
    }

    override fun onPause() {
        super.onPause()
        progressHandler.removeCallbacks(progressRunnable)
    }

}