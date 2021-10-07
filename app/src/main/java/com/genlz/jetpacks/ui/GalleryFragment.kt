package com.genlz.jetpacks.ui

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.widget.Checkable
import androidx.activity.OnBackPressedCallback
import androidx.annotation.AnyRes
import androidx.annotation.IntDef
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.doOnPreDraw
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.navigation.NavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Transition
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
import com.genlz.jetpacks.utility.appCompatActivity
import java.util.*

class GalleryFragment : Fragment(R.layout.fragment_gallery) {

    /**
     * Support reading passed in args as usual.
     * The name of args must be as same as the definition in the navigation xml which is not support
     * string resource reference,and will be translated to lower camel case.
     */
    private val args by navArgs<GalleryFragmentArgs>()

    private val binding by viewBinding(FragmentGalleryBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val move = TransitionInflater.from(requireContext())
            .inflateTransition(android.R.transition.move).apply {
                duration = resources.getInteger(R.integer.material_motion_duration_long_1).toLong()
            }
        sharedElementEnterTransition = move

        val activity = requireActivity()
        activity.onBackPressedDispatcher.addCallback(this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    when (args.showOptions) {
                        SHOW_OPTIONS_NORMAL -> {
                            //must set false to disable this callback.otherwise stackoverflow.
                            isEnabled = false
                            activity.onBackPressed()
                            return
                        }
                        SHOW_OPTIONS_FULLSCREEN -> activity.supportFragmentManager.popBackStack(
                            TAG,
                            FragmentManager.POP_BACK_STACK_INCLUSIVE
                        )
                        else -> throw IllegalArgumentException("Unknown show options:${args.showOptions}")
                    }
                }
            })
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        if (args.showOptions == SHOW_OPTIONS_NORMAL) {
            controlFullscreen(enter)
        }
        return super.onCreateAnimation(transit, enter, nextAnim)
    }

    //adb shell settings put global transition_duration_scale 20
    private fun controlFullscreen(enter: Boolean) {
        val fullscreenController = activity as? FullscreenController ?: return
        if (enter) {
            //The animation of motion layout is incompatible with shared animation?
            requireView().postDelayed({
                fullscreenController.enterFullscreen()
            }, (sharedElementEnterTransition as Transition).duration)
        } else {
            requireView().postDelayed({
                fullscreenController.exitFullscreen()
            }, (sharedElementEnterTransition as Transition).duration)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val cacheKeys = args.cacheKeys
        repeat(cacheKeys.size) {
            LayoutInflater.from(context).inflate(
                R.layout.simple_radio_button,
                binding.pagerIndicator,
                true
            )
        }
        binding.imagePager.apply {
            adapter = ImagesAdapter(cacheKeys)
            offscreenPageLimit = cacheKeys.size
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    (binding.pagerIndicator[position] as Checkable).isChecked = true
                }
            })
            setCurrentItem(args.initPosition, false)
        }

        // Make sure always as fullscreen state at this fragment scenario.
        if (args.showOptions == SHOW_OPTIONS_NORMAL) {
            controlFullscreen(true)
        }

        // Note: When using a shared element transition from a fragment using a RecyclerView
        // to another fragment, you must still postpone the fragment using a RecyclerView
        // to ensure that the returning shared element transition functions correctly
        // when popping back to the RecyclerView.
        postponeEnterTransition()

        (view.parent as? ViewGroup)?.doOnPreDraw {
            startPostponedEnterTransition()
        }
    }

    companion object {

        const val SHOW_OPTIONS_FULLSCREEN = 1

        const val SHOW_OPTIONS_NORMAL = 0

        /**
         * For navigation support.
         *
         * @param navController
         * @param views The shared element views.
         * @param initPosition The position of the initial clicked point.
         * @param keys The navigation params.
         */
        fun navigate(
            navController: NavController,
            views: List<View>,
            initPosition: Int,
            keys: Array<MemoryCache.Key>
        ) {
            val sharedElements = views.mapIndexed { index, view ->
                view to "hero_image_$index"
            }.toTypedArray()

            val extras = FragmentNavigatorExtras(*sharedElements)
            navController.navigate(
                GalleryDirections.gallery(keys, initPosition),
                extras
            )
        }

        /**
         * Modal background,but have no shared elements transition?
         */
        fun navigate(
            activity: FragmentActivity,
            views: List<View>,
            initPosition: Int,
            keys: Array<MemoryCache.Key>
        ) {
            activity.supportFragmentManager.commit {
                setReorderingAllowed(true)
                views.forEachIndexed { index, view ->
                    ViewCompat.setTransitionName(view, "item_image_$index")
                    addSharedElement(view, "hero_image_$index")
                }
                val fragment = GalleryFragment().apply {
                    arguments = bundleOf(
                        "cacheKeys" to keys,
                        "showOptions" to SHOW_OPTIONS_FULLSCREEN,
                        "initPosition" to initPosition
                    )
                }
                replace(android.R.id.content, fragment, TAG)
                addToBackStack(TAG)
            }
        }

        fun localResUri(@AnyRes resource: Int): Uri =
            Uri.parse("android.resource://${App.INSTANCE.packageName}/$resource")
    }
}

private class ImagesAdapter(
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
        holder.onBind(keys[position])
    }

    override fun getItemCount(): Int = keys.size

    class ImagesViewHolder(
        private val binding: SimplePagerItemImageBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                it.context.appCompatActivity?.onBackPressed()
            }
        }

        fun onBind(key: MemoryCache.Key) {
            // Transition name must unique,so cannot set in XML.
            // Another point to consider when using shared element transitions with a RecyclerView
            // is that you cannot set the transition name in the RecyclerView item's XML layout
            // because an arbitrary number of items share that layout. A unique transition name
            // must be assigned so that the transition animation uses the correct view.
            ViewCompat.setTransitionName(binding.simpleImage, "hero_image_$bindingAdapterPosition")

            val bitmap = binding.root.context.imageLoader.memoryCache[key]
            binding.simpleImage.load(bitmap)
        }
    }
}

interface FullscreenController {

    fun enterFullscreen()

    fun exitFullscreen()
}

private const val TAG = "GalleryFragment"
