package com.jfkj.bililitebyxiayu.page.user


import android.content.res.Resources
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Display
import android.view.ViewGroup
import android.view.WindowMetrics
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.jfkj.bililitebyxiayu.R
import com.jfkj.bililitebyxiayu.databinding.ActivityUserBinding
import com.jfkj.bililitebyxiayu.func.Https
import com.jfkj.bililitebyxiayu.func.UserCardResult
import com.jfkj.bililitebyxiayu.func.dpToPx
import com.jfkj.bililitebyxiayu.func.user_agent
import com.jfkj.bililitebyxiayu.page.user.fragment.Fragment_Video
import com.jfkj.bililitebyxiayu.style.mainstyle.adpater.PageAdapter
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.util.Random

//import com.jfkj.bililitebyxiayu.func.getWbiKeys


class UserActivity : AppCompatActivity() {
    lateinit var binding: ActivityUserBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user)

//        val (imgKey, subKey) = getWbiKeys()
//        getWbiKeys()
        val url = "https://api.bilibili.com/x/web-interface/card"
        val headers = mapOf(
            "User-Agent" to user_agent
        )
        val cookie_str = getSharedPreferences("data", MODE_PRIVATE)
            .getString("cookie", "null") ?: "null"
        val mid = intent.getStringExtra("mid") ?: "1"
        val data = mapOf(
            "mid" to mid,
            "photo" to "true",
        )
        val okhttp = Https()
        okhttp.init(cookie_str)
        okhttp.get(url, data, headers, callback = {
            val json = Json { ignoreUnknownKeys = true }
            val Map: UserCardResult = json.decodeFromString(it)
            runOnUiThread {
                //up头像
                Glide.with(binding.root)
                    .load(Map.data.card.face)
                    .transition(DrawableTransitionOptions.withCrossFade()) // 添加淡入淡出效果
                    .into(binding.face)
                binding.name.text = Map.data.card.name
                binding.detail.text = Map.data.card.sign
                binding.star.text = "${Map.data.card.attention}关注"
                binding.fans.text = "${Map.data.card.fans}粉丝"

            }
        })
        val adapter = PageAdapter(
            this, mutableListOf(
                Fragment_Video(mid), Fragment_Video(mid)
            )
        )
        val StartPixel = dpToPx(7f, this)
        val Width = Resources.getSystem().getDisplayMetrics().widthPixels-StartPixel*6
        binding.ViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                // 页面正在滚动中的回调
                // position：当前页面的位置
                // positionOffset：当前页面滚动的偏移量，范围为[0, 1]
                // positionOffsetPixels：
                if (positionOffset != 0f) {
                    val layoutParams =
                        binding.CheckBottom.layoutParams as ViewGroup.MarginLayoutParams
                    // 修改 marginLeft 的值（以像素为单位）
                    val newMarginLeft = StartPixel + (Width / 3) * positionOffset
                    layoutParams.leftMargin = newMarginLeft.toInt()
                    binding.CheckBottom.layoutParams = layoutParams
                }
            }

            override fun onPageSelected(position: Int) {
                // 页面被选中的回调

            }

            override fun onPageScrollStateChanged(state: Int) {
                // 页面滚动状态改变的回调
                // state：滚动状态，有三个值：ViewPager2.SCROLL_STATE_IDLE、ViewPager2.SCROLL_STATE_DRAGGING、ViewPager2.SCROLL_STATE_SETTLING
            }
        })
        val random = Random().nextInt(4)
        var pic = ""
        when (random) {
            0 -> pic = "https://pic.imgdb.cn/item/65bba812871b83018a61a1de.jpg"
            1 -> pic = "https://pic.imgdb.cn/item/65bba807871b83018a6166a2.jpg"
            2 -> pic = "https://pic.imgdb.cn/item/65bba7e7871b83018a60c71b.jpg"
            3 -> pic = "https://pic.imgdb.cn/item/65bba7b3871b83018a5fc16f.jpg"
        }
        //up背景图
        binding.backgroud?.let {
            Glide.with(binding.root)
                .load(pic)
                .transition(DrawableTransitionOptions.withCrossFade()) // 添加淡入淡出效果
                .into(it)
        }
        binding.ViewPager.adapter = adapter
    }

}





