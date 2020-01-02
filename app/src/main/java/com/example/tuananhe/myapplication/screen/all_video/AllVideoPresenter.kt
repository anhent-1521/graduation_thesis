package com.example.tuananhe.myapplication.screen.all_video

import com.example.tuananhe.myapplication.data.model.Video
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AllVideoPresenter(private val view: AllVideoContract.View) : AllVideoContract.Presenter {

    override fun getAllVideo() {
        var videos: ArrayList<Video>
        CoroutineScope(Dispatchers.IO).launch {
            videos = VideoHelper.getVideos(view.provideContext())
            withContext(Dispatchers.Main) {
                view.onGetAllVideo(videos)
            }
        }
    }

}
