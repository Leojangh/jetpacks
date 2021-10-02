package com.genlz.jetpacks.ui.community

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.genlz.android.viewbinding.viewBinding
import com.genlz.jetpacks.R
import com.genlz.jetpacks.databinding.FragmentCommunityBinding
import com.genlz.jetpacks.ui.community.follow.FollowFragment
import com.genlz.jetpacks.ui.community.recommend.RecommendFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class CommunityFragment : Fragment(R.layout.fragment_community) {

    private val binding by viewBinding(FragmentCommunityBinding::bind)

    private val viewModel by viewModels<CommunityFragmentViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val pager = binding.contentView.pager
        val tab = binding.contentView.tab

        mediateTabWithPager(pager, tab)
    }

    private fun mediateTabWithPager(
        pager: ViewPager2,
        tabLayout: TabLayout
    ) {
        val adapter: CommunityPagerAdapter
        pager.adapter = CommunityPagerAdapter(
            this,
            listOf(R.string.recommend, R.string.follow)
        ).also { adapter = it }
        TabLayoutMediator(tabLayout, pager) { tab, position ->
            tab.setText(adapter.titles[position])
            tab.orCreateBadge.apply {
                number = 10
            }
            tab.setIcon(R.drawable.pager_tab_selector)
        }.attach()
    }

    companion object {
        private const val TAG = "CommunityFragment"
    }
}

private class CommunityPagerAdapter(
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