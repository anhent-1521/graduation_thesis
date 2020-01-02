package com.example.tuananhe.myapplication.screen.edit

import android.content.Intent
import com.example.tuananhe.myapplication.BaseFragment
import com.example.tuananhe.myapplication.R
import com.example.tuananhe.myapplication.screen.all_video.AllVideoActivity
import kotlinx.android.synthetic.main.fragment_edit.*

class EditFragment : BaseFragment() {

    override fun getLayoutResId() = R.layout.fragment_edit

    override fun initViews() {
        card_video.setOnClickListener {
            val intent = Intent(context, AllVideoActivity::class.java)
            context?.startActivity(intent)
        }
    }
}