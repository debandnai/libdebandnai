package com.merkaaz.app.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.merkaaz.app.ui.fragments.FilterByTabFragment
import com.merkaaz.app.ui.fragments.SortByTabFragment
import com.merkaaz.app.utils.Constants


public class ViewPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    val numOfTabs: Int
) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return numOfTabs
    }

    override fun createFragment(position: Int): Fragment {
        return if (numOfTabs == 1)
            SortByTabFragment()
        else {
            when (position) {
                0 -> FilterByTabFragment()
                else -> SortByTabFragment()
            }
        }

    }
}