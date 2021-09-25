package com.genlz.jetpacks.ui.community.follow

import androidx.annotation.StringRes
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.genlz.android.viewbinding.viewBinding
import com.genlz.jetpacks.R
import com.genlz.jetpacks.databinding.FragmentFollowBinding
import com.genlz.jetpacks.ui.community.Titleable
import com.genlz.jetpacks.ui.community.Titleable.Companion.TITLE_RES_KEY

class FollowFragment : Fragment(R.layout.fragment_follow), Titleable {

    private val binding by viewBinding<FragmentFollowBinding>()

    companion object {
        fun newInstance(
            @StringRes
            titleStringResId: Int = R.string.follow
        ) = FollowFragment().apply {
            arguments = bundleOf(TITLE_RES_KEY to titleStringResId)
        }
    }

    override val titleStringResId: Int
        get() = arguments?.getInt(TITLE_RES_KEY) ?: error("Not title resource passed in as parameter")
}