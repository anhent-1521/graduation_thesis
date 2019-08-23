package com.example.tuananhe.myapplication.screen.main

import android.support.v4.view.ViewPager
import com.example.tuananhe.myapplication.BaseActivity
import com.example.tuananhe.myapplication.R
import com.example.tuananhe.myapplication.utils.Constant.Companion.TAB_COUNT
import com.example.tuananhe.myapplication.utils.Constant.Companion.TAB_EDIT
import com.example.tuananhe.myapplication.utils.Constant.Companion.TAB_IMAGE
import com.example.tuananhe.myapplication.utils.Constant.Companion.TAB_SETTING
import com.example.tuananhe.myapplication.utils.Constant.Companion.TAB_VIDEO
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : BaseActivity() {

    private var homePagerAdapter: HomePagerAdapter? = null

    override fun getLayoutResId(): Int = R.layout.activity_home

    override fun getTitleBarColorId(): Int = R.color.color_red_light

    override fun initViews() {
        homePagerAdapter = HomePagerAdapter(supportFragmentManager)
        view_pager.adapter = homePagerAdapter
        view_pager.offscreenPageLimit = TAB_COUNT
        tab_layout.setupWithViewPager(view_pager)
        setupTabIcon()
        view_pager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setupTabIcon()
            }
        })
    }

    override fun initComponents() {

    }

    private fun setupTabIcon() {
        val selected = tab_layout.selectedTabPosition
        tab_layout.getTabAt(TAB_VIDEO)
            ?.setIcon(if (selected == TAB_VIDEO) R.drawable.ic_video_active else R.drawable.ic_video)
        tab_layout.getTabAt(TAB_IMAGE)
            ?.setIcon(if (selected == TAB_IMAGE) R.drawable.ic_images_active else R.drawable.ic_images)
        tab_layout.getTabAt(TAB_EDIT)
            ?.setIcon(if (selected == TAB_EDIT) R.drawable.ic_effect_active else R.drawable.ic_effect)
        tab_layout.getTabAt(TAB_SETTING)
            ?.setIcon(if (selected == TAB_SETTING) R.drawable.ic_options_active else R.drawable.ic_options)
    }
}