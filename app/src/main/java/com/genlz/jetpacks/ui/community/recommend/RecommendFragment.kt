package com.genlz.jetpacks.ui.community.recommend

import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.genlz.android.viewbinding.viewBinding
import com.genlz.jetpacks.R
import com.genlz.jetpacks.adapter.RecommendAdapter
import com.genlz.jetpacks.databinding.FragmentRecommendBinding
import com.genlz.jetpacks.ui.community.Titleable
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RecommendFragment(
    @StringRes
    override val titleStringResId: Int,
) : Fragment(R.layout.fragment_recommend), Titleable {

    private val binding by viewBinding(FragmentRecommendBinding::bind)

    private val viewModel by viewModels<RecommendFragmentViewModel>()

    private val adapter = RecommendAdapter()

    private var loadingJob: Job? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerView.adapter = adapter
        loadRecommendData()
    }

    private fun loadRecommendData() {
        loadingJob?.cancel()
        loadingJob = lifecycleScope.launch {
            viewModel.loadRecommend().collectLatest {
                adapter.submitData(it)
            }
        }
    }

    companion object {
        fun newInstance(
            @StringRes
            titleStringResId: Int = R.string.recommend
        ): RecommendFragment {
            val args = Bundle()

            val fragment = RecommendFragment(titleStringResId)
            fragment.arguments = args
            return fragment
        }
    }
}