package com.example.tuananhe.myapplication.screen.edit.choose

import android.app.Activity
import com.example.tuananhe.myapplication.BaseEditActivity
import com.example.tuananhe.myapplication.BaseFragment
import com.example.tuananhe.myapplication.R
import com.example.tuananhe.myapplication.data.ItemEdit
import com.example.tuananhe.myapplication.screen.edit.add_music.AddMusicActivity
import com.example.tuananhe.myapplication.screen.edit.rotate.RotateActivity
import com.example.tuananhe.myapplication.screen.edit.speed.SpeedActivity
import com.example.tuananhe.myapplication.screen.edit.trim.TrimActivity
import kotlinx.android.synthetic.main.activity_choose_edit.*
import com.example.tuananhe.myapplication.screen.edit.merge_video.MergeVideoActivity
import com.example.tuananhe.myapplication.screen.edit.remove_audio.RemoveAudioActivity
import com.example.tuananhe.myapplication.utils.MediaUtil

class ChooseEditActivity : BaseEditActivity() {

    private var adapter: ChooseAdapter? = null

    override fun getLayoutResId(): Int = R.layout.activity_choose_edit

    override fun getEditTitle(): String = "Edit Video"

    override fun onClickSave() {
    }

    override fun onClickCancel() {
    }

    override fun initView() {
    }

    override fun setItemChoose() {
        adapter = ChooseAdapter(MediaUtil.isVideoHaveAudioTrack(video.path))
        adapter?.listener = { item -> gotoEdit(item) }
        recycler_edit.adapter = adapter
    }

    override fun onClickPreview() {
    }

    private fun gotoEdit(item: ItemEdit) {
        var clazz: Class<out Activity> = TrimActivity::class.java
        var type: String = item.title
        when (item.title) {
            ItemEdit.TRIM -> {
                clazz = TrimActivity::class.java
            }
            ItemEdit.REMOVE_AUDIO -> {
                clazz = RemoveAudioActivity::class.java
            }
            ItemEdit.ADD_INTRO -> {
                clazz = MergeVideoActivity::class.java
            }
            ItemEdit.ADD_MUSIC -> {
                clazz = AddMusicActivity::class.java
            }
            ItemEdit.REMOVE_MIDDLE -> {
                clazz = TrimActivity::class.java
            }
            ItemEdit.CROP -> {

            }
            ItemEdit.SPEED -> {
                clazz = SpeedActivity::class.java
            }
            ItemEdit.ROTATE -> {
                clazz = RotateActivity::class.java
            }
        }
        startActivity(BaseFragment
                .getVideoActivityIntent(this, video, clazz, type))
    }
}
