package com.genlz.jetpacks.ui.community

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.genlz.jetpacks.ui.community.follow.FollowFragment
import com.genlz.jetpacks.ui.community.recommend.RecommendFragment

class CommunityPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = pageFactory.size

    override fun createFragment(position: Int): Fragment {
        val fragment = pageFactory[position]() as Fragment
        return fragment
    }

    companion object {
        private val pageFactory = listOf(
            RecommendFragment::newInstance,
            FollowFragment::newInstance
        )
    }
}