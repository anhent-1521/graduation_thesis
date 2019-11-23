package com.example.tuananhe.myapplication

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.tuananhe.myapplication.data.model.Video

abstract class BaseFragment : Fragment() {

    companion object{
        private const val VIDEO_EXTRA = "video"

        fun getVideoActivityIntent(context: Context?, video: Video, clazz: Class<out Activity>): Intent {
            val intent = Intent(context, clazz)
            intent.putExtra(VIDEO_EXTRA, video)
            return intent
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(getLayoutResId(), container, false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        getData()
    }

    abstract fun getLayoutResId(): Int
    abstract fun initViews()
    open fun getData() {

    }

}