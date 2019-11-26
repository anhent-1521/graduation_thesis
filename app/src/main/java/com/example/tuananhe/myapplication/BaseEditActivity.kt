package com.example.tuananhe.myapplication

import android.media.MediaPlayer
import android.os.Handler
import android.util.Log
import android.widget.SeekBar
import com.example.tuananhe.myapplication.data.model.Video
import com.example.tuananhe.myapplication.utils.ExtensionUtil
import com.example.tuananhe.myapplication.utils.MediaUtil
import kotlinx.android.synthetic.main.activity_choose_edit.*
import kotlinx.android.synthetic.main.activity_choose_edit.image_play_pause
import kotlinx.android.synthetic.main.activity_choose_edit.seekbar_progress
import kotlinx.android.synthetic.main.activity_choose_edit.video_view
import kotlinx.android.synthetic.main.activity_detail_video.*
import kotlinx.android.synthetic.main.layout_header_edit.*
import kotlinx.android.synthetic.main.layout_header_edit.image_back
import kotlinx.android.synthetic.main.layout_progress_edit.*

abstract class BaseEditActivity : BaseActivity() {

    lateinit var video: Video
    var player: MediaPlayer? = null
    var progressHandler = Handler()
    var isComplete = false
    var isGotPlayer = false
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
        with(ExtensionUtil()) {
            text_preview.changePrimaryStyle(this@BaseEditActivity)
            text_cancel.changePrimaryStyle(this@BaseEditActivity)
            text_save.changePrimaryStyle(this@BaseEditActivity, R.drawable.bg_save_edit_press)
        }
        text_preview.setOnClickListener { onClickPreview() }
        text_cancel.setOnClickListener { onClickCancel() }
        text_save.setOnClickListener { onClickSave() }
        frame_progress.setOnClickListener {  }
        text_title.text = getEditTitle()
        progress.maxProgress = 100.toDouble()
        seekbar_progress.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (isComplete) {
                    seekBar?.progress = seekBar?.max ?: 0
                    return
                }
                video_view.seekTo(seekBar?.progress ?: 0)
            }

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            }
        })
        initView()
    }

    override fun initComponents() {
        getVideo()
        playVideo()
        setItemChoose()
    }

    abstract fun initView()

    abstract fun getEditTitle(): String

    abstract fun setItemChoose()

    abstract fun onClickPreview()

    abstract fun onClickCancel()

    abstract fun onClickSave()

    open fun onPlayerReady() {

    }

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
            if (!isGotPlayer) {
                isGotPlayer = true
                onPlayerReady()
            }
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
        val current = video_view?.currentPosition ?: 0
        seekbar_progress.progress = current
        text_start.text = MediaUtil.getVideoDuration(current / SECOND_UNIT)
    }

    private fun completeVideo() {
        progressHandler.removeCallbacks(progressRunnable)
        isComplete = true
        player?.reset()
        image_play_pause.setBackgroundResource(R.drawable.ic_resume_record)
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