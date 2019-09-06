package com.example.tuananhe.myapplication.screen.image

import android.content.ContentResolver
import android.content.Intent
import android.os.Environment
import com.example.tuananhe.myapplication.BaseFragment
import com.example.tuananhe.myapplication.R
import com.example.tuananhe.myapplication.data.model.Image
import com.example.tuananhe.myapplication.screen.detail_image.DetailImageActivity
import com.example.tuananhe.myapplication.utils.Constant
import kotlinx.android.synthetic.main.fragment_image.*

class ImageFragment : BaseFragment(), ImageContract.View {

    private var imageAdapter: ImageAdapter? = null
    private var imageRetriever: ImageRetriever? = null

    override fun getLayoutResId(): Int = R.layout.fragment_image

    override fun initViews() {
        imageRetriever = ImageRetriever(this)
    }

    override fun onGetImageSuccess(images: ArrayList<Image>) {
        imageAdapter = ImageAdapter(images)
        imageAdapter?.listener = { pos -> gotoDetailImage(pos) }
        recycler_images.adapter = imageAdapter
    }

    override fun onGetImageFail() {
    }

    override fun getContentResolver(): ContentResolver? = activity?.contentResolver

    private fun gotoDetailImage(pos: Int) {
        val intent = Intent(context, DetailImageActivity::class.java)
        intent.putExtra("position", pos)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        imageRetriever?.loadImages(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + Constant.IMAGE_DIRECTORY)
    }
}
