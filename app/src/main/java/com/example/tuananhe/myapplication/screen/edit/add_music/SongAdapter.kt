package com.example.tuananhe.myapplication.screen.edit.add_music

import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.tuananhe.myapplication.R
import com.example.tuananhe.myapplication.data.model.Song
import com.example.tuananhe.myapplication.utils.MediaUtil
import kotlinx.android.synthetic.main.item_song.view.*

class SongAdapter(private val songs: ArrayList<Song>) : androidx.recyclerview.widget.RecyclerView.Adapter<SongAdapter.ViewHolder>() {

    var listener: ((Song) -> Unit)? = null
    var selected = -1

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder =
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_song, parent, false))


    override fun getItemCount(): Int = songs.size

    override fun onBindViewHolder(holder: ViewHolder, p1: Int) {
        holder.bindData(songs[p1])
    }

    inner class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

        fun bindData(song: Song) {
            itemView.text_title.text = song.title
            itemView.text_duration.text = MediaUtil.getVideoDuration(song.duration)
            if (selected == adapterPosition) {
                itemView.text_title.setTextColor(
                        ResourcesCompat.getColor(itemView.resources, R.color.colorPrimary, itemView.context.theme))
                itemView.text_duration.setTextColor(
                        ResourcesCompat.getColor(itemView.resources, R.color.colorPrimary, itemView.context.theme))
            } else {
                itemView.text_title.setTextColor(
                        ResourcesCompat.getColor(itemView.resources, R.color.color_default_text, itemView.context.theme))
                itemView.text_duration.setTextColor(
                        ResourcesCompat.getColor(itemView.resources, R.color.color_default_text, itemView.context.theme))
            }
            itemView.setOnClickListener {
                val oldSelected = selected
                selected = adapterPosition
                notifyItemChanged(oldSelected)
                notifyItemChanged(selected)
                listener?.invoke(song)
            }
        }
    }
}