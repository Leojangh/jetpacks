package com.genlz.jetpacks.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionInflater
import coil.load
import com.genlz.android.viewbinding.viewBinding
import com.genlz.jetpacks.R
import com.genlz.jetpacks.databinding.FragmentImagesBinding
import com.genlz.jetpacks.databinding.SimplePagerItemImageBinding

class ImagesFragment : Fragment(R.layout.fragment_images) {

    private val args by navArgs<ImagesFragmentArgs>()

    private val binding by viewBinding(FragmentImagesBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val transition =
            TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.move)
        sharedElementEnterTransition = transition
        sharedElementReturnTransition = transition
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.imagePager.adapter = ImagesAdapter(args.imageUris)
    }

    companion object {
        /**
         * For no navigation support.
         */
        fun newInstance(uris: Array<Uri>) = ImagesFragment().apply {
            arguments = bundleOf(
                IMAGE_URIS to uris
            )
        }
    }
}

private class ImagesAdapter(
    private val uris: Array<Uri>
) : RecyclerView.Adapter<ImagesAdapter.ImagesViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagesViewHolder {
        val binding = SimplePagerItemImageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ImagesViewHolder(binding)
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


const val IMAGE_URIS = "image_uris"