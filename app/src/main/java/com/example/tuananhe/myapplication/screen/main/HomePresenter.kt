package com.example.tuananhe.myapplication.screen.main

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.provider.Settings
import android.support.v4.content.ContextCompat
import android.view.WindowManager
import com.example.tuananhe.myapplication.floating_bubble.floatingview.FloatingViewManager
import com.example.tuananhe.myapplication.service.ChatHeadService
import com.example.tuananhe.myapplication.utils.AppUtil

class HomePresenter(private val view: HomeContract.View) : HomeContract.Presenter {

    override fun checkRequiredPermission(permissions: Array<String>, requestCode: Int) {
        if (!AppUtil.hasPermission(view.getContext(), permissions)) {
            AppUtil.requestPermission(view.getContext() as Activity, permissions, requestCode)
        } else {
            checkOverlayPermission()
        }
    }

    override fun checkOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(view.getContext())) {
                view.startBubbleView()
            } else {
                view.showOverlayDialog()
            }
        }
    }

    override fun startBubble(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // 1. 'windowLayoutInDisplayCutoutMode' do not be set to 'never'
            if (activity.window.attributes.layoutInDisplayCutoutMode == WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER) {
                //throw RuntimeException("'windowLayoutInDisplayCutoutMode' do not be set to 'never'")
            }
            // 2. Do not set Activity to landscape
            if (activity.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                //throw RuntimeException("Do not set Activity to landscape")
            }
        }

        val key: String = ChatHeadService.EXTRA_CUTOUT_SAFE_AREA
        val intent = Intent(activity, ChatHeadService::class.java)
        intent.putExtra(key, FloatingViewManager.findCutoutSafeArea(activity))
        ContextCompat.startForegroundService(activity, intent)
    }

}
