package com.example.tuananhe.myapplication.utils.setting

import android.text.TextUtils

enum class Resolution private constructor(var width: Int, var height: Int) {
    R240P(240, 426), R360P(360, 640), R480P(480, 854),
    R540P(540, 960), R720P(720, 1280), R1080P(1080, 1920),
    R1440P(1440, 2560);

    override fun toString(): String {
        return when (this) {
            R720P -> "720p"
            R1080P -> "1080p"
            R240P -> "240p"
            R360P -> "360p"
            R480P -> "480p"
            R540P -> "540p"
            R1440P -> "1440p"
        }
    }

    companion object {

        fun getResolution(resolution: String): Resolution {
            var resolution = resolution
            resolution = if (TextUtils.isEmpty(resolution)) "" else resolution
            return when (resolution) {
                "720p" -> R720P
                "1080p" -> R1080P
                "240p" -> R240P
                "360p" -> R360P
                "480p" -> R480P
                "540p" -> R540P
                "1440p" -> R1440P
                else -> throw IllegalArgumentException("Unsupported resolution")
            }
        }
    }
}
