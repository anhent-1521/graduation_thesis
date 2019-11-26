package com.example.tuananhe.myapplication.screen.edit.rotate

import android.content.Context
import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.util.Log
import android.view.Surface
import android.view.TextureView
import android.view.View
import android.widget.LinearLayout
import android.widget.SeekBar
import com.example.tuananhe.myapplication.BaseActivity
import com.example.tuananhe.myapplication.EditInfo
import com.example.tuananhe.myapplication.R
import com.example.tuananhe.myapplication.data.model.Video
import com.example.tuananhe.myapplication.screen.detail_video.DetailVideoActivity
import com.example.tuananhe.myapplication.utils.ExtensionUtil
import com.example.tuananhe.myapplication.utils.MediaUtil
import kotlinx.android.synthetic.main.activity_rotate.*
import kotlinx.android.synthetic.main.layout_header_edit.*
import kotlinx.android.synthetic.main.layout_progress_edit.*
import java.io.IOException

class RotateActivity : BaseActivity(), RotateContract.View,
        TextureView.SurfaceTextureListener {

    override fun initComponents() {
        getVideo()
        video_view.surfaceTextureListener = this
    }

    override fun showProgress() {
        frame_progress.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        frame_progress.visibility = View.GONE
    }

    override fun updateProgress(pro: Int) {
        progress.setCurrentProgress(pro.toDouble())
    }

    lateinit var video: Video
    var player: MediaPlayer? = null
    var surface: Surface? = null
    private val presenter = RotatePresenter(this)
    private var rotate = 0
    var isComplete = false
    var progressHandler = Handler()
    private var progressRunnable = object : Runnable {
        override fun run() {
            runOnUiThread {
                updateProgress()
            }
            progressHandler.post(this)
        }
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {
    }

    override fun onSurfaceTextureUpdated(surfaceTexture: SurfaceTexture?) {
    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean = false

    override fun onSurfaceTextureAvailable(s: SurfaceTexture?, width: Int, height: Int) {
        surface = Surface(s)
        playVideo(surface)
    }

    override fun initViews() {
        presenter.initFFmpeg()
        with(ExtensionUtil()) {
            text_rotate.changePrimaryStyle(this@RotateActivity)
        }
        text_rotate.setOnClickListener { onClickRotate() }
        image_back.setOnClickListener { onBackPressed() }
        text_cancel.setOnClickListener { onClickCancel() }
        text_save.setOnClickListener { onClickSave() }
        image_play_pause.setOnClickListener { onPlayPauseClicked() }
        seekbar_progress.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (isComplete) {
                    seekBar?.progress = seekBar?.max ?: 0
                    return
                }
                player?.seekTo(seekBar?.progress ?: 0)
            }

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            }
        })
    }

    override fun getLayoutResId(): Int = R.layout.activity_rotate

    override fun provideContext(): Context = this

    override fun onEditStart() {
    }

    override fun onEditEnd() {
    }


    override fun onSuccess(video: Video?) {
        if (video != null) {
            Handler().postDelayed({
                startActivity(getVideoActivityIntent(this,
                        video,
                        DetailVideoActivity::class.java))
                finish()
            }, 100)
        }
    }

    private fun onClickRotate() {
        video_view.rotation += 90f
        rotate++
    }

    private fun onClickCancel() {
        presenter.cancel()
        hideProgress()
    }

    private fun onClickSave() {
        presenter.doEdit(EditInfo(rotate = rotate.toString()))
    }

    private fun getVideo() {
        video = intent.getParcelableExtra(VIDEO_EXTRA)
        text_end.text = MediaUtil.getVideoDuration(video.duration)
        presenter.getVideo(video)
    }

    private fun playVideo(surface: Surface?) {
        try {
            if (player == null) {
                player = MediaPlayer()
                player?.setDataSource(this, Uri.parse(video.path))
                player?.setSurface(surface)
                player?.prepare()
            }
            player?.setOnPreparedListener {
                calculateVideoViewSize()
                seekbar_progress.max = player?.duration ?: 0
                image_play_pause.setBackgroundResource(R.drawable.ic_pause_video)
                progressHandler.post(progressRunnable)
                player?.start()
            }
            player?.setOnCompletionListener { completeVideo() }
        } catch (e: IOException) {
            Log.d("====", e.message)
        }
    }

    private fun calculateVideoViewSize() {
        val videoH: Int = player?.videoHeight ?: 0
        val videoW: Int = player?.videoWidth ?: 0
        var viewH: Int = video_view.height
        var viewW: Int = video_view.width
        val rate = videoH.toFloat() / videoW
        if (rate > 1) {
            viewW = (viewH / rate).toInt()
        } else {
            viewH = (viewW * rate).toInt()
        }
        val params = LinearLayout.LayoutParams(viewW, viewH)
        video_view.layoutParams = params
    }

    private fun onPlayPauseClicked() {
        if (isComplete) {
            isComplete = false
            playVideo(surface)
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
        text_start.text = MediaUtil.getVideoDuration(current / SECOND_UNIT)
    }

    private fun completeVideo() {
        progressHandler.removeCallbacks(progressRunnable)
        isComplete = true
        player?.seekTo(100)
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

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
        player = null
    }
}