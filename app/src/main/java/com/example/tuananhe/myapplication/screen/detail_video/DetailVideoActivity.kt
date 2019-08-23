package com.example.tuananhe.myapplication.screen.detail_video

import android.media.MediaPlayer
import android.os.Handler
import android.util.Log
import android.view.SurfaceHolder
import android.view.View.VISIBLE
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
        surface_view.setOnClickListener { fadeControl() }
        image_play_pause.setOnClickListener { onPlayPauseClicked() }
    }

    override fun initComponents() {
        video = intent.getParcelableExtra<Video>("video")

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
            player?.start()
            initControl()
            handler.post(runnable)
        }
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
                Log.d("----", "pause ${player?.currentPosition} ")
                player?.pause()
                image_play_pause.setBackgroundResource(R.drawable.bg_play)
                Log.d("----", "pause ${player?.currentPosition} ")
            } else {
                Log.d("----", "play ${player?.currentPosition} ")
                player?.start()
                image_play_pause.setBackgroundResource(R.drawable.bg_pause)
                Log.d("----", "play ${player?.currentPosition} ")
            }
        }
    }

    private fun completeVideo() {
        isComplete = true
        player?.reset()
        image_play_pause.setBackgroundResource(R.drawable.bg_play)
    }

    private fun fadeControl() {
        with(ExtensionUtil()) {
            if (image_play_pause.visibility == VISIBLE) {
                image_play_pause.fadeIn()
                linear_progress.fadeIn()
                linear_top.fadeIn()
            } else {
                image_play_pause.fadeOut()
                linear_progress.fadeOut()
                linear_top.fadeOut()
            }
        }
    }

    private fun updateProgress() {
        val current = player?.currentPosition ?: 0
        seekbar_progress.progress = current
        text_current.text = MediaUtil.getVideoDuration(current / 1000.toLong())
//        Log.d("----", " $current ")
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.reset()
        player?.release()
        handler.removeCallbacks(runnable)
    }
}
