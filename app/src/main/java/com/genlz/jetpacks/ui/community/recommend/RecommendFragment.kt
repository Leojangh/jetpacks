package com.genlz.jetpacks.ui.community.recommend

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.genlz.android.viewbinding.viewBinding
import com.genlz.jetpacks.R
import com.genlz.jetpacks.databinding.FragmentRecommendBinding
import com.genlz.jetpacks.databinding.PostItemBinding
import com.genlz.jetpacks.pojo.Post
import com.genlz.jetpacks.ui.community.Titleable
import com.genlz.jetpacks.ui.community.Titleable.Companion.TITLE_RES_KEY
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RecommendFragment : Fragment(R.layout.fragment_recommend), Titleable {

    private val binding by viewBinding(FragmentRecommendBinding::bind)

    private val viewModel by viewModels<RecommendFragmentViewModel>()

    private var loadingJob: Job? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Memory leak caused by reference to the adapter.
        binding.postList.adapter = RecommendAdapter().apply {
            loadingJob?.cancel()
            loadingJob = viewLifecycleOwner.lifecycleScope.launch {
                viewModel.loadRecommend().collectLatest {
                    submitData(it)
                }
            }
        }
    }

    companion object {
        fun newInstance(
            @StringRes
            titleStringResId: Int = R.string.recommend
        ) = RecommendFragment().apply {
            arguments = bundleOf(TITLE_RES_KEY to titleStringResId)
        }
    }

    override val titleStringResId: Int
        get() = arguments?.getInt(TITLE_RES_KEY)
            ?: error("Not title resource passed in as parameter")
}

private class RecommendAdapter :
    PagingDataAdapter<Post, RecommendAdapter.PostViewHolder>(PostDiffCallback()) {

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        getItem(position)?.let(holder::bind)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        PostViewHolder(
            PostItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    class PostViewHolder(
        private val binding: PostItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Post) {
            binding.apply {
                post = item
                executePendingBindings()
            }
        }
    }
}

private class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }
}