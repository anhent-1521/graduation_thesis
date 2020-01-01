package com.example.tuananhe.myapplication.screen.edit.add_music

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.example.tuananhe.myapplication.data.model.Song
import java.util.*

class SongHelper {

    companion object {

        fun getSongs(context: Context): ArrayList<Song> {
            val projections = arrayOf(
                    MediaStore.Audio.Media.TITLE,
                    MediaStore.Audio.Media.DURATION,
                    MediaStore.Audio.Media.DATA)

            val cursor = context.contentResolver.query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    projections,
                    null,
                    null,
                    MediaStore.Audio.Media.TITLE + " ASC"
            )
            val songs: ArrayList<Song> = ArrayList()
            if (cursor == null) {
                return songs
            }
            if (cursor.count == 0) {
                cursor.close()
                return songs
            }
            val indexTitle = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val indexDuration = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)
            val indexData = cursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
                val title = cursor.getString(indexTitle)
                val duration = cursor.getLong(indexDuration) / 1000
                val index = cursor.getString(indexData)
                val data = Uri.parse(index)
                if (duration > 5 && index.isNotEmpty()) {
                    songs.add(Song(songs.size, title, duration, data))
                }
                cursor.moveToNext()
            }
            cursor.close()
            return songs
        }
    }
}