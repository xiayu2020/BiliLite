package com.jfkj.bililitebyxiayu.page.search.adapter

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jfkj.bililitebyxiayu.R
import com.jfkj.bililitebyxiayu.func.HomeVideoData
import com.jfkj.bililitebyxiayu.func.SearchTintData
import com.jfkj.bililitebyxiayu.func.dpToPx

class Adpater_Tint(
    private var dataList: MutableList<SearchTintData>,
    val callback: (View, Int) -> Unit
) :
    RecyclerView.Adapter<Adpater_Tint.ViewHolder>() {

    class ViewHolder(val liner: LinearLayout, val callback: (View, Int) -> Unit) :
        RecyclerView.ViewHolder(liner) {
        init {
            itemView.setOnClickListener {
                val position = absoluteAdapterPosition
                callback(liner.getChildAt(0),position)
            }
        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //val binding = GridSearchCicrleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val liner = LinearLayout(parent.context)
        liner.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, // 宽度设置为MATCH_PARENT
            dpToPx(45f,parent.context).toInt() // 高度设置为WRAP_CONTENT
        )
        val textview = TextView(parent.context)
        textview.textSize = 15f
        textview.setTextColor(parent.context.getColor(R.color.字体颜色))
        liner.addView(textview)


        return ViewHolder(liner, callback)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Access the 'text' TextView through the binding
        (holder.liner.getChildAt(0) as TextView).text = dataList[position].value
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun refresh(data:MutableList<SearchTintData>){
        dataList=data
        this.notifyDataSetChanged()
    }
}