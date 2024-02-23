package com.jfkj.bililitebyxiayu.page.search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jfkj.bililitebyxiayu.databinding.GridSearchOvalBinding


class Adpater_Search_History(private val dataList: MutableList<String>) :
    RecyclerView.Adapter<Adpater_Search_History.ViewHolder>() {

    class ViewHolder(val binding: GridSearchOvalBinding) : RecyclerView.ViewHolder(binding.root){

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = GridSearchOvalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Access the 'text' TextView through the binding
        holder.binding.text.text = dataList[position]
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}

