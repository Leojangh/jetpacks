package com.genlz.jetpacks.ui.gallery

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityOptionsCompat
import androidx.core.os.bundleOf
import androidx.core.util.Pair
import androidx.core.view.ViewCompat
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.commit
import coil.memory.MemoryCache
import com.genlz.share.util.appcompat.intent
import com.genlz.share.util.appcompat.setDecorFitsSystemWindowsExt
import com.genlz.share.util.appcompat.transitionNameExt

/**
 * A container Activity for [GalleryFragment].
 */
class GalleryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val fragmentContainerView = FragmentContainerView(this).apply {
            id = ViewCompat.generateViewId()
        }
        setContentView(fragmentContainerView)
        window.setDecorFitsSystemWindowsExt(false)
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace(
                    fragmentContainerView.id,
                    GalleryFragment::class.java,
                    intent.extras,
                    TAG
                )
            }
        }

        postponeEnterTransition()
        window.decorView.doOnPreDraw {
            startPostponedEnterTransition()
        }
    }

    companion object {

        private const val TAG = "GalleryActivity"

        fun navigate(
            activity: Activity,
            views: List<View>,
            thumbnailsAndOriginUrls: Map<MemoryCache.Key, String>,
            position: Int = 0
        ) {
            val sharedElements = views.mapIndexed { index, view ->
                if (view.transitionNameExt == null) view.transitionNameExt = "item_image_$index"
                Pair(view, "hero_image_$index")
            }.toTypedArray()
            val options =
                ActivityOptionsCompat.makeSceneTransitionAnimation(activity, *sharedElements)
            val intent = activity.intent<GalleryActivity>(
                bundleOf(
                    "cacheKeys" to thumbnailsAndOriginUrls.keys.toTypedArray(),
                    "imageUris" to thumbnailsAndOriginUrls.values.toTypedArray(),
                    "initPosition" to position
                )
            )
            activity.startActivity(intent, options.toBundle())
        }
    }
}