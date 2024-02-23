package com.jfkj.bililitebyxiayu.page.history_favour.adpater

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.jfkj.bililitebyxiayu.databinding.ListVideoCardBinding
import com.jfkj.bililitebyxiayu.func.FavourData
import com.jfkj.bililitebyxiayu.func.FavourDetailData
import com.jfkj.bililitebyxiayu.func.HistoryData

class Apater_Favour(private var dataList: MutableList<FavourData>, val callback: (Int, View) -> Unit) :
    RecyclerView.Adapter<Apater_Favour.ViewHolder>() {

    class ViewHolder(val binding: ListVideoCardBinding, val callback: (Int, View) -> Unit) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                val position = absoluteAdapterPosition
                callback(position, binding.videocard)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ListVideoCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, callback)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Access the 'text' TextView through the binding
        holder.binding.title.text = dataList[position].title
        holder.binding.detail.text = dataList[position].media_count.toString()
        holder.binding.tint.text = "共${dataList[position].media_count}个"
//        holder.binding.timeLong.text =
//            if (time > 60) "${time / 60}:${time % 60}" else "0:${time % 60}"
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

}

class Adpater_Detail_Favour(private var dataList: MutableList<FavourDetailData>, val callback: (Int, View) -> Unit) :
    RecyclerView.Adapter<Adpater_Detail_Favour.ViewHolder>() {

    class ViewHolder(val binding: ListVideoCardBinding, val callback: (Int, View) -> Unit) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                val position = absoluteAdapterPosition
                callback(position, binding.videocard)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ListVideoCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, callback)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Access the 'text' TextView through the binding
        holder.binding.title.text = dataList[position].title
        holder.binding.detail.text = dataList[position].pubtime.toString()
        val time = dataList[position].duration
        holder.binding.timeLong.text =
            if (time > 60) "${time / 60}:${time % 60}" else "0:${time % 60}"

        Glide.with(holder.binding.root)
            .load(dataList[position].cover)
            .transition(DrawableTransitionOptions.withCrossFade()) // 添加淡入淡出效果
            .into(holder.binding.image)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun addData(data: MutableList<FavourDetailData>): MutableList<FavourDetailData> {
        dataList.addAll(data)
        notifyItemRangeInserted(dataList.size+1, data.size)
        return dataList
    }
}