package com.example.tuananhe.myapplication.screen.main

import android.Manifest.permission.*
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.viewpager.widget.ViewPager
import com.example.tuananhe.myapplication.BaseActivity
import com.example.tuananhe.myapplication.R
import com.example.tuananhe.myapplication.utils.AppUtil
import com.example.tuananhe.myapplication.utils.Constant.Companion.COMMON_PERMISSION
import com.example.tuananhe.myapplication.utils.Constant.Companion.OVERLAY_PERMISSION
import com.example.tuananhe.myapplication.utils.Constant.Companion.TAB_COUNT
import com.example.tuananhe.myapplication.utils.Constant.Companion.TAB_EDIT
import com.example.tuananhe.myapplication.utils.Constant.Companion.TAB_IMAGE
import com.example.tuananhe.myapplication.utils.Constant.Companion.TAB_SETTING
import com.example.tuananhe.myapplication.utils.Constant.Companion.TAB_VIDEO
import com.example.tuananhe.myapplication.utils.view.dialog.OverlayDialog
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : BaseActivity(), HomeContract.View {

    private val permissions = arrayOf(WRITE_EXTERNAL_STORAGE, RECORD_AUDIO, CAMERA)

    private val presenter = HomePresenter(this)

    private var homePagerAdapter: HomePagerAdapter? = null

    private var dialog: OverlayDialog? = null


    override fun getLayoutResId(): Int = R.layout.activity_home

    override fun getTitleBarColorId(): Int = R.color.color_red_light

    override fun initViews() {
        homePagerAdapter = HomePagerAdapter(supportFragmentManager)
        view_pager.adapter = homePagerAdapter
        view_pager.offscreenPageLimit = TAB_COUNT
        tab_layout.setupWithViewPager(view_pager)
        setupTabIcon()
        view_pager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setupTabIcon()
            }
        })
    }

    override fun initComponents() {
        // create default notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = getString(R.string.default_floatingview_channel_id)
            val channelName = getString(R.string.default_floatingview_channel_name)
            val defaultChannel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_MIN)
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(defaultChannel)
        }
        presenter.checkRequiredPermission(permissions, COMMON_PERMISSION)
    }

    override fun getContext() = this

    override fun showOverlayDialog() {
        dialog = OverlayDialog(this)
        dialog?.listener = { showOverlaySetting() }
        dialog?.show()
    }

    override fun startBubbleView() {
        presenter.startBubble(this)
    }

    override fun showOverlaySetting() {
        //API < 23 overlay permission is provided by default
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
            )
            startActivityForResult(intent, OVERLAY_PERMISSION)
        }
    }

    private fun setupTabIcon() {
        val selected = tab_layout.selectedTabPosition
        tab_layout.getTabAt(TAB_VIDEO)
                ?.setIcon(if (selected == TAB_VIDEO) R.drawable.ic_video_active else R.drawable.ic_video)
        tab_layout.getTabAt(TAB_IMAGE)
                ?.setIcon(if (selected == TAB_IMAGE) R.drawable.ic_images_active else R.drawable.ic_images)
        tab_layout.getTabAt(TAB_EDIT)
                ?.setIcon(if (selected == TAB_EDIT) R.drawable.ic_effect_active else R.drawable.ic_effect)
        tab_layout.getTabAt(TAB_SETTING)
                ?.setIcon(if (selected == TAB_SETTING) R.drawable.ic_options_active else R.drawable.ic_options)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            COMMON_PERMISSION -> {
                //After request common permission, request overlay permission to display bubble view
                dialog?.let { dialog ->
                    if (dialog.isShowing) {
                        return
                    }
                }
                presenter.checkOverlayPermission()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            OVERLAY_PERMISSION -> {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    return
                }
                //Check if the permission is granted or not.
                if (Settings.canDrawOverlays(this)) {
                    presenter.startBubble(this)
                } else {
                    //Permission is not granted
                    showOverlayDialog()
                }
            }
        }

    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent != null) {
            if (intent.getBooleanExtra(AppUtil.EXTRA_SHOW_SETTING, false)) {
                view_pager.setCurrentItem(3, true)
            }
        }
    }

}
