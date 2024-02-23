package com.jfkj.bililitebyxiayu.page.user.fragment

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
import com.jfkj.bililitebyxiayu.func.UserVideoResult
import com.jfkj.bililitebyxiayu.func.getWbiKeys
import com.jfkj.bililitebyxiayu.func.user_agent
import com.jfkj.bililitebyxiayu.page.player.VideoActivity
import com.jfkj.bililitebyxiayu.page.user.adpater.Adpater_User_Video
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class Fragment_Video(val mid:String) : Fragment() {
    lateinit var recy: RecyclerView
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

        val sharedPrefs = getActivity()?.getSharedPreferences("data", Context.MODE_PRIVATE)
        val params: LinkedHashMap<String, Any> = linkedMapOf(
            "mid" to mid,
        )
        getWbiKeys(sharedPrefs, params, callback = {
            val param = it.first
            val cookie_str = requireContext().getSharedPreferences("data", Context.MODE_PRIVATE)
                .getString("cookie", "null")?:"null"
            val query = it.second
            val okhttp = Https()
            okhttp.init(cookie_str)
            val headers = mapOf(
                "User-Agent" to user_agent
            )
            okhttp.get(
                "https://api.bilibili.com/x/space/wbi/arc/search?$query",
                null,
                headers,
                callback = {userdata->
                    val json = Json { ignoreUnknownKeys = true }
                    val Map: UserVideoResult = json.decodeFromString(userdata)
                    requireActivity().runOnUiThread {
                        val user_video_adpater = Adpater_User_Video(Map.data.list.vlist, callback = {position,view->
                            val intent = Intent(
                                requireContext(),
                                VideoActivity::class.java
                            )
                            println(userdata)
                            intent.putExtra("aid", Map.data.list.vlist[position].aid.toString())
                            intent.putExtra("cid", "null")
                            intent.putExtra("bvid", Map.data.list.vlist[position].bvid)
                            intent.putExtra("title", Map.data.list.vlist[position].title)
                            intent.putExtra("pic", Map.data.list.vlist[position].pic)
                            val options: ActivityOptionsCompat =
                                ActivityOptionsCompat.makeSceneTransitionAnimation(
                                    requireActivity(),
                                    view,
                                    "video_card_transition",
                                )
                            startActivity(intent, options.toBundle())

                        })
                        recy.adapter = user_video_adpater

                    }
                })
        })

    }


}