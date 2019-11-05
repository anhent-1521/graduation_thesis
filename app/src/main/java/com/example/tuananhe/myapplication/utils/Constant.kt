package com.example.tuananhe.myapplication.utils

class Constant {
    companion object {
        const val TAB_COUNT = 4
        const val TAB_VIDEO = 0
        const val TAB_IMAGE = 1
        const val TAB_EDIT = 2
        const val TAB_SETTING = 3
        const val OVERLAY_PERMISSION = 218
        const val COMMON_PERMISSION = 97

        const val VIDEO_FOLDER = "/video"
        const val IMAGE_DIRECTORY = "/image"

        const val START_COUNTDOWN = "START_COUNTDOWN"
        const val START_RECORD = "START_RECORD"
        const val RECORDING = "RECORDING"
        const val PAUSE_RECORD = "PAUSE_RECORD"
        const val RESUME_RECORD = "RESUME_RECORD"
        const val STOP_RECORD = "STOP_RECORD"
        const val EXIT_RECORD = "EXIT_RECORD"
        const val HIDE_NOTI = "HIDE_NOTI"

        const val EXTRA_RESULT_CODE = "EXTRA_RESULT_CODE"
        const val EXTRA_DATA_INTENT = "EXTRA_DATA_INTENT"
        const val RECORD_REQUEST_CODE = 21

        const val RECORD_STATE_STARTED = 0
        const val RECORD_STATE_PAUSED = 1
        const val RECORD_STATE_RESUMED = 2
        const val RECORD_STATE_STOPPED = 3

        //Setting
        const val SETTING_PREF = "SETTING_PREF"
        const val SETTING = "SETTING"
        const val SETTING_CHANGED = "SETTING_CHANGED"
        const val MBPS = 1000000

    }
}