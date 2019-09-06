package com.example.tuananhe.myapplication.utils

import android.app.Activity
import android.content.Context
import android.support.v4.content.res.ResourcesCompat
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager

class AppUtil {
    companion object {
        fun changeTitleBarColor(activity: Activity, color: Int) {
            val window = activity.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ResourcesCompat.getColor(activity.resources, color, activity.theme)
        }

        fun showKeyboard(context: Context, v: View) {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (v.requestFocus()) {
                imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT)
            }
        }
    }
}