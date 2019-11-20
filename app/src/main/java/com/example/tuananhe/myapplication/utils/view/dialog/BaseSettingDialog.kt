package com.example.tuananhe.myapplication.utils.view.dialog

import android.content.Context
import android.support.v7.app.AlertDialog

abstract class BaseSettingDialog(protected var context: Context) {

    protected var mDialog: AlertDialog

    abstract fun getDialog(): AlertDialog

    init {
        mDialog = getDialog()
    }

    fun show() {
        mDialog.show()
    }

    fun dismiss() {
        mDialog.dismiss()
    }
}
