package com.jfkj.bililitebyxiayu.page.player.adpater.UserVideoAdpater

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.jfkj.bililitebyxiayu.databinding.ListVideoCardBinding
import com.jfkj.bililitebyxiayu.func.VideoRelated

class RelatedVideoAdpater(private val dataList: MutableList<VideoRelated>, val callback: (Int,View) -> Unit) :
    RecyclerView.Adapter<RelatedVideoAdpater.ViewHolder>() {

    class ViewHolder(val binding: ListVideoCardBinding, val callback: (Int, View) -> Unit) : RecyclerView.ViewHolder(binding.root){
        init {
            itemView.setOnClickListener {
                val position = absoluteAdapterPosition
                callback(position,binding.videocard)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListVideoCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding,callback)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Access the 'text' TextView through the binding

        holder.binding.title.text = dataList[position].title
        holder.binding.detail.text = dataList[position].owner.name
        val time=dataList[position].duration
        holder.binding.timeLong.text = if (time>60) "${time/60}:${time%60}" else "0:${time%60}"

        Glide.with(holder.binding.root)
            .load(dataList[position].pic)
            .transition(DrawableTransitionOptions.withCrossFade()) // 添加淡入淡出效果
            .into(holder.binding.image)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}
