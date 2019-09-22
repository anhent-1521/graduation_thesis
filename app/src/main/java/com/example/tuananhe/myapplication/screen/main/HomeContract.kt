package com.example.tuananhe.myapplication.screen.main

import android.content.Context

interface HomeContract {

    interface View {
        fun getContext(): Context

        fun showOverlayDialog()

        fun startBubbleView()

        fun showOverlaySetting()

    }

    interface Presenter {

        fun checkRequiredPermission(permissions: Array<String>, requestCode: Int)

        fun checkOverlayPermission()

    }
}