package com.example.tuananhe.myapplication

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.tuananhe.myapplication.utils.AppUtil

abstract class BaseActivity : AppCompatActivity() {

    companion object {
        const val TITLE_COLOR_NONE = -1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutResId())
        if (getTitleBarColorId() > 0) {
            AppUtil.changeTitleBarColor(this, getTitleBarColorId())
        }
        initViews()
        initComponents()
    }

    abstract fun getLayoutResId(): Int
    open fun getTitleBarColorId(): Int = TITLE_COLOR_NONE
    abstract fun initViews()
    abstract fun initComponents()

}