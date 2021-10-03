package com.genlz.jetpacks.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import androidx.annotation.AnyRes
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionInflater
import androidx.viewpager2.widget.ViewPager2
import coil.imageLoader
import coil.load
import coil.memory.MemoryCache
import com.genlz.android.viewbinding.viewBinding
import com.genlz.jetpacks.App
import com.genlz.jetpacks.GalleryDirections
import com.genlz.jetpacks.R
import com.genlz.jetpacks.databinding.FragmentGalleryBinding
import com.genlz.jetpacks.databinding.SimplePagerItemImageBinding

class GalleryFragment : Fragment(R.layout.fragment_gallery) {

    private val args by navArgs<GalleryFragmentArgs>()

    private val binding by viewBinding(FragmentGalleryBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(requireContext())
            .inflateTransition(android.R.transition.move).apply {
                duration = resources.getInteger(R.integer.material_motion_duration_long_1).toLong()
            }
        postponeEnterTransition()
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        controlFullscreen(enter)
        return super.onCreateAnimation(transit, enter, nextAnim)
    }

    //adb shell settings put global transition_duration_scale 20
    private fun controlFullscreen(enter: Boolean) {
        val fullscreenController = activity as? FullscreenController ?: return
        if (enter) {
            fullscreenController.enterFullscreen()
        } else {
            fullscreenController.exitFullscreen()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val cacheKeys = args.cacheKeys

        binding.imagePager.apply {
            adapter = ImagesAdapter(this@GalleryFragment, cacheKeys)
//            offscreenPageLimit = cacheKeys.size //影响动画
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    val rb = binding.pagerIndicator[position] as android.widget.Checkable
                    rb.isChecked = true
                }
            })
        }
        repeat(cacheKeys.size) {
            LayoutInflater.from(context).inflate(
                R.layout.simple_radio_button,
                binding.pagerIndicator,
                true
            )
        }
    }

    companion object {

        /**
         * For navigation support
         *
         * @param navController
         * @param view The shared element view.
         */
        fun navigate(
            navController: NavController,
            view: View,
            keys: Array<MemoryCache.Key>
        ) {
            val extras = FragmentNavigatorExtras(
                view to navController.context.getString(R.string.image_origin)
            )
            navController.navigate(
                GalleryDirections.gallery(keys),
                extras
            )
        }

        fun localResUri(@AnyRes resource: Int): Uri =
            Uri.parse("android.resource://${App.INSTANCE.packageName}/$resource")
    }
}

private class ImagesAdapter(
    private val fragment: Fragment,
    private val keys: Array<MemoryCache.Key>
) : RecyclerView.Adapter<ImagesAdapter.ImagesViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagesViewHolder {
        return ImagesViewHolder(
            SimplePagerItemImageBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ImagesViewHolder, position: Int) {

        holder.onBind(position)
    }

    override fun getItemCount(): Int = keys.size

    inner class ImagesViewHolder(
        private val binding: SimplePagerItemImageBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                fragment.findNavController().navigateUp()
            }
        }

        fun onBind(position: Int) {
            val bitmap = fragment.requireContext().imageLoader.memoryCache[keys[position]]
            binding.simpleImage.load(bitmap)
            fragment.startPostponedEnterTransition()
        }
    }
}

interface FullscreenController {

    fun enterFullscreen()

    fun exitFullscreen()
}

private const val TAG = "GalleryFragment"
