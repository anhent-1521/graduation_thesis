package com.example.tuananhe.myapplication.utils.view.dialog

import android.content.Context
import android.support.v7.app.AlertDialog
import com.example.tuananhe.myapplication.R
import com.example.tuananhe.myapplication.utils.Settings
import com.example.tuananhe.myapplication.utils.setting.Resolution

class ResolutionDialog(context: Context) : BaseSettingDialog(context) {

    override fun getDialog(): AlertDialog {
        val settings = Settings.getSetting()
        val currentResolution = settings.resolution.toString()
        var checked = -1

        val resolutionArray = context.resources.getStringArray(R.array.resolution)
        for (i in resolutionArray.indices) {
            val res = resolutionArray[i]
            if (res.contains(currentResolution)) {
                checked = i
                break
            }
        }

        val builder = AlertDialog.Builder(context)
                .setSingleChoiceItems(R.array.resolution, checked) { dialog, which ->
                    dialog.dismiss()
                    val resolution = Resolution.getResolution(resolutionArray[which])
                    settings.resolution = resolution
                    Settings.saveSetting(settings)

                }
                .setPositiveButton(R.string.cancel) { dialog, _ -> dialog.dismiss() }

        val dialog = builder.create()
        dialog.setTitle(R.string.select_resolution)

        return dialog
    }
}

