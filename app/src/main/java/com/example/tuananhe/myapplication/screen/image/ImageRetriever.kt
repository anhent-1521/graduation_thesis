package com.example.tuananhe.myapplication.screen.image

import android.net.Uri
import android.provider.MediaStore
import com.example.tuananhe.myapplication.data.model.Image
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileFilter


class ImageRetriever(private var view: ImageContract.View) : ImageContract.Presenter {

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

    private fun extractImages(files: Array<File>): ArrayList<Image> {
        val images = ArrayList<Image>()
        try {
            for (file in files) {
                val bitmap =
                    MediaStore.Images.Media.getBitmap(view.getContentResolver(), Uri.fromFile(file))
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