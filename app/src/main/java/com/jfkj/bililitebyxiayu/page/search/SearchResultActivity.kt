package com.jfkj.bililitebyxiayu.page.search

import android.content.res.Resources
import android.os.Bundle
import android.text.Editable
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.viewpager2.widget.ViewPager2
import com.jfkj.bililitebyxiayu.R
import com.jfkj.bililitebyxiayu.databinding.ActivitySearchResultBinding
import com.jfkj.bililitebyxiayu.func.dpToPx
import com.jfkj.bililitebyxiayu.page.search.fragment.Fragment_Search_Result
import com.jfkj.bililitebyxiayu.style.mainstyle.adpater.PageAdapter

class SearchResultActivity : AppCompatActivity() {
    lateinit var binding: ActivitySearchResultBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search_result)
        val text=intent.getStringExtra("text")?:""
        binding.edit.setText(text)
        val adapter = PageAdapter(
            this, mutableListOf(
                Fragment_Search_Result(text,"video"), Fragment_Search_Result(text,"bili_user"),Fragment_Search_Result(text,"article")
            )
        )
        binding.ViewPager.adapter=adapter
        val StartPixel = dpToPx(7f, this)
        val Width = Resources.getSystem().getDisplayMetrics().widthPixels-StartPixel*8
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
                    val newMarginLeft = StartPixel + (Width / 4) * positionOffset
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

    }
}