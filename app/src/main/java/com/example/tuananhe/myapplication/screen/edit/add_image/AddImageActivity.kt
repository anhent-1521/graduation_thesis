package com.example.tuananhe.myapplication.screen.edit.add_image

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.tuananhe.myapplication.BaseEditActivity
import com.example.tuananhe.myapplication.R
import kotlinx.android.synthetic.main.activity_add_image.*
import java.io.File


class AddImageActivity : BaseEditActivity() {

    companion object {
        const val CHOOSE_IMAGE_CODE = 9520
    }

    override fun initView() {
        linear_choose_image.setOnClickListener { chooseImage() }
    }

    override fun getEditTitle(): String = "Add Background"

    override fun setItemChoose() {
    }

    override fun onClickPreview() {
    }

    override fun onClickCancel() {
    }

    override fun onClickSave() {
    }

    override fun getLayoutResId(): Int = R.layout.activity_add_image

    private fun chooseImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, CHOOSE_IMAGE_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CHOOSE_IMAGE_CODE && resultCode == Activity.RESULT_OK) {
            val inputStream = contentResolver.openInputStream(data?.data)
            image_thumbnail.setImageBitmap(BitmapFactory.decodeStream(inputStream))
        }
    }
}