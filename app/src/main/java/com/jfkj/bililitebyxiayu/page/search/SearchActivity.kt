package com.jfkj.bililitebyxiayu.page.search

import android.app.appsearch.SearchResult
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.databinding.DataBindingUtil
import com.jfkj.bililitebyxiayu.R
import com.jfkj.bililitebyxiayu.databinding.ActivitySearchBinding
import com.jfkj.bililitebyxiayu.func.HistoryResult
import com.jfkj.bililitebyxiayu.func.Https
import com.jfkj.bililitebyxiayu.func.MyStarResult
import com.jfkj.bililitebyxiayu.func.SearchTintResult
import com.jfkj.bililitebyxiayu.func.user_agent
import com.jfkj.bililitebyxiayu.page.player.VideoActivity
import com.jfkj.bililitebyxiayu.page.search.adapter.Adpater_My_Star
import com.jfkj.bililitebyxiayu.page.search.adapter.Adpater_Seek_History
import com.jfkj.bililitebyxiayu.page.search.adapter.Adpater_Tint
import com.jfkj.bililitebyxiayu.page.user.UserActivity
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class SearchActivity : AppCompatActivity() {
    lateinit var binding: ActivitySearchBinding
    lateinit var TintAdpater: Adpater_Tint
    lateinit var Map: SearchTintResult
    private var isFirst = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search)

        //播放历史相关
        seekhistroy()
        //关注提示相关
        mystar()

        binding.edit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun afterTextChanged(editable: Editable) {
                if (editable.toString().trim()!=""){
                    Tint(editable.toString().trim())
                    binding.func.visibility=ViewGroup.GONE
                }else{
                    binding.func.visibility=ViewGroup.VISIBLE
                }

            }
        })
    }

    private fun seekhistroy() {
        val url = "https://api.bilibili.com/x/web-interface/history/cursor"
        val headers = mapOf(
            "User-Agent" to user_agent
        )
        val cookie_str = getSharedPreferences("data", Context.MODE_PRIVATE)
            .getString("cookie", "null") ?: "null"
        val data = mapOf(
            "ps" to "10",
        )
        val okhttp = Https()
        okhttp.init(cookie_str)
        okhttp.get(url, data, headers, callback = {
            val json = Json { ignoreUnknownKeys = true }
            val Map: HistoryResult = json.decodeFromString(it)
            //up头像
            runOnUiThread {
                val mystar_adpater = Adpater_Seek_History(Map.data.list, callback = { position ->
//                    val intent = Intent(this, VideoActivity::class.java)
//                    intent.putExtra("aid", Map.data.list[position].id.toString())
//                    intent.putExtra("cid", Map.data.item[position].cid.toString())
//                    intent.putExtra("bvid", Map.data.item[position].bvid)
//                    intent.putExtra("title", Map.data.item[position].title)
//                    intent.putExtra("pic", Map.data.item[position].pic)
//                    startActivity(intent)
                    val intent = Intent(
                        this,
                        VideoActivity::class.java
                    )
                    intent.putExtra("aid", Map.data.list[position].history.oid.toString())
                    intent.putExtra("cid", Map.data.list[position].history.cid.toString())
                    intent.putExtra("bvid", Map.data.list[position].history.bvid)
                    intent.putExtra("title", Map.data.list[position].title)
                    intent.putExtra("pic", Map.data.list[position].cover)
                    startActivity(intent)
                })
                binding.seekHistory.adapter = mystar_adpater

            }
        })
    }

    private fun mystar() {
        val url = "https://api.bilibili.com/x/relation/followings"
        val headers = mapOf(
            "User-Agent" to user_agent
        )
        val cookie_str = getSharedPreferences("data", Context.MODE_PRIVATE)
            .getString("cookie", "null") ?: "null"
        val mid = getSharedPreferences("data", Context.MODE_PRIVATE).getString("mid", "1") ?: "1"
        val data = mapOf(
            "vmid" to mid,
            "order" to "asc",
            "ps" to "10",
            "pn" to "1"
        )
        val okhttp = Https()
        okhttp.init(cookie_str)
        okhttp.get(url, data, headers, callback = {
            val json = Json { ignoreUnknownKeys = true }
            val Map: MyStarResult = json.decodeFromString(it)
            //up头像
            runOnUiThread {
                val mystar_adpater = Adpater_My_Star(Map.data.list, callback = { view, position ->
                    val options: ActivityOptionsCompat =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                            this,
                            view,
                            "user_card_transition",
                        )
                    val intent = Intent(this, UserActivity::class.java)
                    intent.putExtra("mid", Map.data.list[position].mid.toString())
                    startActivity(intent, options.toBundle())

                })
                binding.myStar.adapter = mystar_adpater

            }
        })
    }

    private fun Tint(text: String) {
        val url = "https://s.search.bilibili.com/main/suggest"
        val headers = mapOf(
            "User-Agent" to user_agent
        )
        val cookie_str = getSharedPreferences("data", Context.MODE_PRIVATE)
            .getString("cookie", "null") ?: "null"
        val data = mapOf(
            "term" to text,
        )
        val okhttp = Https()
        okhttp.init(cookie_str)
        okhttp.get(url, data, headers, callback = {
            val json = Json { ignoreUnknownKeys = true }
             Map = json.decodeFromString(it)
            //up头像
            runOnUiThread {
                if (isFirst) {
                    CreateTintAdpater()
                } else {
                    TintAdpater.refresh(Map.result.tag)
                }

            }
        })
    }

    private fun CreateTintAdpater() {
        TintAdpater = Adpater_Tint(Map.result.tag, callback = { view, position ->
            val options: ActivityOptionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this,
                    view,
                    "search_transition",
                )
            val intent = Intent(this, SearchResultActivity::class.java)
            intent.putExtra("text", Map.result.tag[position].value)
            startActivity(intent, options.toBundle())

        })
        binding.tint.adapter = TintAdpater
    }
}