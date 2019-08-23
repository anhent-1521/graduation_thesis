package com.example.tuananhe.myapplication.screen.detail_image

import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.tuananhe.myapplication.BaseFragment
import com.example.tuananhe.myapplication.R
import com.example.tuananhe.myapplication.data.model.Image
import kotlinx.android.synthetic.main.fragment_detail_image.*

class DetailImageFragment : BaseFragment() {

    companion object {
        fun newInstance(image: Image): DetailImageFragment {
            val fragment = DetailImageFragment()
            val bunde = Bundle()
            bunde.putParcelable("image", image)
            fragment.arguments = bunde
            return fragment
        }
    }

    override fun getLayoutResId(): Int = R.layout.fragment_detail_image

    override fun initViews() {
        image_thumbnail.setOnClickListener {
           (activity as DetailImageActivity).setControlVisible()
        }

        val image = arguments?.getParcelable<Image>("image")
        if (image != null) {
            context?.let {
                Glide.with(it).load(image.path as String?).into(image_thumbnail)
            }
        }
    }

}
