package com.example.tuananhe.myapplication.screen.image

import android.content.res.Resources
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.tuananhe.myapplication.R
import com.example.tuananhe.myapplication.data.model.Image
import kotlinx.android.synthetic.main.item_image.view.*

class ImageAdapter(private var images: ArrayList<Image>) :
    RecyclerView.Adapter<ImageAdapter.ViewHolder>() {

    var listener: ((Int) -> Unit)? = null
    var clickListener: ((Image) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false))

    override fun getItemCount(): Int = images.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(images[position], position)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var imageSize: Int = 0

        init {
            val screenWith = Resources.getSystem().displayMetrics.widthPixels
            val padding = itemView.resources.getDimensionPixelOffset(R.dimen.image_screen_padding)
            imageSize = (screenWith - padding) / 3

            val params = itemView.card_view.layoutParams
            params.height = imageSize
            itemView.layoutParams = params

        }

        fun bindData(image: Image, position: Int) {
            Glide.with(itemView.context)
                .load(image.path)
                .centerCrop()
                .into(itemView.image_thumbnail)
            itemView.image_thumbnail.width
            itemView.setOnClickListener {
                listener?.invoke(position)
                clickListener?.invoke(image)
            }
        }
    }

}