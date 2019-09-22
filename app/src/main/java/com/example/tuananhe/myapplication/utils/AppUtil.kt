package com.example.tuananhe.myapplication.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.res.ResourcesCompat
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager


class AppUtil {
    companion object {
        fun changeTitleBarColor(activity: Activity, color: Int) {
            val window = activity.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ResourcesCompat.getColor(activity.resources, color, activity.theme)
        }

        fun showKeyboard(context: Context, v: View) {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (v.requestFocus()) {
                imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT)
            }
        }

        fun hasPermission(context: Context, permissions: Array<String>?): Boolean {
            if (permissions == null) {
                return false
            }
            for (permission in permissions) {
                if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false
                }
            }
            return true
        }

        fun requestPermission(activity: Activity, permissions: Array<String>, requestCode: Int) {
            ActivityCompat.requestPermissions(activity, permissions, requestCode)
        }

        fun openAppPermissionSetting(context: Context) {
            val intent = Intent(
                    ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:${context.packageName}")
            )
            context.startActivity(intent)
        }
    }
}