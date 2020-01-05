package com.example.tuananhe.myapplication.screen.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.tuananhe.myapplication.screen.edit.EditFragment
import com.example.tuananhe.myapplication.screen.image.ImageFragment
import com.example.tuananhe.myapplication.screen.setting.SettingFragment
import com.example.tuananhe.myapplication.screen.video.VideoFragment
import com.example.tuananhe.myapplication.utils.Constant.Companion.TAB_COUNT
import com.example.tuananhe.myapplication.utils.Constant.Companion.TAB_EDIT
import com.example.tuananhe.myapplication.utils.Constant.Companion.TAB_IMAGE
import com.example.tuananhe.myapplication.utils.Constant.Companion.TAB_SETTING
import com.example.tuananhe.myapplication.utils.Constant.Companion.TAB_VIDEO

class HomePagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            TAB_VIDEO -> VideoFragment()
            TAB_IMAGE -> ImageFragment()
            TAB_EDIT -> EditFragment()
            TAB_SETTING -> SettingFragment()
            else -> Fragment()
        }
    }

    override fun getCount(): Int = TAB_COUNT
}