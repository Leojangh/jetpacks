package com.genlz.jetpacks.ui

import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.genlz.android.viewbinding.viewBinding
import com.genlz.jetpacks.R
import com.genlz.jetpacks.databinding.FragmentFirstBinding
import com.genlz.jetpacks.utility.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
@AndroidEntryPoint
class FirstFragment : Fragment() {

    private val viewModel by viewModels<FirstFragmentViewModel>()
    private val binding by viewBinding<FragmentFirstBinding>()
    private val navController by lazy { findNavController() }

    @Inject
    internal lateinit var connectivityManager: ConnectivityManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonFirst.setOnClickListener {
            val action = FirstFragmentDirections.actionFirstFragmentToSecondFragment()
            navController.navigate(action)
        }

        binding.showDialog.setOnClickListener {
            navController.navigate(R.id.FirstFragmentDialog, bundleOf("title" to "Hello"))
        }

        binding.thumbUp.setOnClickListener {
            viewModel.thumbUp()
        }
        binding.thumbDown.setOnClickListener {
            viewModel.thumbDown()
        }

        viewModel.st.launchAndCollectIn(viewLifecycleOwner) {
            binding.textviewFirst.text = when (it) {
                is Loading -> "拼命加载中。。。"
                is Success -> it.data.toString()
                is Fail -> it.throwable.message
            }
        }
        launchAndRepeatWithViewLifecycle {
            collectData()
        }
    }

    private suspend fun collectData() {
        viewModel.st.collect {
            binding.textviewFirst.text = when (it) {
                is Loading -> "拼命加载中。。。"
                is Success -> it.data.toString()
                is Fail -> it.throwable.message
            }
        }
    }

    @Suppress("UNUSED")
    companion object {
        private const val TAG = "FirstFragment"
    }
}

