package com.genlz.jetpacks.ui.community.recommend.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.genlz.jetpacks.data.Post
import com.genlz.jetpacks.databinding.PostItemBinding

class RecommendAdapter :
    PagingDataAdapter<Post, RecommendAdapter.PostViewHolder>(PostDiffCallback()) {

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        getItem(position)?.let(holder::bind)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        return PostViewHolder(
            PostItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    class PostViewHolder(
        private val binding: PostItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding
        }

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