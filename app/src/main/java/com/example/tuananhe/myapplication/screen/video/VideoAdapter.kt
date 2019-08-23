package com.example.tuananhe.myapplication.screen.video

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.tuananhe.myapplication.R
import com.example.tuananhe.myapplication.data.model.Video
import com.example.tuananhe.myapplication.utils.MediaUtil
import kotlinx.android.synthetic.main.item_video.view.*

class VideoAdapter(private var videos: ArrayList<Video>) :
    RecyclerView.Adapter<VideoAdapter.ViewHolder>() {

    var listener: ((Video) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_video, parent, false))

    override fun getItemCount(): Int = videos.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(videos[position])
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindData(video: Video) {
            Glide.with(itemView.context)
                .load(video.path)
                .centerCrop()
                .into(itemView.image_thumbnail)
            itemView.text_duration.text = MediaUtil.getVideoDuration(video.duration)
            itemView.text_name.text = video.name
            itemView.text_size.text = MediaUtil.getVideoSize(video.path)
            itemView.setOnClickListener { listener?.invoke(video) }
        }
    }

}