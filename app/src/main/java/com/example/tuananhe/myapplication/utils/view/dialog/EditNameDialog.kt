package com.example.tuananhe.myapplication.utils.view.dialog

import android.content.Context
import android.view.WindowManager
import android.widget.TextView
import com.example.tuananhe.myapplication.R
import com.example.tuananhe.myapplication.utils.AppUtil
import kotlinx.android.synthetic.main.dialog_edit_name.*

class EditNameDialog(context: Context, private val name: String?) : BaseDialog(context) {

    override fun getLayoutResId(): Int = R.layout.dialog_edit_name

    override fun initView() {
        window.setSoftInputMode (WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

        edit_name.setText(name, TextView.BufferType.EDITABLE)
        edit_name.setSelection(0, name?.length ?: 0)
        edit_name.isCursorVisible = true
        AppUtil.showKeyboard(context, edit_name)
    }

    override fun onOptimisticClick() {
        renameListener?.invoke(edit_name.text.toString())
    }

}
