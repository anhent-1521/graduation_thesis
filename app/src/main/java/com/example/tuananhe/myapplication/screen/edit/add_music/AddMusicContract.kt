package com.example.tuananhe.myapplication.screen.edit.add_music

import android.graphics.Bitmap
import com.example.tuananhe.myapplication.data.model.Song

interface AddMusicContract {

    interface View : com.example.tuananhe.myapplication.View {
        fun onGetsSong(songs: ArrayList<Song>)

        fun onGetListBitMap(bitmaps: ArrayList<Bitmap>)
    }

    interface Presenter : com.example.tuananhe.myapplication.Presenter {
        fun getSongs()

        fun chooseSong(song: Song)
    }
}
