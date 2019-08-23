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

    var imageAdapter: ImageAdapter? = null
    var imageRetriever: ImageRetriever? = null

    override fun getLayoutResId(): Int = R.layout.fragment_image

    override fun initViews() {
        imageRetriever = ImageRetriever(this)
        imageRetriever?.loadImages(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + Constant.IMAGE_DIRECTORY)
    }

    override fun onGetImageSuccess(images: ArrayList<Image>) {
        imageAdapter = ImageAdapter(images)
        imageAdapter?.listener = { pos -> gotoDetailImage(pos) }
        recycler_images.adapter = imageAdapter
//        recycler_images.addItemDecoration(ItemDE)
    }

    override fun onGetImageFail() {
    }

    override fun getContentResolver(): ContentResolver? = activity?.contentResolver

    private fun gotoDetailImage(pos: Int) {
        val intent = Intent(context, DetailImageActivity::class.java)
        intent.putExtra("position", pos)
        startActivity(intent)
    }
}
