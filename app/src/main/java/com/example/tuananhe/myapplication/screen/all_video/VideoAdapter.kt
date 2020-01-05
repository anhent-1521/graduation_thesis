package com.example.tuananhe.myapplication.screen.all_video

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.tuananhe.myapplication.R
import com.example.tuananhe.myapplication.data.model.Video
import com.example.tuananhe.myapplication.utils.MediaUtil
import kotlinx.android.synthetic.main.item_all_video.view.*

class VideoAdapter(private val videos: ArrayList<Video>) :
    androidx.recyclerview.widget.RecyclerView.Adapter<VideoAdapter.ViewHolder>() {

    var listener: ((Video) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_all_video, parent, false))


    override fun getItemCount(): Int = videos.size

    override fun onBindViewHolder(holder: ViewHolder, p1: Int) {
        holder.bindData(videos[p1])
    }

    inner class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

        fun bindData(video: Video) {
            itemView.text_name.text = video.name
            itemView.text_duration.text = MediaUtil.getVideoDuration(video.duration)
            Glide.with(itemView.context)
                .load(video.path)
                .centerCrop()
                .into(itemView.image_thumbnail)
            itemView.setOnClickListener {
                listener?.invoke(video)
            }
        }
    }
}