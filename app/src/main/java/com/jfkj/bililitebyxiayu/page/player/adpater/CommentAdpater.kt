package com.jfkj.bililitebyxiayu.page.player.adpater

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.jfkj.bililitebyxiayu.R
import com.jfkj.bililitebyxiayu.databinding.ListCommentBinding
import com.jfkj.bililitebyxiayu.func.CommentImage
import com.jfkj.bililitebyxiayu.func.CommentReplies
import com.jfkj.bililitebyxiayu.func.HomeVideoData
import com.jfkj.bililitebyxiayu.func.dpToPx

class CommentAdpater(
    private val dataList: MutableList<CommentReplies>,
    val callback: (Int, View) -> Unit
) : RecyclerView.Adapter<CommentAdpater.ViewHolder>() {
    class ViewHolder(val binding: ListCommentBinding, val callback: (Int, View) -> Unit) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                val position = absoluteAdapterPosition
                callback(position, binding.text)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, callback)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Access the 'text' TextView through the binding

        holder.binding.text.text = dataList[position].content.message
        holder.binding.UserName.text = dataList[position].member.uname
        holder.binding.Time.text = dataList[position].reply_control.time_desc

        Glide.with(holder.binding.root)
            .load(dataList[position].member.avatar)
            .transition(DrawableTransitionOptions.withCrossFade()) // 添加淡入淡出效果
            .into(holder.binding.face)

        val Image=dataList[position].content.pictures
        if (!Image.isNullOrEmpty()){
            val ImageAdpater=CommentImageAdpater(Image, callback = { position,view->

            })
            holder.binding.ImageAdpater.adapter=ImageAdpater
        }else{
            holder.binding.ImageAdpater.visibility=ViewGroup.GONE
        }
        val Reply = dataList[position].replies
        if (!Reply.isNullOrEmpty()) {
            val adpater = CommentDetailAdpater(Reply) { it, view ->

            }
            holder.binding.RepliesAdpater.adapter = adpater
        }else{
            holder.binding.replies.visibility=ViewGroup.GONE
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
    fun addData(data: MutableList<CommentReplies>): MutableList<CommentReplies> {
        dataList.addAll(data)
        notifyItemRangeInserted(dataList.size + 1, data.size)
        return dataList
    }
}



class CommentDetailAdpater(
    private var dataList: MutableList<CommentReplies>,
    val callback: (View, Int) -> Unit
) :
    RecyclerView.Adapter<CommentDetailAdpater.ViewHolder>() {

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
        val param=LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, // 宽度设置为MATCH_PARENT
            LinearLayout.LayoutParams.WRAP_CONTENT, // 高度设置为WRAP_CONTENT
        )
        liner.layoutParams = param
        param.bottomMargin=dpToPx(10f,parent.context).toInt()
        liner.orientation=LinearLayout.HORIZONTAL
        val textview = TextView(parent.context)
        textview.textSize = 13f
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        textview.setTextColor(parent.context.getColor(R.color.主题色2))
        liner.addView(textview)

        val textview2 = TextView(parent.context)
        textview2.textSize = 13f
        textview2.setTextColor(parent.context.getColor(R.color.字体颜色))
        val params2 = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        textview2.layoutParams=params2
        params2.marginStart = dpToPx(10f,parent.context).toInt()
        liner.addView(textview2)


        return ViewHolder(liner, callback)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Access the 'text' TextView through the binding
        (holder.liner.getChildAt(0) as TextView).text = dataList[position].member.uname
        (holder.liner.getChildAt(1) as TextView).text = dataList[position].content.message
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}



class CommentImageAdpater(
    private val dataList: MutableList<CommentImage>,
    val callback: (View, Int) -> Unit
) :
    RecyclerView.Adapter<CommentImageAdpater.ViewHolder>() {

    class ViewHolder(val card: CardView, val callback: (View, Int) -> Unit) :
        RecyclerView.ViewHolder(card) {
        init {
            itemView.setOnClickListener {
                val position = absoluteAdapterPosition
                callback(card.getChildAt(0),position)
            }
        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //val binding = GridSearchCicrleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val card = CardView(parent.context)
        val param=LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, // 宽度设置为MATCH_PARENT
            LinearLayout.LayoutParams.WRAP_CONTENT, // 高度设置为WRAP_CONTENT
        )
        card.layoutParams = param
        card.radius=dpToPx(9f,parent.context)
        param.marginEnd=dpToPx(10f,parent.context).toInt()





        return ViewHolder(card, callback)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val imageview = ImageView(holder.card.context)
        val params = LinearLayout.LayoutParams(
            dpToPx(105f*dataList[position].img_width/dataList[position].img_size,holder.card.context).toInt(),
            dpToPx(95f*dataList[position].img_height/dataList[position].img_size,holder.card.context).toInt(),
        )
        imageview.layoutParams = params
        imageview.scaleType=ImageView.ScaleType.CENTER_CROP
        holder.card.addView(imageview)
        Glide.with(holder.card.context)
            .load(dataList[position].img_src)
            .transition(DrawableTransitionOptions.withCrossFade()) // 添加淡入淡出效果
            .into(imageview)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}
