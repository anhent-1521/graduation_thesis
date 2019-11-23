package com.example.tuananhe.myapplication

import android.content.Context
import com.example.tuananhe.myapplication.data.model.Video

interface View {
    fun provideContext(): Context
    fun onEditStart()
    fun onEditEnd()
    fun showProgress()
    fun hideProgress()
    fun updateProgress(progress: Int)
    fun onSuccess()
}

interface Presenter {
    fun initFFmpeg()
    fun getVideo(video: Video)
    fun doEdit(editInfo: EditInfo)
    fun cancel()
    fun onDestroy()
}

data class EditInfo(
        val start: String? = null,
        val end: String? = null,
        val duration: String? = null,
        val speed: String? = null
)

