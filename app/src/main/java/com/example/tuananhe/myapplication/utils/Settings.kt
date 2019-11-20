package com.example.tuananhe.myapplication.utils

import android.content.Context
import android.content.SharedPreferences
import android.os.Environment
import com.example.tuananhe.myapplication.App
import com.example.tuananhe.myapplication.evenBus.Event
import com.example.tuananhe.myapplication.utils.setting.Resolution
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.greenrobot.eventbus.EventBus
import java.io.File

class Settings {

    companion object {
        private const val DEFAULT_FPS = 30
        private const val DEFAULT_BITRATE = 0
        private val DEFAULT_RESOLUTION = Resolution.R720P
        private const val DEFAULT_COUNT_DOWN = 3
        var DEFAULT_ROOT_DIRECTORY =
                (Environment.getExternalStorageDirectory() as File).canonicalPath.plus("/MoonRecord")
        var DEFAULT_VIDEO_DIRECTORY = DEFAULT_ROOT_DIRECTORY.plus("/video")
        var SDCARD_VIDEO_DIRECTORY = App.getSDCardRoot()

        private val gson = Gson()

        private fun getSharedPreferences(): SharedPreferences =
                App.instance.getSharedPreferences(Constant.SETTING_PREF, Context.MODE_PRIVATE)

        fun saveSetting(setting: Settings, isDispatch: Boolean = true) {
            val preference = getSharedPreferences()
            preference.edit().putString(Constant.SETTING, Gson().toJson(setting)).apply()
            if (isDispatch) {
                EventBus.getDefault().post(Event(Constant.SETTING_CHANGED))
            }
        }

        private fun getSavedSetting(): Settings? {
            val preference = getSharedPreferences()
            val json = preference.getString(Constant.SETTING, null)
            val type = object : TypeToken<Settings>() {}.type
            return gson.fromJson(json, type)
        }

        private fun defaultSetting(): Settings {
            val settings = Settings()
            settings.fps = DEFAULT_FPS
            settings.bitrate = DEFAULT_BITRATE
            settings.resolution = DEFAULT_RESOLUTION
            settings.isRecordAudio = true
            settings.isShowControl = true
            settings.countDown = DEFAULT_COUNT_DOWN
            settings.rootDirectory = DEFAULT_VIDEO_DIRECTORY

            return settings
        }

        fun getSetting(): Settings {
            return getSavedSetting() ?: return defaultSetting()
        }

    }

    var resolution: Resolution? = null
    var fps: Int = 0
    var bitrate: Int = 0
    var countDown: Int = 0

    var isRecordAudio: Boolean = false
    var rootDirectory: String = ""
    var isShowControl: Boolean = false
}
