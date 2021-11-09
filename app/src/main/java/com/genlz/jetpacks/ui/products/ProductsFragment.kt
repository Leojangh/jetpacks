package com.genlz.jetpacks.ui.products

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
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
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.memory.MemoryCache
import coil.size.ViewSizeResolver
import com.genlz.android.viewbinding.viewBinding
import com.genlz.jetpacks.R
import com.genlz.jetpacks.databinding.CommentListItemBinding
import com.genlz.jetpacks.databinding.FragmentProductsBinding
import com.genlz.jetpacks.databinding.PostsListItemBinding
import com.genlz.jetpacks.databinding.SimpleItemImageViewBinding
import com.genlz.jetpacks.pojo.Post
import com.genlz.jetpacks.ui.GalleryFragment
import com.genlz.jetpacks.ui.common.ReSelectable
import com.genlz.share.util.appcompat.appCompatActivity
import com.genlz.share.util.launchAndCollectIn
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "ProductsFragment"

@AndroidEntryPoint
class ProductsFragment : Fragment(R.layout.fragment_products), ReSelectable {

    private val binding by viewBinding(FragmentProductsBinding::bind)

    private val viewModel by viewModels<ProductsFragmentViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = PostAdapter(findNavController())
        binding.posts.adapter = adapter
        viewModel.posts.launchAndCollectIn(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        postponeEnterTransition()
        (view.parent as? ViewGroup)?.doOnPreDraw {
            startPostponedEnterTransition()
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        //Handle configuration change manually: refresh layout.
        binding.posts.adapter?.apply {
            notifyItemRangeChanged(0, itemCount)
        }
    }

    override fun onReselect() {
        Log.d(TAG, "onReselect: ")
    }
}

private class PostAdapter(
    private val navController: NavController
) : ListAdapter<Post, PostAdapter.PostViewHolder>(DiffCallback()) {

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

    private fun doThumbUp() {

    }

    private fun doComment() {

    }

    inner class PostViewHolder(
        private val binding: PostsListItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun onBind(post: Post) {
            binding.apply {
                postThumbs.adapter = ThumbsAdapter(
                    navController,
                    post.thumbnails,
                    bindingAdapterPosition
                )
                postComments.adapter =
                    CommentsAdapter(listOf("好厉害！", "666", "aaa", "bbb", "牛逼", "屌爆了"))
                content.text = post.abstraction
                avatar.load(post.user.avatar) {
                    placeholder(R.drawable.ic_baseline_account_circle_24)
                }
                username.text = post.user.username
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        return PostViewHolder(
            PostsListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }

    private class DiffCallback : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Post, newItem: Post) =
            oldItem == newItem
    }
}

private class ThumbsAdapter(
    private val navController: NavController,
    private val uris: List<String>,
    private val id: Int
) : RecyclerView.Adapter<ThumbsAdapter.ThumbViewHolder>() {

    private val keys = Array(itemCount) {
        MemoryCache.Key("${id}_$it")
    }

    class ThumbViewHolder(
        val binding: SimpleItemImageViewBinding
    ) : RecyclerView.ViewHolder(binding.root)

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

        val img = holder.binding.postThumbnail

        // 如果是使用Fragment打开，那必须在这里设置，而不是像Activity那样在另一端设置都可以，
        // 这是因为使用Fragment打开会导致原有的fragment被销毁，在返回原fragment时需要重新bind，
        // 这也就是为什么需要在此处设置Transition name的原因；使用Activity打开就不会有这个问题，
        // 因为在新的Activity中打开不会销毁原来Activity中的fragment,自然也不会重新bind。
        ViewCompat.setTransitionName(img, "${id}_$position")
        img.load(uris[position]) {

            size(ViewSizeResolver(img))
            allowHardware(false)
            memoryCacheKey(keys[position])
            listener { _, _ ->
                img.setOnClickListener {
                    goGallery(it, position)
                }
            }
        }
    }

    private fun goGallery(
        img: View,
        position: Int
    ) {
        val views = (img.parent as ViewGroup).children.toList()
        val thumbsAndOrigin = keys.zip(uris).toMap()
//        GalleryFragment.navigate(
//            navController,
//            views,
//            position,
//            thumbsAndOrigin
//        )
//        GalleryFragment.navigate(img.context.appCompatActivity!!)(views)(thumbsAndOrigin, position)
        GalleryFragment.navigate(navController)(views)(thumbsAndOrigin, position)
//        GalleryActivity.navigate(
//            img.context.appCompatActivity!!,
//            views,
//            position,
//            thumbsAndOrigin
//        )
    }

    override fun getItemCount() = uris.size
}

private class CommentsAdapter(private val comments: List<String>) :
    RecyclerView.Adapter<CommentsAdapter.CommentViewHolder>() {

    class CommentViewHolder(
        val binding: CommentListItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun onBind(comment: String) {
            binding.root.text = comment
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