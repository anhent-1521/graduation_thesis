package com.example.tuananhe.myapplication.utils.view.dialog

import android.content.Context
import android.content.DialogInterface
import android.support.v7.app.AlertDialog
import com.example.tuananhe.myapplication.R
import com.example.tuananhe.myapplication.utils.Settings

class FpsDialog(context: Context) : BaseSettingDialog(context) {

    override fun getDialog(): AlertDialog {
        val settings = Settings.getSetting()
        val currentFps = settings.fps
        var checked = -1

        val fpsArray = context.resources.getStringArray(R.array.fps)
        for (i in fpsArray.indices) {
            val fps = fpsArray[i]
            if (fps.contains(currentFps.toString())) {
                checked = i
                break
            }
        }

        val builder = AlertDialog.Builder(context)
                .setSingleChoiceItems(R.array.fps, checked, DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss()
                    val fps = Integer.parseInt(fpsArray[which].substring(0, fpsArray[which].lastIndexOf(' ')))
                    settings.fps = fps
                    Settings.saveSetting(settings)
                })
                .setPositiveButton(R.string.cancel, DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })

        val dialog = builder.create()
        dialog.setTitle(R.string.select_fps)

        return dialog
    }
}
