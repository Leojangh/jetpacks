package com.genlz.jetpacks.ui.community

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.genlz.android.viewbinding.viewBinding
import com.genlz.jetpacks.R
import com.genlz.jetpacks.databinding.FragmentCommunityBinding

class CommunityFragment : Fragment(R.layout.fragment_community) {

    private val binding by viewBinding<FragmentCommunityBinding>()

    private val viewModel by viewModels<CommunityFragmentViewModel>()

}