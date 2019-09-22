package com.example.tuananhe.myapplication.utils.view.dialog

import android.content.Context
import com.example.tuananhe.myapplication.R

class OverlayDialog(context: Context) : BaseDialog(context) {

    var listener: ((Unit) -> Unit)? = null

    override fun getLayoutResId() = R.layout.dialog_overlay

    override fun onOptimisticClick() {
        listener?.invoke(Unit)
    }

}
