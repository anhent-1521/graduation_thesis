package com.example.tuananhe.myapplication.screen.all_image

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.example.tuananhe.myapplication.data.model.Image
import java.io.File

class ImageHelper {
    companion object {

        fun getImages(context: Context): ArrayList<Image> {
            val images = ArrayList<Image>()
            try {
                val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                val projection = arrayOf(MediaStore.Images.Media.DATA)
                val cursor = context.contentResolver.query(uri, projection, null, null, null)
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        val path = cursor.getString(0)
                        val image = extractImage(context, path)
                        if (image != null) {
                            images.add(image)
                        }
                    }
                    cursor.close()
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            return images
        }

        private fun extractImage(context: Context, path: String): Image? {
            try {
                val file = File(path)
                val bitmap =
                    MediaStore.Images.Media.getBitmap(
                        context.contentResolver,
                        Uri.fromFile(file)
                    )
                val width = bitmap.width
                val height = bitmap.height

                return Image(file.name, file.path, width, height)

            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }
    }
}