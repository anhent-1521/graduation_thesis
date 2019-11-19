package com.example.tuananhe.myapplication.screen.setting

import com.example.tuananhe.myapplication.BaseFragment
import com.example.tuananhe.myapplication.R
import com.example.tuananhe.myapplication.evenBus.Event
import com.example.tuananhe.myapplication.utils.Constant
import com.example.tuananhe.myapplication.utils.Constant.Companion.MBPS
import com.example.tuananhe.myapplication.utils.Settings
import com.example.tuananhe.myapplication.utils.view.dialog.BitrateDialog
import com.example.tuananhe.myapplication.utils.view.dialog.FpsDialog
import com.example.tuananhe.myapplication.utils.view.dialog.LocationDialog
import com.example.tuananhe.myapplication.utils.view.dialog.ResolutionDialog
import kotlinx.android.synthetic.main.fragment_setting.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class SettingFragment : BaseFragment() {

    override fun getLayoutResId() = R.layout.fragment_setting

    override fun initViews() {
        constrain_resolution.setOnClickListener { showResolution() }
        constrain_fps.setOnClickListener { showFps() }
        constrain_quality.setOnClickListener { showBitrate() }
        constrain_location.setOnClickListener { changeLocation() }
        constrain_audio.setOnClickListener { changeRecordAudio(!switch_audio.isChecked) }
        constrain_control.setOnClickListener { changeShowControl(!switch_control.isChecked) }
        switch_audio.setOnClickListener {  changeRecordAudio(switch_audio.isChecked) }
        switch_control.setOnClickListener { changeShowControl(switch_control.isChecked) }
    }

    private fun showResolution() {
        context?.let { it ->
            val dialog = ResolutionDialog(it)
            dialog.show()
        }
    }

    private fun showBitrate() {
        context?.let { it ->
            val dialog = BitrateDialog(it)
            dialog.show()
        }
    }

    private fun showFps() {
        context?.let { it ->
            val dialog = FpsDialog(it)
            dialog.show()
        }
    }

    private fun changeRecordAudio(isCheck: Boolean) {
        val settings = Settings.getSetting()
        settings.isRecordAudio = isCheck
        Settings.saveSetting(settings)
    }

    private fun changeShowControl(isCheck: Boolean) {
        val settings = Settings.getSetting()
        settings.isShowControl = isCheck
        Settings.saveSetting(settings)
    }

    private fun changeLocation() {
        context?.let { it ->
            val dialog = LocationDialog(it)
            dialog.show()
        }
    }

    private fun getSetting() {
        val settings = Settings.getSetting()
        text_resolution.text = settings.resolution.toString()
        text_fps.text = "${settings.fps} fps"
        text_quality.text = "${settings.bitrate / MBPS} Mbps"
        switch_audio.isChecked = settings.isRecordAudio
        switch_control.isChecked = settings.isShowControl
        text_location.text = settings.rootDirectory
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: Event) {
        when (event.action) {
            Constant.SETTING_CHANGED -> {
                getSetting()
            }
        }
    }

    override fun getData() {
        super.getData()
        getSetting()
        EventBus.getDefault().register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

}
