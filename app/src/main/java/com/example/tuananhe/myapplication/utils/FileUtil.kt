package com.example.tuananhe.myapplication.utils

import android.content.Context
import android.content.Intent
import android.os.Environment
import android.support.v4.content.FileProvider
import java.io.File

class FileUtil {
    companion object {

        private fun getIntent(): Intent {
            val intent = Intent(Intent.ACTION_SEND)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            return intent
        }

        fun shareVideo(context: Context?, path: String?) {
            if (path == null || context == null) return
            val uri = FileProvider.getUriForFile(context, "com.file.provider", File(path))
            val intent = getIntent()
            intent.type = "video/*"
            intent.putExtra(Intent.EXTRA_STREAM, uri)
            context.startActivity(Intent.createChooser(intent, "Share video using"))
        }

        fun shareImage(context: Context, path: String?) {
            if (path == null) return
            val uri = FileProvider.getUriForFile(context, "com.file.provider", File(path))
            val intent = getIntent()
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_STREAM, uri)
            context.startActivity(Intent.createChooser(intent, "Share image using"))
        }

        fun deleteFile(path: String?) {
            if (path == null) return
            val file = File(path)
            if (file.exists()) {
                file.delete()
            }
        }

        fun renameVideo(path: String?, newName: String?): File {
            val directory = Settings.getSetting().rootDirectory
            val old = File(path)
            val file = File(directory, newName)
            if (old.exists()) {
                old.renameTo(file)
                return file
            }
            return old
        }

        fun checkIfHaveSDCard(): Boolean {
            val isSDPresent =
                Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
            val isSDSupportedDevice = Environment.isExternalStorageRemovable()
            return isSDSupportedDevice && isSDPresent
        }
    }
}