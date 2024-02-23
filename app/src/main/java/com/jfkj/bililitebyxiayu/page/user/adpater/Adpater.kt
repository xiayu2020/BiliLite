package com.jfkj.bililitebyxiayu.page.user.adpater

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.jfkj.bililitebyxiayu.databinding.ListVideoCardBinding
import com.jfkj.bililitebyxiayu.func.UserVideoVList

//class Adpater_User_Video(private val data: MutableList<UserVideoTList>) : BaseAdapter() {
//
//    override fun getCount(): Int {
//        return data.size
//    }
//
//    override fun getItem(position: Int): UserVideoTList {
//        return data[position]
//    }
//    fun getTitleItem(position: Int): String {
//        return getItem(position).title
//    }
//    fun getWatchItem(position: Int): String {
//        val data=getItem(position).play
//        return if (data>10000)
//            (data/10000).toString()+"万"
//        else data.toString()
//    }
//    fun getTimeItem(position: Int): String {
//        val data=getItem(position).length
//        return data
//    }
//    fun getImageItem(position: Int): String {
//        return getItem(position).pic
//    }
//    override fun getItemId(position: Int): Long {
//        return 0
//    }
//
//    @SuppressLint("ViewHolder", "MissingInflatedId")
//    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
//        val inflater = LayoutInflater.from(parent.context)
//        val binding = ListVideoCardBinding.inflate(inflater, parent, false)
//
//        binding.executePendingBindings()
//
//        binding.title.text = getTitleItem(position)
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
//}

class Adpater_User_Video(private val dataList: MutableList<UserVideoVList>, val callback: (Int,View) -> Unit) :
    RecyclerView.Adapter<Adpater_User_Video.ViewHolder>() {

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
        holder.binding.detail.text = dataList[position].play.toString()
        holder.binding.timeLong.text = dataList[position].length

        Glide.with(holder.binding.root)
            .load(dataList[position].pic)
            .transition(DrawableTransitionOptions.withCrossFade()) // 添加淡入淡出效果
            .into(holder.binding.image)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}