package com.jfkj.bililitebyxiayu.page.search.adpaterpage

import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.jfkj.bililitebyxiayu.databinding.ListVideoCardBinding
import com.jfkj.bililitebyxiayu.func.SearchDataDetail


class Adpater_Video_Data(
    private val dataList: MutableList<SearchDataDetail>,
    val callback: (Int, View) -> Unit
) : RecyclerView.Adapter<Adpater_Video_Data.ViewHolder>() {

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
        val pattern = Regex("<em[^>]*>(.*?)</em>") // 匹配以<em>开头，以</em>结尾的内容 // 替换为匹配到的内容
        var startIndex=0
        var endIndex=0
        val Text = pattern.replace(dataList[position].title) { matchResult ->
            val matchedTextLength = matchResult.value.length
              startIndex = matchResult.range.first
              endIndex = matchResult.range.first + matchResult.groupValues[1].length// 注意：这里加了1是为了包含 </em>
            matchResult.groupValues[1] // 返回匹配到的内容
        }

        val spannableString = SpannableString(Text)
        val colorSpan = ForegroundColorSpan(0xFF427C)
        spannableString.setSpan(colorSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        holder.binding.title.text = spannableString
        holder.binding.detail.text = dataList[position].play.toString()
        holder.binding.timeLong.text = dataList[position].duration

        Glide.with(holder.binding.root)
            .load("https://${dataList[position].pic}")
            .transition(DrawableTransitionOptions.withCrossFade()) // 添加淡入淡出效果
            .into(holder.binding.image)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun addData(data: MutableList<SearchDataDetail>): MutableList<SearchDataDetail> {
        dataList.addAll(data)
        notifyItemRangeInserted(dataList.size + 1, data.size)
        return dataList
    }
}

