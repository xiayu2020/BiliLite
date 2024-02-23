package com.jfkj.bililitebyxiayu.style.mainstyle

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityOptionsCompat
import androidx.databinding.DataBindingUtil
import androidx.viewpager2.widget.ViewPager2
import com.jfkj.bililitebyxiayu.R
import com.jfkj.bililitebyxiayu.databinding.ActivityMainBinding
import com.jfkj.bililitebyxiayu.page.search.SearchActivity
import com.jfkj.bililitebyxiayu.style.mainstyle.adpater.PageAdapter
import com.jfkj.bililitebyxiayu.style.mainstyle.fragment.Fragment_Page1
import com.jfkj.bililitebyxiayu.style.mainstyle.fragment.Fragment_Page2
import com.jfkj.bililitebyxiayu.style.mainstyle.fragment.Fragment_Page3


class MainStyleActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private fun CheckBotton(position: Int) {
        when (position) {
            0 -> {
                binding.home.setImageResource(R.drawable.home_check)
                binding.status.setImageResource(R.drawable.status_no_check)
                binding.my.setImageResource(R.drawable.my_no_check)
                binding.pagename.text = "Home"
                binding.name.visibility = ViewGroup.GONE
            }

            1 -> {
                binding.home.setImageResource(R.drawable.home_no_check)
                binding.status.setImageResource(R.drawable.status_check)
                binding.my.setImageResource(R.drawable.my_no_check)
                binding.pagename.text = "Status"
                binding.name.visibility = ViewGroup.GONE
            }

            2 -> {
                binding.home.setImageResource(R.drawable.home_no_check)
                binding.status.setImageResource(R.drawable.status_no_check)
                binding.my.setImageResource(R.drawable.my_check)
                binding.pagename.text = "Hey,"
                binding.name.visibility = ViewGroup.VISIBLE
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        val adapter =
            PageAdapter(
                this,
                mutableListOf(Fragment_Page1(), Fragment_Page2(), Fragment_Page3(binding.name))
            )
        binding.ViewPager.adapter = adapter
        binding.homebotton.setOnClickListener {
            CheckBotton(0)
            binding.ViewPager.setCurrentItem(0, true)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
        binding.statusbotton.setOnClickListener {
            CheckBotton(1)
            binding.ViewPager.setCurrentItem(1, true)
        }
        binding.mybotton.setOnClickListener {
            CheckBotton(2)
            binding.ViewPager.setCurrentItem(2, true)
        }
        binding.ViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                // 页面正在滚动中的回调
                // position：当前页面的位置
                // positionOffset：当前页面滚动的偏移量，范围为[0, 1]
                // positionOffsetPixels：当前页面滚动的像素偏移量
            }

            override fun onPageSelected(position: Int) {
                // 页面被选中的回调
                CheckBotton(position)
            }

            override fun onPageScrollStateChanged(state: Int) {
                // 页面滚动状态改变的回调
                // state：滚动状态，有三个值：ViewPager2.SCROLL_STATE_IDLE、ViewPager2.SCROLL_STATE_DRAGGING、ViewPager2.SCROLL_STATE_SETTLING
            }
        })
        binding.search.setOnClickListener {
            // 创建 Bundle 对象，用于传递数据和实现过渡动画
            val intent = Intent(this, SearchActivity::class.java)
            val options: ActivityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this,
                binding.search,
                "search_transition",
            )
            startActivity(intent, options.toBundle())
        }


    }
}


