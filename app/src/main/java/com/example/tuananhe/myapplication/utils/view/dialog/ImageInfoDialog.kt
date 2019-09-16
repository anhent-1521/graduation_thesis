package com.example.tuananhe.myapplication.utils.view.dialog

import android.content.Context
import com.example.tuananhe.myapplication.R
import com.example.tuananhe.myapplication.data.model.Image
import com.example.tuananhe.myapplication.utils.MediaUtil
import kotlinx.android.synthetic.main.dialog_image_info.*

class ImageInfoDialog(context: Context, private var image: Image) : BaseDialog(context) {

    private var activity = context

    override fun getLayoutResId(): Int = R.layout.dialog_image_info

    override fun initView() {

        text_location.text =
            String.format("%s %s", context.getString(R.string.text_location_image_info), image.path)
        text_resolution.text =
            String.format(
                "%s %sx%s",
                activity.getString(R.string.text_resolution_image_info),
                image.width,
                image.height
            )
        text_size.text =
            String.format(
                "%s %s",
                activity.getString(R.string.text_size_image_info),
                MediaUtil.getVideoSize(image.path)
            )
    }

    override fun onOptimisticClick() {
        //nothing to do
    }

}
