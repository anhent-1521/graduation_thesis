package com.example.tuananhe.myapplication.screen.edit.trim

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.util.Log
import com.example.tuananhe.myapplication.data.model.Video
import wseemann.media.FFmpegMediaMetadataRetriever
import java.util.ArrayList
import kotlin.math.log

class TrimPresenter(val view: TrimContract.View) : TrimContract.Presenter {

    override fun getListBitmap(video: Video) {
        var retriever: FFmpegMediaMetadataRetriever? = null
        val bitmaps = ArrayList<Bitmap>()
        try {
            retriever = FFmpegMediaMetadataRetriever()
            retriever.setDataSource(video.path)
            for (i in 0..5) {
                var timeUs = i * video.duration * 100
                Log.d("====", "$timeUs")
                if (timeUs == 0L) {
                    timeUs = 100000L
                }
                val bitmap = retriever.getFrameAtTime(timeUs,
                        FFmpegMediaMetadataRetriever.OPTION_CLOSEST_SYNC)
                if (bitmap != null) {
                    bitmaps.add(bitmap)
                }
                if (bitmaps.size == 10) {
                    view.onGetListBitMap(bitmaps)
                }
            }
        } catch (e: IllegalArgumentException) {
            Log.d("====", e.toString())
        }
        retriever?.release()
    }

    override fun trimVideo() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}