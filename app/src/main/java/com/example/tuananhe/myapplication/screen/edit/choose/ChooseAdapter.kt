package com.example.tuananhe.myapplication.screen.edit.choose

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.tuananhe.myapplication.R
import com.example.tuananhe.myapplication.data.ItemEdit
import kotlinx.android.synthetic.main.item_choose_edit.view.*

class ChooseAdapter(private val hasAudio: Boolean) :
    androidx.recyclerview.widget.RecyclerView.Adapter<ChooseAdapter.ViewHolder>() {

    var listener: ((ItemEdit) -> Unit)? = null
    var items: ArrayList<ItemEdit> = arrayListOf()

    init {
        items = ItemEdit.getEdits()
        if (!hasAudio) {
            items.removeAt(3)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_choose_edit,
                parent,
                false
            )
        )

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        holder.bindData(items[pos])
    }

    inner class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

        fun bindData(item: ItemEdit) {
            Glide.with(itemView.context)
                .load(item.image)
                .centerCrop()
                .into(itemView.image_edit)
            itemView.text_edit.text = item.title
            itemView.setOnClickListener { listener?.invoke(item) }
        }
    }
}