package com.genlz.jetpacks.ui.service

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.genlz.android.viewbinding.viewBinding
import com.genlz.jetpacks.R
import com.genlz.jetpacks.databinding.FragmentServiceBinding

class ServiceFragment : Fragment(R.layout.fragment_service) {

    private val binding by viewBinding(FragmentServiceBinding::bind)

    private val viewModel by viewModels<ServiceFragmentViewModel>()

}