package com.jfkj.bililitebyxiayu.page.history_favour

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jfkj.bililitebyxiayu.R
import com.jfkj.bililitebyxiayu.databinding.ActivityHistroyFavourBinding
import com.jfkj.bililitebyxiayu.func.FavourDetailData
import com.jfkj.bililitebyxiayu.func.FavourDetailResult
import com.jfkj.bililitebyxiayu.func.FavourResult
import com.jfkj.bililitebyxiayu.func.Https
import com.jfkj.bililitebyxiayu.func.user_agent
import com.jfkj.bililitebyxiayu.page.history_favour.adpater.Adpater_Detail_Favour
import com.jfkj.bililitebyxiayu.page.history_favour.adpater.Apater_Favour
import com.jfkj.bililitebyxiayu.page.player.VideoActivity
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class FavourActivity : AppCompatActivity() {
    lateinit var binding: ActivityHistroyFavourBinding
    lateinit var Map: FavourDetailResult
    lateinit var Data: MutableList<FavourDetailData>
    lateinit var adpater: Adpater_Detail_Favour
    private var id = 0L
    private var page = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_histroy_favour)
        binding.title.text = "Star"
        binding.search.setOnClickListener {
            Toast.makeText(applicationContext, "开发中", Toast.LENGTH_SHORT).show()
        }
        val displayMetrics = resources.displayMetrics
        val screenWidthDp = displayMetrics.widthPixels / displayMetrics.density.toInt()
        val columnWidthDp = 500 // 每列的宽度，单位为dp

        val spanCount = (screenWidthDp-40)/ columnWidthDp
        binding.recy.layoutManager = GridLayoutManager(this, spanCount)
        binding.recy2.layoutManager = GridLayoutManager(this, spanCount)

        val url = "https://api.bilibili.com/x/v3/fav/folder/created/list-all"
        val mid = intent.getStringExtra("mid") ?: "1"
        val headers = mapOf(
            "User-Agent" to user_agent
        )
        val cookie_str = getSharedPreferences("data", Context.MODE_PRIVATE)
            .getString("cookie", "null") ?: "null"
        val data = mapOf(
            "up_mid" to mid,
        )
        val okhttp = Https()
        okhttp.init(cookie_str)
        okhttp.get(url, data, headers, callback = {
            val json = Json { ignoreUnknownKeys = true }
            val Js: FavourResult = json.decodeFromString(it)
            //up头像
            runOnUiThread {
                val myadpater=Apater_Favour(Js.data.list, callback = { position, view ->
                    binding.recy.visibility = ViewGroup.GONE
                    id = Js.data.list[position].id
                    page=1
                    CreateDetailData()
                })
                binding.recy.adapter = myadpater
            }
        })

        binding.recy2.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val layoutManager = binding.recy.layoutManager as LinearLayoutManager
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                val totalItemCount = adpater.itemCount

                val isLastItem = lastVisibleItemPosition == totalItemCount - 1
                val isBottomReached = isLastItem && layoutManager.computeVerticalScrollOffset(
                    RecyclerView.State()
                ) + layoutManager.computeVerticalScrollExtent(RecyclerView.State()) >= layoutManager.computeVerticalScrollRange(
                    RecyclerView.State()
                )
                if (isBottomReached) {
                    CreateDetailData()
                }
            }
        })
    }


    private fun CreateDetailData() {
        val url = "https://api.bilibili.com/x/v3/fav/resource/list"
        val headers = mapOf(
            "User-Agent" to user_agent
        )
        val cookie_str = getSharedPreferences("data", Context.MODE_PRIVATE)
            .getString("cookie", "null") ?: "null"
        val data = mapOf(
            "media_id" to id.toString(),
            "pn" to page.toString(),
            "ps" to "20",
        )
        val okhttp = Https()
        okhttp.init(cookie_str)
        okhttp.get(url, data, headers, callback = {
            val json = Json { ignoreUnknownKeys = true }
            Map = json.decodeFromString(it)
            //up头像
            runOnUiThread {
                if (page == 1) {
                    CreateDetailAdapter()
                    Data=Map.data.medias
                } else {
                    Data = adpater.addData(Map.data.medias)
                }
                println(page)
                page++
            }
        })
    }


    private fun CreateDetailAdapter() {
        adpater = Adpater_Detail_Favour(Map.data.medias, callback = { position, view ->
            val intent = Intent(
                this,
                VideoActivity::class.java
            )
            intent.putExtra("aid", Data[position].id.toString())
            intent.putExtra("cid", Data[position].ugc.first_cid.toString())
            intent.putExtra("bvid", Data[position].bvid)
            intent.putExtra("title", Data[position].title)
            intent.putExtra("pic", Data[position].cover)
            val options: ActivityOptionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this,
                    view,
                    "video_card_transition",
                )
            startActivity(intent, options.toBundle())
        })
        binding.recy2.adapter = adpater
    }
}