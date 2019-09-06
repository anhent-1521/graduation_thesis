package com.example.tuananhe.myapplication.screen.detail_video

import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.media.MediaPlayer
import android.os.Handler
import android.view.View
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
import android.view.View.VISIBLE
import android.widget.SeekBar
import com.example.tuananhe.myapplication.BaseActivity
import com.example.tuananhe.myapplication.R
import com.example.tuananhe.myapplication.data.model.Video
import com.example.tuananhe.myapplication.utils.ExtensionUtil
import com.example.tuananhe.myapplication.utils.FileUtil
import com.example.tuananhe.myapplication.utils.MediaUtil
import kotlinx.android.synthetic.main.activity_detail_video.*


class DetailVideoActivity : BaseActivity() {

    companion object {
        const val CONTROL_DELAY = 3000L
        const val SECOND_UNIT = 1000L
        const val VIDEO_EXTRA = "video"

        fun getDetailVideoActivityIntent(context: Context?, video: Video): Intent {
            val intent = Intent(context, DetailVideoActivity::class.java)
            intent.putExtra(VIDEO_EXTRA, video)
            return intent
        }
    }

    private var player: MediaPlayer? = null
    private var video: Video? = null
    private var isComplete = false
    private var progressRunnable = object : Runnable {
        override fun run() {
            runOnUiThread {
                updateProgress()
            }
            progressHandler.post(this)
        }
    }
    private var controlRunnable = Runnable {
        runOnUiThread {
            if (image_play_pause.visibility == VISIBLE) {
                fadeInControl()
            }
        }
    }
    private var progressHandler = Handler()
    private var controlHandler = Handler()

    override fun getLayoutResId(): Int = R.layout.activity_detail_video

    override fun initViews() {
        video_view.systemUiVisibility = SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        video_view.setOnSystemUiVisibilityChangeListener {
            if (((it and SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION) == 0) and (image_play_pause.visibility != VISIBLE)) {
                fadeOutControl()
            }
        }
        constraint_parent.setOnClickListener { fadeControl() }
        video_view.keepScreenOn = true

        image_play_pause.setOnClickListener { onPlayPauseClicked() }

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

        image_back.setOnClickListener { onBackPressed() }
        image_share.setOnClickListener {
            onPlayPauseClicked()
            openShare()
        }
    }

    override fun initComponents() {
        video = intent.getParcelableExtra(VIDEO_EXTRA)
        playVideo()
    }


    private fun openShare() {
        FileUtil.shareImage(this, video?.path)
    }

    private fun playVideo() {
        video_view.setVideoPath(video?.path)
        video_view?.setOnPreparedListener {
            player = it
            setVideoSize()
            video_view.start()
            initControl()
            progressHandler.post(progressRunnable)
            controlHandler.postDelayed(controlRunnable, CONTROL_DELAY)
            player?.setOnCompletionListener { completeVideo() }
        }
    }

    private fun setVideoSize() {
        val screenPoint = Point()
        val display = windowManager.defaultDisplay
        display.getRealSize(screenPoint)

        val width = player?.videoWidth ?: screenPoint.x
        val height = player?.videoHeight ?: screenPoint.y

        val videoHeight = height / width.toFloat() * screenPoint.x
        val param = video_view.layoutParams

        if (screenPoint.x / width > 2) {
            param.width = (width / height.toFloat() * screenPoint.y).toInt()
            param.height = screenPoint.y
        } else {
            param.width = screenPoint.x
            param.height = videoHeight.toInt()
        }

        video_view.layoutParams = param
    }

    private fun initControl() {
        seekbar_progress.max = player?.duration ?: 0
        text_duration.text = MediaUtil.getVideoDuration(video?.duration ?: 0)
        image_play_pause.setBackgroundResource(R.drawable.bg_pause)
        image_play_pause.visibility = VISIBLE
    }

    private fun onPlayPauseClicked() {
        if (isComplete) {
            isComplete = false
            playVideo()
        } else {
            resumeAndPauseVideo()
        }
    }

    private fun resumeAndPauseVideo() {
        player?.let {
            if (player?.isPlaying as Boolean) {
                player?.pause()
                progressHandler.removeCallbacks(progressRunnable)
                controlHandler.removeCallbacks(controlRunnable)
                image_play_pause.setBackgroundResource(R.drawable.bg_play)
            } else {
                progressHandler.post(progressRunnable)
                controlHandler.postDelayed(controlRunnable, CONTROL_DELAY)
                player?.start()
                image_play_pause.setBackgroundResource(R.drawable.bg_pause)
            }
        }
    }

    private fun completeVideo() {
        progressHandler.removeCallbacks(progressRunnable)
        controlHandler.removeCallbacks(controlRunnable)
        isComplete = true
        player?.reset()
        image_play_pause.setBackgroundResource(R.drawable.bg_play)
        fadeOutControl()
    }

    private fun fadeControl() {
        if (image_play_pause.visibility == VISIBLE) {
            fadeInControl()
            controlHandler.removeCallbacks(controlRunnable)
            video_view.systemUiVisibility =
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        } else {
            fadeOutControl()
            controlHandler.postDelayed(controlRunnable, CONTROL_DELAY)
            video_view.systemUiVisibility = SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        }

    }

    private fun fadeInControl() {
        with(ExtensionUtil()) {
            image_play_pause.fadeIn()
            linear_progress.fadeIn()
            linear_top.fadeIn()
        }
    }

    private fun fadeOutControl() {
        with(ExtensionUtil()) {
            image_play_pause.fadeOut()
            linear_progress.fadeOut()
            linear_top.fadeOut()
        }
    }

    private fun updateProgress() {
        val current = player?.currentPosition ?: 0
        seekbar_progress.progress = current
        text_current.text = MediaUtil.getVideoDuration(current / SECOND_UNIT)
    }

    override fun onDestroy() {
        super.onDestroy()
        progressHandler.removeCallbacks(progressRunnable)
        controlHandler.removeCallbacks(controlRunnable)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        progressHandler.removeCallbacks(progressRunnable)
        controlHandler.removeCallbacks(controlRunnable)
    }

    override fun onPause() {
        super.onPause()
        progressHandler.removeCallbacks(progressRunnable)
        controlHandler.removeCallbacks(controlRunnable)
    }

}
