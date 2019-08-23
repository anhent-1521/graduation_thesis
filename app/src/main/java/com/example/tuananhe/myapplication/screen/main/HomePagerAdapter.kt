package com.example.tuananhe.myapplication.screen.main

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.example.tuananhe.myapplication.screen.image.ImageFragment
import com.example.tuananhe.myapplication.screen.video.VideoFragment
import com.example.tuananhe.myapplication.utils.Constant.Companion.TAB_COUNT
import com.example.tuananhe.myapplication.utils.Constant.Companion.TAB_IMAGE
import com.example.tuananhe.myapplication.utils.Constant.Companion.TAB_VIDEO

class HomePagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            TAB_VIDEO -> VideoFragment()
            TAB_IMAGE -> ImageFragment()
            else -> Fragment()
        }
    }

    override fun getCount(): Int = TAB_COUNT
}