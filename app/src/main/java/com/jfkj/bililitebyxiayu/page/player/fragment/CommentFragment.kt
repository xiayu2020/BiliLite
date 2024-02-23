package com.jfkj.bililitebyxiayu.page.player.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jfkj.bililitebyxiayu.func.CommentReplies
import com.jfkj.bililitebyxiayu.func.CommentResult
import com.jfkj.bililitebyxiayu.func.Https
import com.jfkj.bililitebyxiayu.func.getWbiKeys
import com.jfkj.bililitebyxiayu.func.user_agent
import com.jfkj.bililitebyxiayu.page.player.adpater.CommentAdpater
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class CommentFragment(val aid: String) : Fragment() {
    lateinit var recy: RecyclerView
    lateinit var Map: CommentResult
    lateinit var adpater: CommentAdpater
    lateinit var Data: MutableList<CommentReplies>
    private var page = 0
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
        recy.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val layoutManager = recy.layoutManager as LinearLayoutManager
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                val totalItemCount = adpater.itemCount
                val isLastItem = lastVisibleItemPosition == totalItemCount - 1
                val isBottomReached = isLastItem && layoutManager.computeVerticalScrollOffset(
                    RecyclerView.State()
                ) + layoutManager.computeVerticalScrollExtent(RecyclerView.State()) >= layoutManager.computeVerticalScrollRange(
                    RecyclerView.State()
                )
                if (isBottomReached) {
                    CreateData()
                }
            }
        })
    }

    private fun CreateData() {
        val sharedPrefs = requireContext().getSharedPreferences("data", Context.MODE_PRIVATE)
        val params: LinkedHashMap<String, Any> = linkedMapOf(
            "oid" to aid,
            "type" to "1",
            "next" to page
        )
        getWbiKeys(sharedPrefs, params, callback = {
            val query = it.second
            val headers = mapOf(
                "User-Agent" to user_agent
            )
            val cookie_str = sharedPrefs.getString("cookie", "null") ?: "null"
            val okhttp = Https()
            okhttp.init(cookie_str)
            okhttp.get(
                "https://api.bilibili.com/x/v2/reply/wbi/main?$query",
                null,
                headers,
                callback = { userdata ->
                    val json = Json { ignoreUnknownKeys = true }
                    Map = json.decodeFromString(userdata)
                    requireActivity().runOnUiThread {
                        if (page == 0) {
                            Data=Map.data.replies
                            CreateAdpater()
                        } else {
                            Data = adpater.addData(Map.data.replies)
                        }
                        page++
                    }
                })
        })
    }

    private fun CreateAdpater() {
        adpater = CommentAdpater(Map.data.replies, callback = { it, view ->


        })
        recy.adapter = adpater
    }

}