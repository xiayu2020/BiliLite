package com.jfkj.bililitebyxiayu.page.history_favour

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jfkj.bililitebyxiayu.R
import com.jfkj.bililitebyxiayu.databinding.ActivityHistroyFavourBinding
import com.jfkj.bililitebyxiayu.func.HistoryCursor
import com.jfkj.bililitebyxiayu.func.HistoryData
import com.jfkj.bililitebyxiayu.func.HistoryResult
import com.jfkj.bililitebyxiayu.func.Https
import com.jfkj.bililitebyxiayu.func.user_agent
import com.jfkj.bililitebyxiayu.page.history_favour.adpater.Adpater_History
import com.jfkj.bililitebyxiayu.page.player.VideoActivity
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class HistoryActivity : AppCompatActivity() {
    lateinit var binding: ActivityHistroyFavourBinding
    lateinit var Map: HistoryResult
    lateinit var Data:MutableList<HistoryData>
    lateinit var adpater: Adpater_History
    lateinit var LastCursor: HistoryCursor
    private var isFirst = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_histroy_favour)
        binding.title.text = "Histroy"
        binding.search.setOnClickListener {
            Toast.makeText(applicationContext, "开发中", Toast.LENGTH_SHORT).show()
        }
        val displayMetrics = resources.displayMetrics
        val screenWidthDp = displayMetrics.widthPixels / displayMetrics.density.toInt()
        val columnWidthDp = 500 // 每列的宽度，单位为dp

        val spanCount = (screenWidthDp-40)/ columnWidthDp
        binding.recy.layoutManager = GridLayoutManager(this, spanCount)

        CreateData()
        binding.recy.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val layoutManager = binding.recy.layoutManager as LinearLayoutManager
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                val totalItemCount = adpater.itemCount

                val isLastItem = lastVisibleItemPosition == totalItemCount - 1
                val isBottomReached = isLastItem && layoutManager.computeVerticalScrollOffset(RecyclerView.State()) + layoutManager.computeVerticalScrollExtent(RecyclerView.State()) >= layoutManager.computeVerticalScrollRange(RecyclerView.State())

                if (isBottomReached) {
                    CreateData()
                }
            }
        })
    }

    private fun CreateData() {
        val url = "https://api.bilibili.com/x/web-interface/history/cursor"
        val headers = mapOf(
            "User-Agent" to user_agent
        )
        val cookie_str = getSharedPreferences("data", Context.MODE_PRIVATE)
            .getString("cookie", "null") ?: "null"
        val data = if (isFirst) mapOf("ps" to "30") else mapOf(
            "max" to LastCursor.max.toString(),
            "business" to LastCursor.business,
            "view_at" to LastCursor.view_at.toString(),
            "ps" to "30"
         )
        val okhttp = Https()
        okhttp.init(cookie_str)
        okhttp.get(url, data, headers, callback = {
            val json = Json { ignoreUnknownKeys = true }
            Map = json.decodeFromString(it)
            LastCursor = Map.data.cursor
            //up头像
            runOnUiThread {
                if (isFirst) {
                    Data=Map.data.list
                    CreateAdapter()
                    isFirst = false
                } else {
                    Data=adpater.addData(Map.data.list)
                }

            }
        })
    }

    private fun CreateAdapter() {
        adpater = Adpater_History(Map.data.list, callback = { position, view ->
            val intent = Intent(
                this,
                VideoActivity::class.java
            )
            intent.putExtra("aid", Data[position].history.oid.toString())
            intent.putExtra("cid", Data[position].history.cid.toString())
            intent.putExtra("bvid", Data[position].history.bvid)
            intent.putExtra("title", Data[position].title)
            intent.putExtra("pic",Data[position].cover)
            val options: ActivityOptionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this,
                    view,
                    "video_card_transition",
                )
            startActivity(intent, options.toBundle())
        })
        binding.recy.adapter = adpater

    }
}