package com.jfkj.bililitebyxiayu.style.mainstyle.adpater

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.jfkj.bililitebyxiayu.databinding.VideoCardBinding
import com.jfkj.bililitebyxiayu.func.HistoryData
import com.jfkj.bililitebyxiayu.func.HomeVideoData

class Adpater_Page1(
    private var dataList: MutableList<HomeVideoData>,
    val callback: (Int, View) -> Unit
) :
    RecyclerView.Adapter<Adpater_Page1.ViewHolder>() {

    class ViewHolder(val binding: VideoCardBinding, val callback: (Int, View) -> Unit) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                val position = absoluteAdapterPosition
                callback(position, binding.videocard)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = VideoCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, callback)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Access the 'text' TextView through the binding

        holder.binding.title.text = dataList[position].title
        holder.binding.detail.text = dataList[position].owner.name
        holder.binding.watchNumber.text = dataList[position].stat.view.toString()
        holder.binding.timeLong.text = dataList[position].duration.toString()

        Glide.with(holder.binding.root)
            .load(dataList[position].pic)
            .transition(DrawableTransitionOptions.withCrossFade()) // 添加淡入淡出效果
            .into(holder.binding.image)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun fresh(data: MutableList<HomeVideoData>): MutableList<HomeVideoData> {
        dataList = data
        this.notifyDataSetChanged()
        return data
    }

    fun addData(data: MutableList<HomeVideoData>): MutableList<HomeVideoData> {
        dataList.addAll(data)
        notifyItemRangeInserted(dataList.size + 1, data.size)
        return dataList
    }
}

//class Adpater_Page1(private val data: MutableList<HomeVideoData>) : BaseAdapter() {
//
//    override fun getCount(): Int {
//        return data.size
//    }
//
//    override fun getItem(position: Int): HomeVideoData {
//        return data[position]
//    }
//    fun getOwner(position: Int): Owner {
//        return data[position].owner
//    }
//    fun getStat(position: Int): Stat {
//        return data[position].stat
//    }
//    fun getTitleItem(position: Int): String {
//        return getItem(position)["title"] ?:"加载中"
//    }
//    fun getNameItem(position: Int): String {
//        return getOwner(position)["name"] ?:"加载中"
//    }
//    fun getWatchItem(position: Int): String {
//        val data=getStat(position)["view"] ?:0
//        return if (data>10000)
//            (data/10000).toString()+"万"
//        else data.toString()
//    }
//    fun getTimeItem(position: Int): String {
//        val data=getItem(position)["duration"]?.toInt() ?:0
//        return ((data/60).toString() + ":" + (data % 60).toString())
//    }
//    fun getImageItem(position: Int): String {
//        return getItem(position)["pic"] ?:""
//    }
//    override fun getItemId(position: Int): Long {
//        return 0
//    }
//
//    @SuppressLint("ViewHolder", "MissingInflatedId")
//    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
//        val inflater = LayoutInflater.from(parent.context)
//        val binding = VideoCardBinding.inflate(inflater, parent, false)
//
//        binding.executePendingBindings()
//
//        binding.title.text = getTitleItem(position)
//        binding.detail.text = getNameItem(position)
//        binding.watchNumber.text = getWatchItem(position)
//        binding.timeLong.text = getTimeItem(position)
//
//        Glide.with(binding.root)
//            .load(getImageItem(position))
//            .transition(DrawableTransitionOptions.withCrossFade()) // 添加淡入淡出效果
//            .into(binding.image)
//        return binding.root
//    }
//
//    fun update() {
////        data.clear()
//        notifyDataSetChanged()
//    }
//
