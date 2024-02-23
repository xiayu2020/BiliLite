package com.jfkj.bililitebyxiayu.style.mainstyle.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.jfkj.bililitebyxiayu.R
import com.jfkj.bililitebyxiayu.databinding.FragmentMainUserBinding
import com.jfkj.bililitebyxiayu.func.Https
import com.jfkj.bililitebyxiayu.func.MyResult
import com.jfkj.bililitebyxiayu.func.UserCardResult
import com.jfkj.bililitebyxiayu.func.user_agent
import com.jfkj.bililitebyxiayu.page.history_favour.FavourActivity
import com.jfkj.bililitebyxiayu.page.history_favour.HistoryActivity
import com.jfkj.bililitebyxiayu.page.user.UserActivity
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class Fragment_Page3(val NameView:TextView) : Fragment() {
    lateinit var binding: FragmentMainUserBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_main_user, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //基本信息
        MyBaseData()
        val mid = requireContext().getSharedPreferences("data", Context.MODE_PRIVATE).getString("mid","1")?:"1"
        val url = "https://api.bilibili.com/x/web-interface/card"
        val headers = mapOf(
            "User-Agent" to user_agent
        )
        val cookie_str = requireContext().getSharedPreferences("data", Context.MODE_PRIVATE)
            .getString("cookie", "null") ?: "null"
        val data = mapOf(
            "mid" to mid,
            "photo" to "true",
        )
        val okhttp = Https()
        okhttp.init(cookie_str)
        okhttp.get(url, data, headers, callback = {
            val json = Json { ignoreUnknownKeys = true }
            val Map: UserCardResult = json.decodeFromString(it)

            requireActivity().runOnUiThread {
                //up头像
                Glide.with(binding.root)
                    .load(Map.data.card.face)
                    .transition(DrawableTransitionOptions.withCrossFade()) // 添加淡入淡出效果
                    .into(binding.face)
                NameView.text = Map.data.card.name
                binding.detail.text = Map.data.card.sign
                binding.star.text = Map.data.card.attention.toString()
                binding.fans.text = Map.data.card.fans.toString()

            }
        })

        binding.user.setOnClickListener {
            val options: ActivityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                requireActivity(),
                binding.usercard,
                "user_card_transition",
            )
            val intent = Intent(requireContext(), UserActivity::class.java)
            intent.putExtra("mid", mid)
            startActivity(intent, options.toBundle())
        }

        binding.userdata.setOnClickListener{
            val options: ActivityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                requireActivity(),
                binding.usercard,
                "user_card_transition",
            )
            val intent = Intent(requireContext(), UserActivity::class.java)
            intent.putExtra("mid", mid)
            startActivity(intent, options.toBundle())
        }
        binding.history.setOnClickListener{
            val intent = Intent(requireContext(), HistoryActivity::class.java)
            startActivity(intent)
        }
        binding.favour.setOnClickListener{
            val intent = Intent(requireContext(), FavourActivity::class.java)
            intent.putExtra("mid", mid)
            startActivity(intent)
        }
    }

    private fun MyBaseData() {
        val url = "https://api.bilibili.com/x/web-interface/nav"
        val headers = mapOf(
            "User-Agent" to user_agent
        )
        val sharedPrefs = requireContext().getSharedPreferences("data", Context.MODE_PRIVATE)
        val cookie_str = sharedPrefs.getString("cookie", "null") ?: "null"
        val okhttp = Https()
        okhttp.init(cookie_str)
        okhttp.get(url, null, headers, callback = {
            val json = Json { ignoreUnknownKeys = true }
            val Map: MyResult = json.decodeFromString(it)
            requireActivity().runOnUiThread{
                binding.coin.text=Map.data.money.toString()
                binding.moral.text=Map.data.moral.toString()
            }
            val imgUrl = Map.data.wbi_img.img_url
            val subUrl = Map.data.wbi_img.sub_url
            val imgKey = imgUrl.split('/').last().split('.').first()
            val subKey = subUrl.split('/').last().split('.').first()
            // 写入key
            with(sharedPrefs.edit()) {
                putString("imgKey", imgKey)
                putString("subKey", subKey)
                putString("mid", Map.data.mid.toString())
                apply()
            }
        })
    }
}