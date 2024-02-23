package com.jfkj.bililitebyxiayu.page.player

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.jfkj.bililitebyxiayu.R
import com.jfkj.bililitebyxiayu.databinding.ActivityVideoBinding
import com.jfkj.bililitebyxiayu.func.Https
import com.jfkj.bililitebyxiayu.func.VideoDiversityResult
import com.jfkj.bililitebyxiayu.func.dpToPx
import com.jfkj.bililitebyxiayu.func.user_agent
import com.jfkj.bililitebyxiayu.page.player.fragment.CommentFragment
import com.jfkj.bililitebyxiayu.page.player.fragment.DetailVideoFragment
import com.jfkj.bililitebyxiayu.page.player.fragment.PlayerFragment
import com.jfkj.bililitebyxiayu.style.mainstyle.adpater.PageAdapter
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json


class VideoActivity : AppCompatActivity() {
    lateinit var binding: ActivityVideoBinding
    lateinit var fragment: PlayerFragment
    lateinit var fragmentTransaction: FragmentTransaction
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_video)
        val aid = intent.getStringExtra("aid") ?: "239342773"
        val bvid = intent.getStringExtra("bvid") ?: "239342773"
        //加载播放器fragment
        if (intent.getStringExtra("cid") == "null") {
            val url = "https://api.bilibili.com/x/player/pagelist"
            val headers = mapOf(
                "User-Agent" to user_agent
            )
            val cookie_str = getSharedPreferences("data", Context.MODE_PRIVATE)
                .getString("cookie", "null") ?: "null"
            val data = mapOf(
                "aid" to aid,
            )
            val okhttp = Https()
            okhttp.init(cookie_str)
            okhttp.get(url, data, headers, callback = {
                val json = Json { ignoreUnknownKeys = true }
                val Map: VideoDiversityResult = json.decodeFromString(it)
                runOnUiThread {
                    fragment = PlayerFragment(
                        intent.getStringExtra("bvid") ?: "1",
                        Map.data[0].cid.toString(),
                        intent.getStringExtra("title") ?: "",
                        binding.preview,
                    )
                    fragmentTransaction.add(R.id.Container, fragment)
                    fragmentTransaction.commit()
                }
            })
        } else {
            val fragmentManager = supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.add(
                R.id.Container,
                PlayerFragment(
                    intent.getStringExtra("bvid") ?: "1",
                    intent.getStringExtra("cid") ?: "1",
                    intent.getStringExtra("title") ?: "",
                    binding.preview,
                )
            )
            fragmentTransaction.commit()
        }
        //第一帧图片
        Glide.with(binding.root)
            .load(intent.getStringExtra("pic"))
            .transition(DrawableTransitionOptions.withCrossFade()) // 添加淡入淡出效果
            .into(binding.preview)


        //视频详细
        val adapter = PageAdapter(
            this, mutableListOf(
                DetailVideoFragment(aid, bvid), CommentFragment(aid)
            )
        )
        binding.ViewPager.adapter = adapter


    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // 在这里处理屏幕旋转后的逻辑
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // 屏幕变为横向
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,  // 宽度设置为MATCH_PARENT或具体数值
                LinearLayout.LayoutParams.MATCH_PARENT // 高度设置为WRAP_CONTENT或具体数值
            )
            binding.Container.setLayoutParams(params)
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,  // 宽度设置为MATCH_PARENT或具体数值
                dpToPx(235f, this).toInt(), // 高度设置为WRAP_CONTENT或具体数值
            )
            binding.Container.setLayoutParams(params)
        }
    }
fun getbinding(){
    binding
}
}



