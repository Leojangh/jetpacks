package com.genlz.jetpacks.ui

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionInflater
import androidx.viewpager2.widget.ViewPager2
import coil.load
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
        val move =
            TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.move)
        sharedElementEnterTransition = move
        sharedElementReturnTransition = move
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val uris = args.imageUris
        binding.imagePager.apply {
            adapter = ImagesAdapter(uris)
            offscreenPageLimit = uris.size
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    val rb = binding.pagerIndicator[position] as android.widget.Checkable
                    rb.isChecked = true
                }
            })
        }

        repeat(uris.size) {
            LayoutInflater.from(context).inflate(
                R.layout.simple_radio_button,
                binding.pagerIndicator,
                true
            )
        }
    }


    companion object {

        private const val IMAGE_URIS = "image_uris"

        /**
         * For no navigation support.
         */
        fun newInstance(uris: Array<Uri>) = GalleryFragment().apply {
            arguments = bundleOf(
                IMAGE_URIS to uris
            )
        }

        /**
         * For navigation support
         */
        fun navigate(navController: NavController, view: View, imageUris: Array<Uri>) {
            navController.navigate(
                GalleryDirections.gallery(imageUris),
                FragmentNavigatorExtras(
                    view to navController.context.getString(R.string.image_origin)
                )
            )
        }

        fun localResUri(resource: Int): Uri =
            Uri.parse("android.resource://${App.INSTANCE.packageName}/$resource")
    }
}

private class ImagesAdapter(
    private val uris: Array<Uri>
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

    override fun getItemCount(): Int = uris.size

    inner class ImagesViewHolder(
        binding: SimplePagerItemImageBinding
    ) : RecyclerView.ViewHolder(binding.simpleImage) {

        private val img = binding.simpleImage

        fun onBind(position: Int) {
            img.load(uris[position])
        }
    }
}

private const val TAG = "GalleryFragment"
