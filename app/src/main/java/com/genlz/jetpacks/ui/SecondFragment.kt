package com.genlz.jetpacks.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.genlz.android.viewbinding.viewBinding
import com.genlz.jetpacks.databinding.FragmentSecondBinding
import com.genlz.jetpacks.utility.Fail
import com.genlz.jetpacks.utility.Loading
import com.genlz.jetpacks.utility.Success
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
@AndroidEntryPoint
class SecondFragment : Fragment() {

    private val viewModel by viewModels<SecondFragmentViewModel>()
    private val binding by viewBinding<FragmentSecondBinding>()
    private val navController by lazy { findNavController() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: $viewModel")

        binding.buttonSecond.setOnClickListener {
            navController.navigateUp()
        }

        binding.textviewSecond.setOnClickListener {
            viewModel.thumbUp()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.t.collect {
                    binding.textviewSecond.text = when (it) {
                        is Loading -> "拼命加载中。。。"
                        is Success -> it.data.toString()
                        is Fail -> it.throwable.message
                    }
                }
            }
        }
    }

    companion object {
        private const val TAG = "SecondFragment"
    }
}