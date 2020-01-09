package com.example.tuananhe.myapplication.utils.view.dialog

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatDialog
import android.view.ViewGroup
import com.example.tuananhe.myapplication.R
import com.example.tuananhe.myapplication.utils.ExtensionUtil
import kotlinx.android.synthetic.main.dialog_common.*

abstract class BaseDialog(context: Context) : AppCompatDialog(context) {

    var optimisticListener: ((Unit?) -> Unit)? = null
    var renameListener: ((String?) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutResId())
        setCancelable(false)
        try {
            window?.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            )
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window?.attributes?.windowAnimations = R.style.CommonDialogAnimation

            with(ExtensionUtil()) {
                button_optimistic.changePrimaryStyle(this@BaseDialog.context)
                button_cancel.changeDarkerStyle(this@BaseDialog.context)
            }

            button_optimistic.setOnClickListener {
                onOptimisticClick()
                dismiss()
            }

            button_cancel.setOnClickListener {
                dismiss()
            }

            initView()
        } catch (e: Exception) {

        }

    }

    abstract fun getLayoutResId(): Int

    open fun initView() {}

    abstract fun onOptimisticClick()
}
