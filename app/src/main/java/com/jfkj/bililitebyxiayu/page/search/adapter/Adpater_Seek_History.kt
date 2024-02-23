package com.jfkj.bililitebyxiayu.page.search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jfkj.bililitebyxiayu.databinding.GridSearchCicrleBinding
import com.jfkj.bililitebyxiayu.func.HistoryData

class Adpater_Seek_History(private val dataList: MutableList<HistoryData>, val callback: (Int) -> Unit) :
    RecyclerView.Adapter<Adpater_Seek_History.ViewHolder>() {

    class ViewHolder(val binding: GridSearchCicrleBinding,val callback: (Int) -> Unit) : RecyclerView.ViewHolder(binding.root){
        init {
            itemView.setOnClickListener {
                val position = absoluteAdapterPosition
                callback(position)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = GridSearchCicrleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding,callback)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Access the 'text' TextView through the binding
        holder.binding.text.text = dataList[position].title
        holder.binding.type.text = dataList[position].title.substring(0,1)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}