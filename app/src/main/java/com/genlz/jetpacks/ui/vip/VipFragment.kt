package com.genlz.jetpacks.ui.vip

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.genlz.android.viewbinding.viewBinding
import com.genlz.jetpacks.R
import com.genlz.jetpacks.databinding.FragmentVipBinding
import com.genlz.jetpacks.ui.common.FabSetter.Companion.findFabSetter
import com.genlz.jetpacks.ui.gallery.GalleryFragment
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.snackbar.Snackbar

class VipFragment : Fragment(R.layout.fragment_vip) {

    private val binding by viewBinding(FragmentVipBinding::bind)

    private val viewModel by viewModels<VipFragmentViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val uris = listOf(
            GalleryFragment.localResUri(R.mipmap.unsplash1).toString(),
            GalleryFragment.localResUri(R.mipmap.unsplash2).toString(),
            GalleryFragment.localResUri(R.mipmap.unsplash3).toString(),
            GalleryFragment.localResUri(R.mipmap.unsplash4).toString(),
            GalleryFragment.localResUri(R.mipmap.unsplash5).toString(),
        )
        binding.banner.setAdapter(BannerAdapter(uris))

        findFabSetter()?.setupFab {
            show()
            setImageResource(R.drawable.ic_baseline_verified_24)
            setOnClickListener {
                Snackbar.make(it, "$it", Snackbar.LENGTH_SHORT).apply {
                }.show()
            }
        }
    }

    companion object {
        private const val TAG = "VipFragment"
    }
}

class BannerAdapter(
    private val uris: List<String>
) : RecyclerView.Adapter<BannerAdapter.BannerViewHolder>() {

    class BannerViewHolder(v: View) : RecyclerView.ViewHolder(v)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        return BannerViewHolder(ShapeableImageView(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            scaleType = ImageView.ScaleType.CENTER_CROP
        })
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        with(holder.itemView as ImageView) {
            load(uris[position])
        }
    }

    override fun getItemCount() = uris.size
}