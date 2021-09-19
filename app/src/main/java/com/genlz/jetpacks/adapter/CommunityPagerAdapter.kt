package com.genlz.jetpacks.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.genlz.jetpacks.ui.community.Titleable
import com.genlz.jetpacks.ui.community.follow.FollowFragment
import com.genlz.jetpacks.ui.community.recommend.RecommendFragment
import kotlin.reflect.KFunction0

class CommunityPagerAdapter(
    fragment: Fragment,
    val titles: List<Int>,
) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = pageFactory.size

    override fun createFragment(position: Int): Fragment {
        val f = pageFactory[position](titles[position])
        return f as Fragment
    }

    companion object {
        private val pageFactory = listOf(
            RecommendFragment::newInstance,
            FollowFragment::newInstance
        )
    }
}