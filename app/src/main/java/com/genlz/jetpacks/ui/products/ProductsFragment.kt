package com.genlz.jetpacks.ui.products

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.core.view.ViewCompat
import androidx.core.view.children
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.memory.MemoryCache
import com.genlz.android.viewbinding.viewBinding
import com.genlz.jetpacks.R
import com.genlz.jetpacks.databinding.CommentListItemBinding
import com.genlz.jetpacks.databinding.FragmentProductsBinding
import com.genlz.jetpacks.databinding.PostsListItemBinding
import com.genlz.jetpacks.databinding.SimpleItemImageViewBinding
import com.genlz.jetpacks.ui.GalleryFragment
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "ProductsFragment"

@AndroidEntryPoint
class ProductsFragment : Fragment(R.layout.fragment_products) {

    private val binding by viewBinding(FragmentProductsBinding::bind)

    private val viewModel by viewModels<ProductsFragmentViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            posts.adapter = PostsAdapter(findNavController(), viewModel.mockResource())
        }

        postponeEnterTransition()
        (view.parent as? ViewGroup)?.doOnPreDraw {
            startPostponedEnterTransition()
        }
    }
}

private class PostsAdapter(
    private val navController: NavController,
    private val uris: List<Uri>,
) : RecyclerView.Adapter<PostsAdapter.PostsViewHolder>() {

    private val popupWindow = PopupWindow(navController.context).apply {
        setBackgroundDrawable(null)
        contentView = View.inflate(
            navController.context,
            R.layout.popup_menu,
            null
        ).apply {
            findViewById<View>(R.id.comment).setOnClickListener {
                doComment()
                dismiss()
            }
            findViewById<View>(R.id.thumb_up).setOnClickListener {
                doThumbUp()
                dismiss()
            }
        }
        isOutsideTouchable = true

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            TransitionInflater.from(navController.context)
                .inflateTransition(android.R.transition.slide_right).let {
                    enterTransition = it
                    exitTransition = it
                }
        }
    }

    inner class PostsViewHolder(
        private val binding: PostsListItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun onBind(navController: NavController, uris: List<Uri>, comments: List<String>) {
            binding.apply {
                postThumbs.adapter = ThumbsAdapter(navController, uris, bindingAdapterPosition)
                postComments.adapter = CommentsAdapter(comments)
                content.text = navController.context.getText(R.string.long_text)
                avatar.setImageResource(R.drawable.ic_twitter)
                action.setOnClickListener {
                    if (popupWindow.isShowing) {
                        popupWindow.dismiss()
                    } else {
                        popupWindow.showAsDropDown(it)
                    }
                }
            }
        }
    }

    private fun doComment() {

    }

    private fun doThumbUp() {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostsViewHolder {
        return PostsViewHolder(
            PostsListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: PostsViewHolder, position: Int) {
        holder.onBind(
            navController,
            uris.shuffled(),
            listOf("好厉害！", "666", "aaa", "bbb", "牛逼", "屌爆了").shuffled()
        )
    }

    override fun getItemCount(): Int = uris.size
}

private class ThumbsAdapter(
    private val navController: NavController,
    private val uris: List<Uri>,
    private val position: Int //position in posts list
) : RecyclerView.Adapter<ThumbsAdapter.ThumbViewHolder>() {

    private val keys = Array(itemCount) {
        MemoryCache.Key("thumbnail_${position}_$it")
    }

    class ThumbViewHolder(
        private val binding: SimpleItemImageViewBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun onBind(
            navController: NavController,
            uri: Uri,
            keys: Array<MemoryCache.Key>,
            position: Int
        ) {
            ViewCompat.setTransitionName(
                binding.root,
                binding.root.context.getString(
                    R.string.image_thumbnail,
                    position
                ) + "_$bindingAdapterPosition"
            )

            binding.root.load(uri) {
                memoryCacheKey(keys[bindingAdapterPosition])
                listener { _, _ ->
                    binding.root.setOnClickListener {
                        GalleryFragment.navigate(
                            navController,
                            (it.parent as ViewGroup).children.toList(),
                            bindingAdapterPosition,
                            keys
                        )
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThumbViewHolder {
        return ThumbViewHolder(
            SimpleItemImageViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ThumbViewHolder, position: Int) {
        holder.onBind(navController, uris[position], keys, this.position)
    }

    override fun getItemCount() = uris.size
}

private class CommentsAdapter(private val comments: List<String>) :
    RecyclerView.Adapter<CommentsAdapter.CommentViewHolder>() {

    class CommentViewHolder(
        val binding: CommentListItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val comment = binding.root

        fun onBind(comment: String) {
            this.comment.text = comment
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        return CommentViewHolder(
            CommentListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.onBind(comments[position])
    }

    override fun getItemCount() = comments.size
}