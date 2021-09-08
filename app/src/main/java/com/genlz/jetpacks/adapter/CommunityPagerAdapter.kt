package com.genlz.jetpacks.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.genlz.jetpacks.ui.community.Titleable
import com.genlz.jetpacks.ui.community.follow.FollowFragment
import com.genlz.jetpacks.ui.community.recommend.RecommendFragment
import kotlin.reflect.KFunction0

class CommunityPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = pageFactory.size

    val titles = mutableListOf<Titleable>()

    override fun createFragment(position: Int): Fragment {
        val f = pageFactory[position]()
        titles += f
        return f as Fragment
    }

    companion object {
        private val pageFactory: List<KFunction0<Titleable>> = listOf(
            RecommendFragment::newInstance,
            FollowFragment::newInstance
        )
    }
}