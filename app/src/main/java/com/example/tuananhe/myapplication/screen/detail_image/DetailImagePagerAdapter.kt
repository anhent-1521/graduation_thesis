package com.example.tuananhe.myapplication.screen.detail_image

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.tuananhe.myapplication.data.model.Image

class DetailImagePagerAdapter(fm: FragmentManager, private val images: List<Image>) :
    FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment =
        DetailImageFragment.newInstance(images[position])

    override fun getCount(): Int = images.size

}
