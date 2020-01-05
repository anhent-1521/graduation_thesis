package com.example.tuananhe.myapplication.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import com.example.tuananhe.myapplication.screen.main.HomeActivity
import com.example.tuananhe.myapplication.screen.transparent.TransparentActivity
import com.example.tuananhe.myapplication.service.record.RecordService
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.lang.Long


class AppUtil {
    companion object {

        const val EXTRA_SHOW_SETTING = "EXTRA_SHOW_SETTING"

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

        fun startHome(context: Context, isSetting: Boolean? = false) {
            val intent = Intent(context, HomeActivity::class.java)
            intent.flags = FLAG_ACTIVITY_NEW_TASK
            intent.putExtra(EXTRA_SHOW_SETTING, isSetting)
            context.startActivity(intent)
        }

        fun startTransparentRecord(context: Context, action: String) {
            val intent = Intent(context, TransparentActivity::class.java)
            intent.flags = FLAG_ACTIVITY_NEW_TASK
            intent.action = action
            context.startActivity(intent)
        }

        fun controlRecord(context: Context, action: String) {
            val intent = Intent(context, RecordService::class.java)
            intent.action = action
            context.startService(intent)
        }

        fun takeScreenShot(activity: Activity) {
            val view = activity.window.decorView.rootView
            activity.resources.displayMetrics.widthPixels
            try {
                val bitmap = Bitmap.createBitmap(activity.resources.displayMetrics.widthPixels,
                        activity.resources.displayMetrics.heightPixels, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap)
                view.draw(canvas)

                val setting = Settings.getSetting()
                val screenshot = File(setting.rootDirectory, "Screenshot"
                        + Long.toHexString(System.currentTimeMillis()) + ".png")

                val outputStream = FileOutputStream(screenshot)
                val quality = 100
                bitmap.compress(Bitmap.CompressFormat.PNG, quality, outputStream)
                outputStream.flush()
                outputStream.close()
            } catch (e: Exception) {
                Log.d("okokokok", e.message)
            } finally {
                activity.finish()
            }
        }
    }
}