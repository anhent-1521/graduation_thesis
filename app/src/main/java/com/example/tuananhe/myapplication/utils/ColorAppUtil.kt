package com.example.tuananhe.myapplication.utils

import android.app.Activity
import android.support.v4.content.res.ResourcesCompat
import android.view.WindowManager

class ColorAppUtil {
    companion object {
        fun changeTitleBarColor(activity: Activity, color: Int) {
            val window = activity.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ResourcesCompat.getColor(activity.resources, color, activity.theme)
        }
    }
}