package com.example.tuananhe.myapplication.screen.edit.add_music

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import com.example.tuananhe.myapplication.BaseActivity
import com.example.tuananhe.myapplication.R
import com.example.tuananhe.myapplication.data.model.Song
import com.example.tuananhe.myapplication.utils.ExtensionUtil
import kotlinx.android.synthetic.main.activity_song.*
import kotlinx.android.synthetic.main.layout_header_edit.*
import java.io.IOException

class SongActivity : BaseActivity() {

    private var song: Song? = null
    private var player = MediaPlayer()

    companion object {
        const val EXTRA_SONG = "EXTRA_SONG"
        fun getSongIntent(context: Context, songs: ArrayList<Song>): Intent {
            val intent = Intent(context, SongActivity::class.java)
            intent.putExtra(EXTRA_SONG, songs)
            return intent
        }
    }

    override fun getLayoutResId(): Int = R.layout.activity_song

    override fun getTitleBarColorId(): Int = R.color.colorPrimary

    override fun initViews() {
        with(ExtensionUtil()) {
            text_save.changePrimaryStyle(this@SongActivity, R.drawable.bg_save_edit_press)
        }
        text_title.text = "Choose Song"
        text_save.setOnClickListener { chooseVideo() }
        image_back.setOnClickListener { chooseVideo() }
        val songs = intent.getParcelableArrayListExtra<Song>(EXTRA_SONG)
        val adapter = SongAdapter(songs)
        adapter.listener = { song -> playSong(song) }
        recycler_song.adapter = adapter
    }

    override fun initComponents() {
    }

    private fun playSong(song: Song) {
        this.song = song
        player.reset()
        try {
            player.setDataSource(this, song.data)
            player.prepare()
            player.setOnPreparedListener { player.start() }
            player.setOnCompletionListener {
                player.seekTo(0)
                player.start()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun chooseVideo() {
        if (song != null) {
            val intent = Intent()
            intent.putExtra(EXTRA_SONG, song)
            setResult(RESULT_OK, intent)
        }
        finish()
    }

    override fun onPause() {
        super.onPause()
        player.pause()
    }

    override fun onResume() {
        super.onResume()
        if (!player.isPlaying) {
            player.start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        player.reset()
        player.release()
    }
}
