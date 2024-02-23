package com.jfkj.bililitebyxiayu.page.search.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jfkj.bililitebyxiayu.func.Https
import com.jfkj.bililitebyxiayu.func.SearchDataDetail
import com.jfkj.bililitebyxiayu.func.SearchResult
import com.jfkj.bililitebyxiayu.func.getWbiKeys
import com.jfkj.bililitebyxiayu.func.user_agent
import com.jfkj.bililitebyxiayu.page.player.VideoActivity
import com.jfkj.bililitebyxiayu.page.search.adpaterpage.Adpater_Video_Data
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class Fragment_Search_Result(val text: String, val type: String) : Fragment() {
    lateinit var recy: RecyclerView
    lateinit var Adpater: Adpater_Video_Data
    lateinit var Map: SearchResult
    lateinit var Data: MutableList<SearchDataDetail>
    private var page = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        recy = RecyclerView(requireContext())
        recy.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, // 宽度设置为MATCH_PARENT
            LinearLayout.LayoutParams.WRAP_CONTENT // 高度设置为WRAP_CONTENT
        )
        recy.layoutManager = LinearLayoutManager(requireContext())
        recy.setLayoutManager(
            LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
            )
        )
        return recy
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        CreateData()

    }

    private fun CreateData() {
        val sharedPrefs = requireContext().getSharedPreferences("data", Context.MODE_PRIVATE)
        val params: LinkedHashMap<String, Any> = linkedMapOf(
            "keyword" to text,
            "ps" to "20",
            "pn" to page.toString(),
            "search_type" to type,
        )
        getWbiKeys(sharedPrefs, params, callback = {
            val url = "https://api.bilibili.com/x/web-interface/wbi/search/type?${it.second}"
            val headers = mapOf(
                "User-Agent" to user_agent
            )
            val cookie_str = sharedPrefs.getString("cookie", "null") ?: "null"
            val okhttp = Https()
            okhttp.init(cookie_str)
            okhttp.get(url, null , headers, callback = {data->
                println(data)
                val json = Json { ignoreUnknownKeys = true }
                Map = json.decodeFromString(data)
                //up头像
                requireActivity().runOnUiThread {
                    Data=Map.data.result
                    if (page == 1) {
                        CreateAdpater()
                    } else {
                        Adpater.addData(Data)
                    }
                    page++
                }
            })
        })
    }

    private fun CreateAdpater() {
        Adpater = Adpater_Video_Data(Data, callback = { position, view ->
            val options: ActivityOptionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                    requireActivity(),
                    view,
                    "search_transition",
                )
            val intent = Intent(
                requireContext(),
                VideoActivity::class.java
            )
            intent.putExtra("aid", Data[position].aid.toString())
            intent.putExtra("cid", Data[position].id.toString())
            intent.putExtra("bvid", Data[position].bvid)
            intent.putExtra("title", Data[position].title)
            intent.putExtra("pic", Data[position].pic)
            startActivity(intent, options.toBundle())

        })
        recy.adapter = Adpater
    }

}