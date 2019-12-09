package com.example.tuananhe.myapplication.screen.edit.merge_video

import android.net.Uri
import com.example.tuananhe.myapplication.data.model.Video

interface MergeContract {
    interface View : com.example.tuananhe.myapplication.View {
        fun onGetVideoSuccess(video: Video?)
        fun showAudioDifferent(message: String)
    }

    interface Presenter : com.example.tuananhe.myapplication.Presenter {
        fun doExtractVideo(uri: Uri?)
        fun goGetExtraDuration(duration: Int)
    }

}
