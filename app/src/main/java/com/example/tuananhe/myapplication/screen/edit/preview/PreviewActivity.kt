package com.example.tuananhe.myapplication.screen.edit.preview

import android.content.pm.ActivityInfo
import android.graphics.Point
import android.media.MediaPlayer
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.SeekBar
import com.example.tuananhe.myapplication.BaseActivity
import com.example.tuananhe.myapplication.EditInfo
import com.example.tuananhe.myapplication.R
import com.example.tuananhe.myapplication.data.ItemEdit
import com.example.tuananhe.myapplication.data.model.Video
import com.example.tuananhe.myapplication.utils.ExtensionUtil
import com.example.tuananhe.myapplication.utils.FileUtil
import com.example.tuananhe.myapplication.utils.MediaUtil
import com.example.tuananhe.myapplication.utils.view.dialog.CommonDialog
import kotlinx.android.synthetic.main.activity_detail_video.*
import kotlinx.android.synthetic.main.activity_detail_video.image_play_pause
import kotlinx.android.synthetic.main.activity_detail_video.seekbar_progress
import kotlinx.android.synthetic.main.activity_detail_video.video_view
import kotlinx.android.synthetic.main.activity_rotate.*

class PreviewActivity : BaseActivity() {

    companion object {
        const val CONTROL_DELAY = 3000L
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
    private var screenWidth = 0
    private var screenHeight = 0
    private var screenRatio = 0f
    private var realWidth = 0
    private var realHeight = 0
    private var realRatio = 0f
    private var editInfo: EditInfo = EditInfo(speed = "1")
    private var editType: String = ItemEdit.TRIM
    private var start = 0
    private var end = 0

    override fun getLayoutResId(): Int = R.layout.activity_detail_video

    override fun initViews() {
        video = intent.getParcelableExtra(VIDEO_EXTRA)
        editInfo = intent.getParcelableExtra(EDIT_INFO_EXTRA)
        editType = editInfo.editType
        start = editInfo.start.toInt() * 1000
        end = editInfo.end.toInt() * 1000

        getSize()
        setScreenOrientation()

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
            player?.let { player ->
                if (player.isPlaying) {
                    onPlayPauseClicked()
                }
            }
            openShare()
        }
        image_delete.setOnClickListener {
            player?.let { player ->
                if (player.isPlaying) {
                    onPlayPauseClicked()
                }
            }
            deleteVideo(video)
        }
    }

    override fun initComponents() {
        playVideo()
    }

    private fun getSize() {
        val screenPoint = Point()
        val display = windowManager.defaultDisplay
        display.getRealSize(screenPoint)
        screenWidth = screenPoint.x
        screenHeight = screenPoint.y
        screenRatio = screenWidth / screenHeight.toFloat()

        realWidth = video?.width ?: screenWidth
        realHeight = video?.height ?: screenHeight
        realRatio = realWidth / realHeight.toFloat()
    }

    private fun setScreenOrientation() {
        if (realWidth >= realHeight) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE
            val marginParam = linear_progress.layoutParams as ViewGroup.MarginLayoutParams
            marginParam.bottomMargin =
                    resources.getDimensionPixelOffset(R.dimen.seek_bar_padding_bottom)
        }
    }

    private fun openShare() {
        FileUtil.shareImage(this, video?.path)
    }

    private fun deleteVideo(video: Video?) {
        val dialog = CommonDialog(
                this, getString(R.string.dialog_delete_video_title)
        )
        dialog.optimisticListener = {
            FileUtil.deleteFile(video?.path)
            finish()
        }
        dialog.show()
    }

    private fun playVideo() {
        video_view.setVideoPath(video?.path)
        video_view?.setOnPreparedListener {
            player = it
            setVideoSize()
            setStartPosition()
            initControl()
            progressHandler.post(progressRunnable)
            controlHandler.postDelayed(controlRunnable, CONTROL_DELAY)
            player?.setOnCompletionListener { completeVideo() }
        }
    }

    private fun setStartPosition() {
        if (editType == ItemEdit.TRIM) {
            player?.seekTo(start)
            player?.setOnSeekCompleteListener {
                video_view.seekTo(start)
                video_view.start()
            }
        }
    }

    private fun setEndPosition() {
        if (editType == ItemEdit.TRIM && player!!.currentPosition >= end) {
            player?.seekTo(start)
            player?.pause()
            image_play_pause.setBackgroundResource(R.drawable.bg_play)
        }
    }

    private fun setVideoSize() {
        val param = video_view.layoutParams
        if (realWidth >= realHeight) {
            if (realHeight >= screenHeight) {
                param.width = if (realWidth >= screenWidth) screenWidth else realWidth
                param.height = screenHeight
            } else {
                val width = (realWidth / realHeight.toFloat() * screenHeight).toInt()
                param.width = if (width >= screenWidth) screenWidth else width
                param.height = screenHeight
            }
        } else if (screenWidth / realWidth > 2) {
            if (screenHeight / realHeight > 3) {
                param.width = screenWidth
                param.height = (realHeight / realWidth.toFloat() * screenWidth).toInt()
            } else {
                param.width = (realWidth / realHeight.toFloat() * screenHeight).toInt()
                param.height = screenHeight
            }
        } else {
            param.width = screenWidth
            param.height = (realHeight / realWidth.toFloat() * screenWidth).toInt()
        }

        video_view.layoutParams = param
    }

    private fun initControl() {
        val max = end - start
        seekbar_progress.max = max
        text_duration.text = MediaUtil.getVideoDuration(max / 1000.toLong())
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
        player?.let { player ->
            if (player.isPlaying) {
                player.pause()
                progressHandler.removeCallbacks(progressRunnable)
                controlHandler.removeCallbacks(controlRunnable)
                image_play_pause.setBackgroundResource(R.drawable.bg_play)
            } else {
                progressHandler.post(progressRunnable)
                controlHandler.postDelayed(controlRunnable, CONTROL_DELAY)
                player.start()
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
        setEndPosition()
        var current = player!!.currentPosition - start
        if (current < 0) current = 0
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
