package com.genlz.jetpacks.ui.gallery

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityOptionsCompat
import androidx.core.os.bundleOf
import androidx.core.util.Pair
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsCompat.Type
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.commit
import coil.memory.MemoryCache
import com.genlz.jetpacks.ui.common.FullscreenController
import com.genlz.share.util.appcompat.*
import com.genlz.share.util.postponeEnterTransitionUtilDraw

/**
 * A container Activity for [GalleryFragment].
 */
class GalleryActivity : AppCompatActivity(), FullscreenController {

    private val windowInsetsController by lazyNoneSafe {
        window.decorView.windowInsetsControllerExt ?: error("Not attached to a window yet!")
    }

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

        postponeEnterTransitionUtilDraw()
    }

    override fun enterFullscreen(sticky: Boolean) {
        windowInsetsController.hide(Type.ime() or Type.systemBars())
        windowInsetsController.systemBarsBehavior =
            if (sticky) WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            else WindowInsetsControllerCompat.BEHAVIOR_SHOW_BARS_BY_SWIPE
    }

    override fun exitFullscreen() {
        windowInsetsController.show(Type.systemBars())
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