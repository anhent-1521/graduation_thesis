package com.example.tuananhe.myapplication.screen.main

import android.app.Activity
import android.os.Build
import android.provider.Settings
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

}
