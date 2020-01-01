package com.example.tuananhe.myapplication.screen.edit.merge_video

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.tuananhe.myapplication.BaseEditActivity
import com.example.tuananhe.myapplication.EditInfo
import com.example.tuananhe.myapplication.R
import com.example.tuananhe.myapplication.data.model.Video
import com.example.tuananhe.myapplication.screen.detail_video.DetailVideoActivity
import com.example.tuananhe.myapplication.utils.ExtensionUtil
import com.example.tuananhe.myapplication.utils.FileUtil
import com.example.tuananhe.myapplication.utils.MediaUtil
import com.example.tuananhe.myapplication.utils.view.dialog.CommonDialog
import kotlinx.android.synthetic.main.activity_merge_video.*
import kotlinx.android.synthetic.main.layout_progress_edit.*

class MergeVideoActivity : BaseEditActivity(), MergeContract.View {

    private val presenter = MergePresenter(this)
    private var introPath = ""
    private var outroPath = ""
    private var videoType = 0
    private var extraDuration = 0

    companion object {
        const val CHOOSE_VIDEO_CODE = 218
        const val INTRO = 1
        const val OUTRO = 2
    }

    override fun initView() {
        presenter.initFFmpeg()
        with(ExtensionUtil()) {
            text_add_intro.changePrimaryStyle(this@MergeVideoActivity)
            text_add_outro.changePrimaryStyle(this@MergeVideoActivity)
        }
        text_add_intro.setOnClickListener { onClickAddVideo(INTRO) }
        text_add_outro.setOnClickListener { onClickAddVideo(OUTRO) }
        text_name_outro.visibility = View.INVISIBLE
        text_name_intro.visibility = View.INVISIBLE
    }

    private fun onClickAddVideo(type: Int) {
        val intent = Intent()
        intent.type = "audio/mpeg"
        intent.action = Intent.ACTION_PICK
        startActivityForResult(intent, CHOOSE_VIDEO_CODE)
        videoType = type
    }

    override fun getEditTitle(): String = "Add Outro & Intro"

    override fun setItemChoose() {
        presenter.getVideo(video)
    }

    override fun onClickPreview() {

    }

    override fun onClickCancel() {
        presenter.cancel()
        hideProgress()
    }

    override fun onClickSave() {
        presenter.goGetExtraDuration(extraDuration)
        presenter.doEdit(EditInfo(introPath = introPath, outroPath = outroPath))
    }

    override fun getLayoutResId(): Int = R.layout.activity_merge_video

    override fun onGetVideoSuccess(video: Video?) {
        video?.let {
            var imageView: ImageView
            if (videoType == INTRO) {
                introPath = video.path ?: ""
                text_duration_intro.text = MediaUtil.getVideoDuration(it.duration)
                text_name_intro.visibility = View.VISIBLE
                text_name_intro.text = it.name
                imageView = image_intro
            } else {
                outroPath = video.path ?: ""
                text_duration_outro.text = MediaUtil.getVideoDuration(it.duration)
                text_name_outro.visibility = View.VISIBLE
                text_name_outro.text = it.name
                imageView = image_outro
            }
            Glide.with(this)
                    .load(it.path)
                    .centerCrop()
                    .into(imageView)
            extraDuration += it.duration.toInt()

        }
    }

    override fun showAudioDifferent(message: String) {
        val dialog = CommonDialog(this, message, false)
        dialog.optimisticListener = { dialog.dismiss() }
        dialog.show()
    }

    override fun provideContext(): Context = this

    override fun onEditStart() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onEditEnd() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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

    override fun onSuccess(video: Video?) {
        if (video != null) {
            Handler().postDelayed({
                startActivity(
                        getVideoActivityIntent(
                                this,
                                video,
                                DetailVideoActivity::class.java
                        )
                )
                finish()
            }, 100)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CHOOSE_VIDEO_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val a = data.data
            a?.let {
                presenter.doExtractVideo(it)
            }
        }
    }
}
