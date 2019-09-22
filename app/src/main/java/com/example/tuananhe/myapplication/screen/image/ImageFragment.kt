package com.example.tuananhe.myapplication.screen.image

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.os.Environment
import com.example.tuananhe.myapplication.BaseFragment
import com.example.tuananhe.myapplication.R
import com.example.tuananhe.myapplication.data.model.Image
import com.example.tuananhe.myapplication.screen.detail_image.DetailImageActivity
import com.example.tuananhe.myapplication.utils.Constant
import com.example.tuananhe.myapplication.utils.ExtensionUtil
import kotlinx.android.synthetic.main.fragment_image.*
import kotlinx.android.synthetic.main.fragment_image.button_turn_on

class ImageFragment : BaseFragment(), ImageContract.View {

    private var imageAdapter: ImageAdapter? = null
    private var presenter: ImagePresenter = ImagePresenter(this)
    private val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

    override fun getLayoutResId(): Int = R.layout.fragment_image

    override fun initViews() {
        context?.let {
            with(ExtensionUtil()) {
                button_turn_on.changePrimaryStyle(it)
            }
        }
        button_turn_on.setOnClickListener { presenter.requestPermission(permissions, Constant.COMMON_PERMISSION) }
    }

    override fun onGetImage() {
        presenter.loadImages(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()
                + Constant.IMAGE_DIRECTORY)

    }

    override fun onGetImageSuccess(images: ArrayList<Image>) {
        imageAdapter = ImageAdapter(images)
        imageAdapter?.listener = { pos -> gotoDetailImage(pos) }
        recycler_images.adapter = imageAdapter
    }

    override fun onGetImageFail() {
    }

    override fun getContext(): Activity? = activity


    override fun showRemindPermission() {
    }

    override fun hideRemindPermission() {
    }

    private fun gotoDetailImage(pos: Int) {
        val intent = Intent(context, DetailImageActivity::class.java)
        intent.putExtra("position", pos)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        presenter.checkPermission(permissions)
    }
}
