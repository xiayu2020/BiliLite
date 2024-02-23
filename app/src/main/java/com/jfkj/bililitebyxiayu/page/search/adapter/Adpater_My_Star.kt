package com.jfkj.bililitebyxiayu.page.search.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.jfkj.bililitebyxiayu.databinding.GridSearchCicrleBinding
import com.jfkj.bililitebyxiayu.func.MyStarData


class Adpater_My_Star(private val dataList: MutableList<MyStarData>,val callback: (View,Int) -> Unit) :
    RecyclerView.Adapter<Adpater_My_Star.ViewHolder>() {

    class ViewHolder(val binding: GridSearchCicrleBinding,val callback: (View,Int) -> Unit) : RecyclerView.ViewHolder(binding.root){
        init {
            itemView.setOnClickListener {
                val position = absoluteAdapterPosition
                callback(binding.filetypecard,position)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = GridSearchCicrleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding,callback)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Access the 'text' TextView through the binding
        holder.binding.text.text = dataList[position].uname
        Glide.with(holder.binding.root)
            .load(dataList[position].face)
            .transition(DrawableTransitionOptions.withCrossFade()) // 添加淡入淡出效果
            .into(holder.binding.face)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}