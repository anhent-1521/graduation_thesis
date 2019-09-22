package com.example.tuananhe.myapplication.screen.image

import android.net.Uri
import android.provider.MediaStore
import com.example.tuananhe.myapplication.data.model.Image
import com.example.tuananhe.myapplication.utils.AppUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileFilter


class ImagePresenter(private var view: ImageContract.View) : ImageContract.Presenter {

    private val fileFilter = FileFilter { file ->
        file.name.toLowerCase().endsWith("png") or file.name.toLowerCase().endsWith("jpeg")
    }

    override fun loadImages(directory: String) {
        try {
            val outputFile = File(directory)
            val files: Array<File> =
                    outputFile.listFiles() //crash nếu không xin quyền trước

            var images: ArrayList<Image>
            CoroutineScope(Dispatchers.IO).launch {
                images = extractImages(files)
                withContext(Dispatchers.Main) {
                    view.onGetImageSuccess(images)
                }
            }
        } catch (e: Exception) {
            view.onGetImageFail()
            e.printStackTrace()
        }
    }

    override fun checkPermission(permissions: Array<String>) {
        view.getContext()?.let {
            if (AppUtil.hasPermission(it, permissions)) {
                view.hideRemindPermission()
                view.onGetImage()
                return
            }
            view.showRemindPermission()
        }
    }

    override fun requestPermission(permissions: Array<String>, requestCode: Int) {
    }

    private fun extractImages(files: Array<File>): ArrayList<Image> {
        val images = ArrayList<Image>()
        try {
            for (file in files) {
                val bitmap =
                        MediaStore.Images.Media.getBitmap(view.getContext()?.contentResolver, Uri.fromFile(file))
                val width = bitmap.width
                val height = bitmap.height

                val image = Image(file.name, file.path, width, height)
                images.add(image)
            }
        } catch (e: Exception) {
            view.onGetImageFail()
            e.printStackTrace()
        }
        return images
    }

}