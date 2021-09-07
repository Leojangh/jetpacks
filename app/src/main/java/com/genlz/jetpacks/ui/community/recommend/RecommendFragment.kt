package com.genlz.jetpacks.ui.community.recommend

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.genlz.android.viewbinding.viewBinding
import com.genlz.jetpacks.R
import com.genlz.jetpacks.databinding.FragmentRecommendBinding
import com.genlz.jetpacks.ui.community.Titleable

class RecommendFragment(
    override val titleStringResId: Int = R.string.recommend,
) : Fragment(R.layout.fragment_recommend), Titleable {

    private val binding by viewBinding(FragmentRecommendBinding::bind)

    companion object {
        fun newInstance(): RecommendFragment {
            val args = Bundle()

            val fragment = RecommendFragment()
            fragment.arguments = args
            return fragment
        }
    }
}