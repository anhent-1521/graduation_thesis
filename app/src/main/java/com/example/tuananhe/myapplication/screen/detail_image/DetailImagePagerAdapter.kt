package com.example.tuananhe.myapplication.screen.detail_image

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.example.tuananhe.myapplication.data.model.Image

class DetailImagePagerAdapter(fm: FragmentManager, private val images: List<Image>) :
    FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment =
        DetailImageFragment.newInstance(images[position])

    override fun getCount(): Int = images.size

}
