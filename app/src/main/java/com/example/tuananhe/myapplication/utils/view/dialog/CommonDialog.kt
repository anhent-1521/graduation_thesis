package com.example.tuananhe.myapplication.utils.view.dialog

import android.content.Context
import android.view.View
import com.example.tuananhe.myapplication.R
import kotlinx.android.synthetic.main.dialog_common.*

class CommonDialog(context: Context,
                   private var title: String?,
                   private var isShowCancel: Boolean = false)
    : BaseDialog(context) {

    override fun getLayoutResId(): Int = R.layout.dialog_common

    override fun initView() {
        text_title.text = title
        if (isShowCancel) {
            button_cancel.visibility = View.GONE
        }
    }

    override fun onOptimisticClick() {
        optimisticListener?.invoke(null)
    }
}
