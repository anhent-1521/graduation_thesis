package com.example.tuananhe.myapplication

import android.content.Context
import android.os.Parcelable
import com.example.tuananhe.myapplication.data.model.Video
import kotlinx.android.parcel.Parcelize

interface View {
    fun provideContext(): Context
    fun onEditStart()
    fun onEditEnd()
    fun showProgress()
    fun hideProgress()
    fun updateProgress(progress: Int)
    fun onSuccess(video: Video?)
}

interface Presenter {
    fun initFFmpeg()
    fun getVideo(video: Video)
    fun doEdit(editInfo: EditInfo)
    fun cancel()
    fun onDestroy()
    fun previewEditVideo()
}

@Parcelize
data class EditInfo(
        val start: String = "",
        val end: String = "",
        val duration: String = "",
        val speed: String = "",
        val rotate: String = "",
        val editType: String = "",
        val introPath: String = "",
        val outroPath: String = ""
) : Parcelable

