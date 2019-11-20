package com.example.tuananhe.myapplication.utils.view.dialog

import android.content.Context
import android.support.v7.app.AlertDialog
import com.example.tuananhe.myapplication.R
import com.example.tuananhe.myapplication.utils.Settings
import com.example.tuananhe.myapplication.utils.Settings.Companion.DEFAULT_VIDEO_DIRECTORY
import com.example.tuananhe.myapplication.utils.Settings.Companion.SDCARD_VIDEO_DIRECTORY

class LocationDialog (context: Context) : BaseSettingDialog(context) {

    override fun getDialog(): AlertDialog {
        val settings = Settings.getSetting()
        val currentLocation = settings.rootDirectory
        var checked = -1

        val locations = arrayOf(DEFAULT_VIDEO_DIRECTORY, SDCARD_VIDEO_DIRECTORY)
        for (i in locations.indices) {
            val fps = locations[i]
            if (fps.contains(currentLocation)) {
                checked = i
                break
            }
        }
        val builder = AlertDialog.Builder(context)
                .setSingleChoiceItems(locations, checked) { dialog, which ->
                    dialog.dismiss()
                    val location = locations[which]
                    settings.rootDirectory = location
                    Settings.saveSetting(settings)
                }
                .setPositiveButton(R.string.cancel) { dialog, _ -> dialog.dismiss() }

        val dialog = builder.create()
        dialog.setTitle(R.string.select_location)

        return dialog
    }

}