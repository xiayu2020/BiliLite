package com.jfkj.bililitebyxiayu.style.mainstyle.adpater

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class PageAdapter(
    Activity: AppCompatActivity,
    val fragmentList: MutableList<Fragment>
) : FragmentStateAdapter(Activity) {

    fun addFragment(fragment: Fragment) {
        fragmentList.add(fragment)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }

}