package com.example.tuananhe.myapplication.screen.edit.choose

import com.example.tuananhe.myapplication.BaseEditActivity
import com.example.tuananhe.myapplication.BaseFragment
import com.example.tuananhe.myapplication.R
import com.example.tuananhe.myapplication.data.ItemEdit
import com.example.tuananhe.myapplication.screen.edit.trim.TrimActivity
import kotlinx.android.synthetic.main.activity_choose_edit.*

class ChooseEditActivity : BaseEditActivity() {

    private var adapter: ChooseAdapter? = null

    override fun getLayoutResId(): Int = R.layout.activity_choose_edit

    override fun initView() {
    }

    override fun setItemChoose() {
        adapter = ChooseAdapter()
        adapter?.listener = { item -> gotoEdit(item) }
        recycler_edit.adapter = adapter
    }

    private fun gotoEdit(item: ItemEdit) {
        when (item.title) {
            ItemEdit.TRIM -> {
                startActivity(BaseFragment
                        .getVideoActivityIntent(this, video, TrimActivity::class.java))
            }
            ItemEdit.ADD_IMAGE -> {

            }
            ItemEdit.ADD_INTRO -> {

            }
            ItemEdit.ADD_MUSIC -> {

            }
            ItemEdit.REMOVE_MIDDLE -> {

            }
            ItemEdit.CROP -> {

            }
            ItemEdit.SPEED -> {

            }
        }
    }
}
