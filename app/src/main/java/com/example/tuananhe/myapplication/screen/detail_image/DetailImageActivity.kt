package com.example.tuananhe.myapplication.screen.detail_image

import android.app.Activity
import android.view.View.VISIBLE
import androidx.viewpager.widget.ViewPager
import com.example.tuananhe.myapplication.BaseActivity
import com.example.tuananhe.myapplication.R
import com.example.tuananhe.myapplication.data.model.Image
import com.example.tuananhe.myapplication.screen.image.ImageContract
import com.example.tuananhe.myapplication.screen.image.ImagePresenter
import com.example.tuananhe.myapplication.utils.ExtensionUtil
import com.example.tuananhe.myapplication.utils.FileUtil
import com.example.tuananhe.myapplication.utils.Settings
import com.example.tuananhe.myapplication.utils.view.dialog.CommonDialog
import com.example.tuananhe.myapplication.utils.view.dialog.ImageInfoDialog
import iamutkarshtiwari.github.io.ananas.editimage.EditImageActivity
import iamutkarshtiwari.github.io.ananas.editimage.ImageEditorIntentBuilder
import kotlinx.android.synthetic.main.activity_detail_image.*
import java.io.File
import java.lang.Long


class DetailImageActivity : BaseActivity(), ImageContract.View {

    companion object{
        private const val PHOTO_EDITOR_REQUEST_CODE = 231
    }

    private var imageRetriever: ImagePresenter? = null
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
        linear_edit.setOnClickListener {
            editImage(images[view_pager.currentItem])
        }
        image_info.setOnClickListener {
            ImageInfoDialog(this, images[view_pager.currentItem]).show()
        }
        text_name.text = this.getString(R.string.text_location_image_info)

    }

    override fun initComponents() {
        imageRetriever = ImagePresenter(this)
        val images = intent.getParcelableArrayListExtra<Image>("images")
        if (images == null) {
            val settings = Settings.getSetting()
            imageRetriever?.loadImages(settings.rootImageDirectory)
        } else {
            onGetImageSuccess(images)
        }
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

    override fun onGetImage() {
    }

    override fun getContext(): Activity? = this

    override fun showRemindPermission() {
    }

    override fun hideRemindPermission() {
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

    private fun editImage(image: Image) {
        val setting = Settings.getSetting()
        val outputFile = File(
                setting.rootImageDirectory, "Edited-"
                + Long.toHexString(System.currentTimeMillis()) + ".mp4"
        )
        try {
            val intent = ImageEditorIntentBuilder(this, image.path, outputFile.path)
                    .withAddText() // Add the features you need
                    .withPaintFeature()
                    .withFilterFeature()
                    .withRotateFeature()
                    .withCropFeature()
                    .withBrightnessFeature()
                    .withSaturationFeature()
                    .withBeautyFeature()
                    .withStickerFeature()
                    .forcePortrait(true) // Add this to force portrait mode (It's set to false by default)
                    .build()
            EditImageActivity.start(this, intent, PHOTO_EDITOR_REQUEST_CODE)
        } catch (e: Exception) {
        }

    }
}

