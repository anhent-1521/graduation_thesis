package com.example.tuananhe.myapplication.screen.edit.trim

import android.content.Context
import android.graphics.Bitmap
import com.example.tuananhe.myapplication.BaseEditActivity
import com.example.tuananhe.myapplication.R
import kotlinx.android.synthetic.main.activity_trim.*

class TrimActivity : BaseEditActivity(), TrimContract.View {

    private val presenter = TrimPresenter(this)

    override fun getLayoutResId(): Int = R.layout.activity_trim

    override fun initView() {

    }

    override fun setItemChoose() {
        presenter.getListBitmap(video)
    }

    override fun provideContext(): Context = this

    override fun onGetListBitMap(bitmaps: ArrayList<Bitmap>) {
        slide_image.addListBitmap(bitmaps)
    }

    override fun onTrimStart() {
    }

    override fun onTrimEnd() {
    }

    override fun showProgress() {
    }

    override fun hideProgress() {
    }


}