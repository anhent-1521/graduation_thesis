package com.example.tuananhe.myapplication.utils.view.dialog

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v7.app.AppCompatDialog
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.tuananhe.myapplication.R
import com.example.tuananhe.myapplication.data.model.Image
import com.example.tuananhe.myapplication.utils.ExtensionUtil
import kotlinx.android.synthetic.main.dialog_screenshot_preview.*

class ScreenShotPreviewDialog(context: Context,
                              private val image: Image,
                              private val bitmap: Bitmap)
    : AppCompatDialog(context) {

    var listener: ((Unit) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_screenshot_preview)
        try {
            window?.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            )
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window?.attributes?.windowAnimations = R.style.CommonDialogAnimation

            with(ExtensionUtil()) {
                button_close.changeDarkerStyle(this@ScreenShotPreviewDialog.context)
            }
            button_close.setOnClickListener {
                bitmap.recycle()
                dismiss()
                listener?.invoke(Unit)
            }
            text_name.text = image.name
            Glide.with(context)
                    .load(bitmap)
                    .centerCrop()
                    .into(image_screenshot)
        } catch (e: Exception) {

        }
    }
}