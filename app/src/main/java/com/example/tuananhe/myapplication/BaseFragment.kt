package com.example.tuananhe.myapplication

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.tuananhe.myapplication.data.model.Video

abstract class BaseFragment : Fragment() {

    companion object{
        private const val VIDEO_EXTRA = "video"
        private const val EDIT_TYPE = "EDIT_TYPE"

        fun getVideoActivityIntent(context: Context?, video: Video,
                                   clazz: Class<out Activity>, type: String = ""): Intent {
            val intent = Intent(context, clazz)
            intent.putExtra(VIDEO_EXTRA, video)
            intent.putExtra(EDIT_TYPE, type)
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