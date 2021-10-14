package com.genlz.android.gallery

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.view.*
import android.widget.Checkable
import android.widget.ImageView
import android.widget.RadioButton
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.core.view.*
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import coil.imageLoader
import coil.load
import coil.memory.MemoryCache
import com.genlz.android.gallery.databinding.ActivityGalleryBinding
import com.github.chrisbanes.photoview.PhotoView

class GalleryActivity : AppCompatActivity() {

    private val binding by lazy { ActivityGalleryBinding.inflate(layoutInflater) }

    @Suppress("UNCHECKED_CAST")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar?.hide()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT

        val cacheKeys = (intent.getParcelableArrayExtra(KEY) as Array<*>).map {
            it as MemoryCache.Key
        }.toTypedArray()
        val position = intent.getIntExtra(POSITION, 0)

        repeat(cacheKeys.size) {
            layoutInflater.inflate(
                R.layout.simple_radio_button,
                binding.pagerIndicator,
                true
            )
        }

        postponeEnterTransition()
        binding.root.doOnPreDraw {
            startPostponedEnterTransition()
        }

        binding.imagePager.apply {
            adapter = PagerAdapter(cacheKeys)
//            offscreenPageLimit = cacheKeys.size
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    (binding.pagerIndicator[position] as Checkable).isChecked = true
                }
            })
            setCurrentItem(position, false)
        }
    }

    private val Int.dp
        get() = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            toFloat(),
            resources.displayMetrics
        ).toInt()

    companion object {
        fun navigate(
            activity: Activity,
            views: List<View>,
            keys: Array<MemoryCache.Key>,
            position: Int
        ) {
            val intent = Intent(activity, GalleryActivity::class.java).apply {
                putExtra(KEY, keys)
                putExtra(POSITION, position)
            }
            val pairs = views.mapIndexed { index, view ->
                ViewCompat.setTransitionName(view, "item_image_$index")
                Pair(view, "hero_image_$index")
            }.toTypedArray()
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                activity,
                *pairs
            )
            activity.startActivity(intent, options.toBundle())
        }

        private const val KEY = "cacheKeys"
        private const val POSITION = "position"
        private const val TAG = "GalleryActivity"
    }
}

private class PagerAdapter(
    private val keys: Array<MemoryCache.Key>
) : RecyclerView.Adapter<PagerAdapter.ImageHolder>() {

    class ImageHolder(private val v: ImageView) : RecyclerView.ViewHolder(v) {
        init {
            v.setOnClickListener { (it.context as Activity).onBackPressed() }
        }

        fun onBind(key: MemoryCache.Key) {
            ViewCompat.setTransitionName(v, "hero_image_$adapterPosition")
            val bitmap = v.context.imageLoader.memoryCache[key]
            v.load(bitmap)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        val v = PhotoView(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        return ImageHolder(v)
    }

    override fun onBindViewHolder(holder: ImageHolder, position: Int) {
        holder.onBind(keys[position])
    }

    override fun getItemCount() = keys.size
}
