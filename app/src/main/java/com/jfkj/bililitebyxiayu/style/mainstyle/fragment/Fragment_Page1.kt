package com.jfkj.bililitebyxiayu.style.mainstyle.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jfkj.bililitebyxiayu.R
import com.jfkj.bililitebyxiayu.databinding.FragmentMainHomeBinding
import com.jfkj.bililitebyxiayu.func.HomeResult
import com.jfkj.bililitebyxiayu.func.HomeVideoData
import com.jfkj.bililitebyxiayu.func.Https
import com.jfkj.bililitebyxiayu.func.user_agent
import com.jfkj.bililitebyxiayu.page.player.VideoActivity
import com.jfkj.bililitebyxiayu.style.mainstyle.adpater.Adpater_Page1
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json


class Fragment_Page1 : Fragment() {
    lateinit var binding: FragmentMainHomeBinding
    lateinit var mainstyle_adpater: Adpater_Page1
    lateinit var Map: HomeResult
    lateinit var Data: MutableList<HomeVideoData>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_main_home, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val displayMetrics = resources.displayMetrics
        val screenWidthDp = displayMetrics.widthPixels / displayMetrics.density.toInt()
        val columnWidthDp = 190 // 每列的宽度，单位为dp

        val spanCount = if(screenWidthDp>500) (screenWidthDp-150) / columnWidthDp else (screenWidthDp-40) / columnWidthDp
        val layoutManager = GridLayoutManager(requireContext(), spanCount)
        binding.list.layoutManager = layoutManager

        CreateData(true)
        binding.RefreshLayout.setColorSchemeResources(R.color.主题色)
        binding.RefreshLayout.setOnRefreshListener {
            CreateData(false)
        }
        binding.list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val layoutManager = binding.list.layoutManager as LinearLayoutManager
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                val totalItemCount = mainstyle_adpater.itemCount
                val isLastItem = lastVisibleItemPosition == totalItemCount - 1
                val isBottomReached = isLastItem && layoutManager.computeVerticalScrollOffset(
                    RecyclerView.State()
                ) + layoutManager.computeVerticalScrollExtent(RecyclerView.State()) >= layoutManager.computeVerticalScrollRange(
                    RecyclerView.State()
                )
                if (isBottomReached) {
                    CreateData(null)
                }
            }
        })


    }


    private fun CreateData(isFisrtOpen: Boolean?) {
        val url = "https://api.bilibili.com/x/web-interface/index/top/rcmd"
        val headers = mapOf(
            "User-Agent" to user_agent
        )
        val cookie_str = requireContext().getSharedPreferences("data", Context.MODE_PRIVATE)
            .getString("cookie", "null") ?: "null"
        val data = mapOf(
            "page" to "1",
            "page_size" to "20",
            "suggest_keyword" to "",
            "numResults" to "40",
        )
        val okhttp = Https()
        okhttp.init(cookie_str)
        okhttp.get(url, data, headers, callback = {
            val json = Json { ignoreUnknownKeys = true }
            Map = json.decodeFromString(it)
            requireActivity().runOnUiThread {
                if (isFisrtOpen == null) {
                   Data= mainstyle_adpater.addData(Map.data.item)
                } else if (isFisrtOpen) {
                    Data=Map.data.item
                    CreateAdpater()
                } else {
                    binding.RefreshLayout.isRefreshing = false
                    Data=mainstyle_adpater.fresh(Map.data.item)
                }
            }
        })
    }

    private fun CreateAdpater() {
        mainstyle_adpater = Adpater_Page1(Map.data.item, callback = { position, view ->
            val intent = Intent(
                requireContext(),
                VideoActivity::class.java
            )
            intent.putExtra("aid", Data[position].id.toString())
            intent.putExtra("cid", Data[position].cid.toString())
            intent.putExtra("bvid",Data[position].bvid)
            intent.putExtra("title",Data[position].title)
            intent.putExtra("pic", Data[position].pic)
            val options: ActivityOptionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                    requireActivity(),
                    view,
                    "video_card_transition",
                )
            startActivity(intent, options.toBundle())
        })
        binding.list.adapter = mainstyle_adpater
    }

}