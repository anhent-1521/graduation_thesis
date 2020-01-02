package com.example.tuananhe.myapplication.screen.all_image

import android.content.Context
import com.example.tuananhe.myapplication.data.model.Image
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AllImagePresenter(val view: AllImageContract.View) : AllImageContract.Presenter {
    override fun getImages(context: Context) {
        var images: ArrayList<Image>
        CoroutineScope(Dispatchers.IO).launch {
            images = ImageHelper.getImages(context)
            withContext(Dispatchers.Main) {
                view.onGetImages(images)
            }
        }
    }
}