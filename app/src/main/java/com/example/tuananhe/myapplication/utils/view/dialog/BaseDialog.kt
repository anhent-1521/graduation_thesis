package com.example.tuananhe.myapplication.utils.view.dialog

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v7.app.AppCompatDialog
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
                button_optimistic.onTouchChangeStyle(
                    this@BaseDialog.context,
                    R.color.color_white,
                    R.color.colorPrimary,
                    R.drawable.bg_dialog_optimistic_press,
                    R.drawable.bg_dialog_optimistic_idle
                )

                button_cancel.onTouchChangeStyle(
                    this@BaseDialog.context,
                    R.color.color_white,
                    R.color.color_default_text,
                    R.drawable.bg_dialog_cancel_press,
                    R.drawable.bg_dialog_cancel_idle
                )
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

    abstract fun initView()

    abstract fun onOptimisticClick()
}
