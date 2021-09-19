package com.genlz.jetpacks.ui.community.follow

import android.os.Bundle
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.genlz.android.viewbinding.viewBinding
import com.genlz.jetpacks.R
import com.genlz.jetpacks.databinding.FragmentFollowBinding
import com.genlz.jetpacks.ui.community.Titleable

class FollowFragment(
    @StringRes
    override val titleStringResId: Int,
) : Fragment(R.layout.fragment_follow), Titleable {

    private val binding by viewBinding<FragmentFollowBinding>()

    companion object {
        fun newInstance(
            @StringRes
            titleStringResId: Int = R.string.follow
        ): FollowFragment {
            val args = Bundle()
            val fragment = FollowFragment(titleStringResId)
            fragment.arguments = args
            return fragment
        }
    }
}