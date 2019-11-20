package com.example.tuananhe.myapplication.utils.view.dialog

import android.content.Context
import android.support.v7.app.AlertDialog
import com.example.tuananhe.myapplication.R
import com.example.tuananhe.myapplication.utils.Settings

class BitrateDialog(context: Context) : BaseSettingDialog(context) {

    override fun getDialog(): AlertDialog {
        val settings = Settings.getSetting()
        val currentBitrate = settings.bitrate / 1000000
        var checked = -1

        val bitrateArray = context.resources.getStringArray(R.array.bitrate)
        for (i in bitrateArray.indices) {
            if (currentBitrate == 0) {
                checked = 0
                break
            }

            val bitrate = bitrateArray[i]
            if (bitrate.contains(currentBitrate.toString())) {
                checked = i
                break
            }
        }

        val builder = AlertDialog.Builder(context)
                .setSingleChoiceItems(R.array.bitrate, checked) { dialog, which ->
                    dialog.dismiss()
                    var bitrate: Int
                    if (which == 0) {
                        bitrate = 0
                    } else {
                        bitrate = Integer.parseInt(bitrateArray[which].substring(0, bitrateArray[which].lastIndexOf(' ')))
                        bitrate *= 1000000
                    }

                    settings.bitrate = bitrate
                    Settings.saveSetting(settings)
                }
                .setPositiveButton(R.string.cancel) { dialog, which -> dialog.dismiss() }

        val dialog = builder.create()
        dialog.setTitle(R.string.select_bitrate)

        return dialog
    }
}