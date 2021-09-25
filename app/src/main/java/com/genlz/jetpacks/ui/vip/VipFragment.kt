package com.genlz.jetpacks.ui.vip

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.genlz.android.viewbinding.viewBinding
import com.genlz.jetpacks.R
import com.genlz.jetpacks.databinding.FragmentVipBinding

class VipFragment : Fragment(R.layout.fragment_vip) {

    private val binding by viewBinding(FragmentVipBinding::bind)

    private val viewModel by viewModels<VipFragmentViewModel>()

}