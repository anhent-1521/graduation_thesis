package com.example.tuananhe.myapplication.screen.detail_video

import android.graphics.Point
import android.media.MediaPlayer
import android.os.Handler
import android.view.SurfaceHolder
import android.view.View
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
import android.view.View.VISIBLE
import android.widget.SeekBar
import com.example.tuananhe.myapplication.BaseActivity
import com.example.tuananhe.myapplication.R
import com.example.tuananhe.myapplication.data.model.Video
import com.example.tuananhe.myapplication.utils.ExtensionUtil
import com.example.tuananhe.myapplication.utils.MediaUtil
import kotlinx.android.synthetic.main.activity_detail_video.*


class DetailVideoActivity : BaseActivity(), SurfaceHolder.Callback {

    private var player: MediaPlayer? = null
    private var video: Video? = null
    private var isComplete = false
    private var runnable = object : Runnable {
        override fun run() {
            runOnUiThread {
                updateProgress()
            }
            handler.post(this)
        }
    }
    private var handler = Handler()

    override fun getLayoutResId(): Int = R.layout.activity_detail_video

    override fun initViews() {
        surface_view.systemUiVisibility = SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        surface_view.setOnSystemUiVisibilityChangeListener {
            if (((it and SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION) == 0) and (image_play_pause.visibility != VISIBLE)) {
                fadeOutControl()
            }
        }
        surface_view.setOnClickListener { fadeControl() }
        image_play_pause.setOnClickListener { onPlayPauseClicked() }
        seekbar_progress.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                player?.seekTo(seekBar?.progress ?: 0)
            }

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            }
        })
    }

    override fun initComponents() {
        video = intent.getParcelableExtra("video")

        player = MediaPlayer()
        player?.setScreenOnWhilePlaying(true)
        player?.setOnCompletionListener { completeVideo() }

        surface_view.holder.addCallback(this)
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        player?.setSurface(surface_view.holder.surface)
        player?.setDisplay(holder)
        playVideo()
    }

    private fun playVideo() {
        player?.setDataSource(video?.path)
        player?.prepare()
        player?.setOnPreparedListener {
            setSurfaceSize()
            player?.start()
            initControl()
            handler.post(runnable)
        }
    }

    private fun setSurfaceSize() {
        val screenPoint = Point()
        val display = windowManager.defaultDisplay
        display.getRealSize(screenPoint)
        val width = player?.videoWidth ?: screenPoint.x
        val height = player?.videoHeight ?: screenPoint.y
        val videoHeight = height / width.toFloat() * screenPoint.x

        val param = surface_view.layoutParams
        param.width = screenPoint.x
        param.height = videoHeight.toInt()

        surface_view.layoutParams = param

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
                handler.removeCallbacks(runnable)
                image_play_pause.setBackgroundResource(R.drawable.bg_play)
            } else {
                handler.post(runnable)
                player?.start()
                image_play_pause.setBackgroundResource(R.drawable.bg_pause)
            }
        }
    }

    private fun completeVideo() {
        handler.removeCallbacks(runnable)
        isComplete = true
        player?.reset()
        image_play_pause.setBackgroundResource(R.drawable.bg_play)
    }

    private fun fadeControl() {
        if (image_play_pause.visibility == VISIBLE) {
            fadeInControl()
            surface_view.systemUiVisibility =
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        } else {
            fadeOutControl()
            surface_view.systemUiVisibility = SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
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
        text_current.text = MediaUtil.getVideoDuration(current / 1000.toLong())
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.reset()
        player?.release()
        handler.removeCallbacks(runnable)
    }
}
