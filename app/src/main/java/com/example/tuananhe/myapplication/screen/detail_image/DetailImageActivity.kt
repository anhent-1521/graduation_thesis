package com.example.tuananhe.myapplication.screen.detail_image

import android.os.Environment
import android.support.v4.view.ViewPager
import android.view.View.VISIBLE
import com.example.tuananhe.myapplication.BaseActivity
import com.example.tuananhe.myapplication.R
import com.example.tuananhe.myapplication.data.model.Image
import com.example.tuananhe.myapplication.screen.image.ImageContract
import com.example.tuananhe.myapplication.screen.image.ImageRetriever
import com.example.tuananhe.myapplication.utils.Constant
import com.example.tuananhe.myapplication.utils.ExtensionUtil
import com.example.tuananhe.myapplication.utils.FileUtil
import com.example.tuananhe.myapplication.utils.view.dialog.CommonDialog
import com.example.tuananhe.myapplication.utils.view.dialog.ImageInfoDialog
import kotlinx.android.synthetic.main.activity_detail_image.*

class DetailImageActivity : BaseActivity(), ImageContract.View {

    private var imageRetriever: ImageRetriever? = null
    private var images = ArrayList<Image>()
    private var position: Int = 0

    override fun getLayoutResId(): Int = R.layout.activity_detail_image

    override fun initViews() {
        position = intent.getIntExtra("position", 0)
        image_back.setOnClickListener {
            onBackPressed()
        }

        linear_share.setOnClickListener {
            FileUtil.shareImage(
                this,
                images[view_pager.currentItem].path
            )
        }

        linear_delete.setOnClickListener {
            deleteImage(images[view_pager.currentItem])
        }
        image_info.setOnClickListener {
            ImageInfoDialog(this, images[view_pager.currentItem]).show()
        }
        text_name.text = this.getString(R.string.text_location_image_info)

    }

    override fun initComponents() {
        imageRetriever = ImageRetriever(this)
        imageRetriever?.loadImages(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + Constant.IMAGE_DIRECTORY)
    }

    override fun onGetImageSuccess(images: ArrayList<Image>) {
        this.images = images
        val pagerAdapter = DetailImagePagerAdapter(supportFragmentManager, images)
        view_pager.adapter = pagerAdapter
        view_pager.currentItem = position
        view_pager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                text_name.text = images[position].name
            }
        })
        text_name.text = images[position].name
    }

    override fun onGetImageFail() {

    }

    fun setControlVisible() {
        if (linear_top.visibility == VISIBLE) {
            hideControl()
        } else {
            showControl()
        }
    }

    private fun hideControl() {
        with(ExtensionUtil()) {
            linear_top.hideDown(-linear_top.height.toFloat())
            constraint_bottom.hideDown(constraint_bottom.height.toFloat())
        }
    }

    private fun showControl() {
        with(ExtensionUtil()) {
            linear_top.showUp()
            constraint_bottom.showUp()
        }
    }


    private fun deleteImage(image: Image) {
        val dialog = CommonDialog(this, getString(R.string.dialog_delete_video_title))
        dialog.optimisticListener = {
            FileUtil.deleteFile(image.path)
            finish()
        }
        dialog.show()

    }
}

