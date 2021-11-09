package com.genlz.jetpacks.ui

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.widget.Checkable
import android.widget.ImageView
import androidx.activity.addCallback
import androidx.annotation.AnyRes
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
import androidx.transition.TransitionInflater
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import coil.imageLoader
import coil.load
import coil.memory.MemoryCache
import coil.request.ImageRequest
import com.genlz.android.viewbinding.viewBinding
import com.genlz.jetpacks.App
import com.genlz.jetpacks.GalleryDirections
import com.genlz.jetpacks.R
import com.genlz.jetpacks.databinding.FragmentGalleryBinding
import com.genlz.jetpacks.ui.common.FullscreenController.Companion.findFullscreenController
import com.genlz.share.util.appcompat.appCompatActivity
import com.genlz.share.util.appcompat.transitionNameExt

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
        sharedElementEnterTransition = TransitionInflater.from(requireContext())
            .inflateTransition(android.R.transition.move)

        val activity = requireActivity()
        activity.onBackPressedDispatcher.addCallback(this) {
            when (args.showOptions) {
                SHOW_OPTIONS_NORMAL -> {
                    //must set false to disable this callback.otherwise stackoverflow.
                    isEnabled = false
                    activity.onBackPressed()
                }
                SHOW_OPTIONS_FULLSCREEN -> activity.supportFragmentManager.popBackStack(
                    TAG,
                    FragmentManager.POP_BACK_STACK_INCLUSIVE
                )
            }
        }
        preload(requireContext())
    }

    private fun preload(context: Context) {
        args.imageUris.forEach {
            val request = ImageRequest.Builder(context).apply {
                data(it)
//                size(ViewSizeResolver(img))
            }.build()
            context.imageLoader.enqueue(request)
        }
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        if (args.showOptions == SHOW_OPTIONS_NORMAL) {
            //adb shell settings put global transition_duration_scale 20
            findFullscreenController()?.apply {
                if (enter) {
                    enterFullscreen()
                } else {
                    exitFullscreen()
                }
            }
        }
        return super.onCreateAnimation(transit, enter, nextAnim)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val cacheKeys = args.cacheKeys
        val imageUris = args.imageUris
        repeat(cacheKeys.size) {
            LayoutInflater.from(context).inflate(
                R.layout.simple_radio_button,
                binding.pagerIndicator,
                true
            )
        }
        binding.imagePager.apply {
            adapter = PagerAdapter(cacheKeys, imageUris, this@GalleryFragment)
//            offscreenPageLimit = cacheKeys.size
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    (binding.pagerIndicator[position] as Checkable).isChecked = true
                }
            })
            setCurrentItem(args.initPosition, false)
        }

        // Make sure always as fullscreen state at this fragment scenario.
        if (args.showOptions == SHOW_OPTIONS_NORMAL) {
            //adb shell settings put global transition_duration_scale 20
            findFullscreenController()?.enterFullscreen()
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
         * Modal background,but have no shared elements transition?
         */
        fun navigate(activity: FragmentActivity): (List<View>) -> (Map<MemoryCache.Key, String>, Int) -> Unit {
            return { views ->
                { thumbnailAndOrigin, initPosition ->
                    val fragment = GalleryFragment().apply {
                        arguments = bundleOf(
                            "cacheKeys" to thumbnailAndOrigin.keys.toTypedArray(),
                            "showOptions" to SHOW_OPTIONS_FULLSCREEN,
                            "initPosition" to initPosition,
                            "imageUris" to thumbnailAndOrigin.values.toTypedArray()
                        )
                    }
                    activity.supportFragmentManager.commit {
                        setReorderingAllowed(true)
                        views.forEachIndexed { index, view ->
                            if (view.transitionNameExt == null) view.transitionNameExt =
                                "item_image_$index"
                            addSharedElement(view, "hero_image_$index")
                        }
                        replace(android.R.id.content, fragment)
                    }
                }
            }
        }

        /**
         * The currying version of [navigate]
         */
        fun navigate(navController: NavController): (List<View>) -> (Map<MemoryCache.Key, String>, Int) -> Unit {
            return { views ->
                { thumbnailAndOrigin, initPosition ->
                    val sharedElements = views.mapIndexed { index, view ->
                        if (view.transitionNameExt == null)
                            view.transitionNameExt = "item_image_$index"
                        view to "hero_image_$index"
                    }.toTypedArray()
                    val cacheKeys = thumbnailAndOrigin.keys.toTypedArray()
                    val uris = thumbnailAndOrigin.values.toTypedArray()

                    val extras = FragmentNavigatorExtras(*sharedElements)
                    navController.navigate(
                        GalleryDirections.gallery(
                            cacheKeys = cacheKeys,
                            initPosition = initPosition,
                            imageUris = uris
                        ),
                        extras
                    )
                }
            }
        }

        /**
         * For navigation support.
         *
         * @param navController
         * @param views The shared element views.
         * @param initPosition The position of the initial clicked point.
         * @param thumbnailAndOrigin The key is the cache keys of thumbnails that has been loaded
         * and the value is the original uri that will be loaded.
         */
        fun navigate(
            navController: NavController,
            views: List<View>,
            initPosition: Int,
            thumbnailAndOrigin: Map<MemoryCache.Key, String>
        ) {
            val sharedElements = views.mapIndexed { index, view ->
                if (view.transitionNameExt == null) view.transitionNameExt = "item_image_$index"
                view to "hero_image_$index"
            }.toTypedArray()
            val cacheKeys = thumbnailAndOrigin.keys.toTypedArray()
            val uris = thumbnailAndOrigin.values.toTypedArray()

            val extras = FragmentNavigatorExtras(*sharedElements)
            navController.navigate(
                GalleryDirections.gallery(
                    cacheKeys = cacheKeys,
                    initPosition = initPosition,
                    imageUris = uris
                ),
                extras
            )
        }

        fun localResUri(@AnyRes resource: Int): Uri =
            Uri.parse("android.resource://${App.INSTANCE.packageName}/$resource")
    }
}

private class PagerAdapter(
    private val keys: Array<MemoryCache.Key>,
    private val imageUris: Array<String>,
    fragment: Fragment
) : FragmentStateAdapter(fragment) {

    override fun getItemCount() = keys.size

    override fun createFragment(position: Int): Fragment {
        return ImageFragment.newInstance(keys[position], imageUris[position], position)
    }

    class ImageFragment : Fragment(R.layout.simple_pager_item_image) {
        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            view.setOnClickListener {
                it.context.appCompatActivity?.onBackPressed()
            }
            val position = requireArguments().getInt(PARAM_POSITION)
            val cacheKey = requireArguments()[PARAM_KEY] as MemoryCache.Key
            val originUri = requireArguments()[PARAM_URI] as String
            val img = view.findViewById<ImageView>(R.id.simple_image)
            // Transition name must unique,so cannot set in XML.
            // Another point to consider when using shared element transitions with a RecyclerView
            // is that you cannot set the transition name in the RecyclerView item's XML layout
            // because an arbitrary number of items share that layout. A unique transition name
            // must be assigned so that the transition animation uses the correct view.
            ViewCompat.setTransitionName(img, "hero_image_$position")
            img.load(originUri) {
                placeholderMemoryCacheKey(cacheKey)
                allowHardware(false)
            }
        }

        companion object {
            const val PARAM_KEY = "key"
            const val PARAM_URI = "uri"
            const val PARAM_POSITION = "position"

            fun newInstance(key: MemoryCache.Key, imageUri: String, position: Int) =
                ImageFragment().apply {
                    arguments = bundleOf(
                        PARAM_KEY to key,
                        PARAM_POSITION to position,
                        PARAM_URI to imageUri
                    )
                }
        }
    }
}

private const val TAG = "GalleryFragment"
