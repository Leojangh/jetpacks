package com.genlz.jetpacks.ui.vip

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.genlz.android.viewbinding.viewBinding
import com.genlz.jetpacks.R
import com.genlz.jetpacks.databinding.FragmentVipBinding
import com.genlz.jetpacks.ui.GalleryFragment
import com.genlz.jetpacks.ui.ToolbarCustomizer
import com.google.android.material.imageview.ShapeableImageView

class VipFragment : Fragment(R.layout.fragment_vip) {

    private val binding by viewBinding(FragmentVipBinding::bind)

    private val viewModel by viewModels<VipFragmentViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as? ToolbarCustomizer)?.custom {
            Log.d(TAG, "onViewCreated: ${findViewById<View>(R.id.avatar)}")
        }

        val uris = listOf(
            GalleryFragment.localResUri(R.mipmap.unsplash1).toString(),
            GalleryFragment.localResUri(R.mipmap.unsplash2).toString(),
        )
        binding.banner.setAdapter(BannerAdapter(uris))
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