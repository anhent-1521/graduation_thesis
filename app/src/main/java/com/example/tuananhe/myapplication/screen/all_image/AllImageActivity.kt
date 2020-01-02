package com.example.tuananhe.myapplication.screen.all_image

import android.annotation.SuppressLint
import android.content.Intent
import android.view.View.GONE
import android.view.View.VISIBLE
import com.example.tuananhe.myapplication.BaseActivity
import com.example.tuananhe.myapplication.R
import com.example.tuananhe.myapplication.data.model.Image
import com.example.tuananhe.myapplication.screen.detail_image.DetailImageActivity
import com.example.tuananhe.myapplication.screen.image.ImageAdapter
import kotlinx.android.synthetic.main.activity_all_image.*
import kotlinx.android.synthetic.main.layout_header_edit.*

class AllImageActivity : BaseActivity(), AllImageContract.View {

    private val presenter = AllImagePresenter(this)

    override fun getLayoutResId(): Int = R.layout.activity_all_image

    override fun getTitleBarColorId(): Int = R.color.colorPrimary

    @SuppressLint("SetTextI18n")
    override fun initViews() {
        image_back.setOnClickListener { onBackPressed() }
        text_save.visibility = GONE
        text_title.text = "All Image"
    }

    override fun initComponents() {
        presenter.getImages(this)
        progress.visibility = VISIBLE
    }

    override fun onGetImages(images: ArrayList<Image>) {
        progress.visibility = GONE
        val adapter = ImageAdapter(images)
        adapter.listener = { pos ->
            val intent = Intent(this, DetailImageActivity::class.java)
            intent.putExtra("position", pos)
            intent.putExtra("images", images)
            startActivity(intent)
        }
        recycler_image.adapter = adapter
    }

}