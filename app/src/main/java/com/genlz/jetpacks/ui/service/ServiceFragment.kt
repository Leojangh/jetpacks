package com.genlz.jetpacks.ui.service

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.genlz.android.viewbinding.viewBinding
import com.genlz.jetpacks.R
import com.genlz.jetpacks.databinding.FragmentServiceBinding
import com.google.android.material.internal.CollapsingTextHelper

class ServiceFragment : Fragment(R.layout.fragment_service) {

    private val binding by viewBinding(FragmentServiceBinding::bind)

    private val viewModel by viewModels<ServiceFragmentViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cth = CollapsingTextHelper(binding.textView)



    }

    companion object {
        private const val TAG = "ServiceFragment"
    }
}