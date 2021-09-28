package com.genlz.jetpacks.ui.products

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.genlz.android.viewbinding.viewBinding
import com.genlz.jetpacks.R
import com.genlz.jetpacks.databinding.FragmentProductsBinding
import com.genlz.jetpacks.di.RetrofitModule
import com.genlz.jetpacks.ui.GalleryFragment

private const val TAG = "ProductsFragment"

class ProductsFragment : Fragment(R.layout.fragment_products) {

    private val binding by viewBinding(FragmentProductsBinding::bind)

    private val viewModel by viewModels<ProductsFragmentViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.img.load(RetrofitModule.SPLASH_BASE_URL + "random")
        binding.img.setOnClickListener {

            val imageUris = arrayOf(
                "https://source.unsplash.com/random".toUri(),
                localResUri(R.mipmap.pawel_unsplash)
            )
            GalleryFragment.navigate(findNavController(), it, imageUris)
        }
    }

    private fun localResUri(resource: Int) =
        Uri.parse("android.resource://${requireContext().packageName}/$resource")
}