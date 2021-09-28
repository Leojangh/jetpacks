package com.genlz.jetpacks.ui.products

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.genlz.android.viewbinding.viewBinding
import com.genlz.jetpacks.GalleryDirections
import com.genlz.jetpacks.R
import com.genlz.jetpacks.databinding.FragmentProductsBinding

private const val TAG = "ProductsFragment"

class ProductsFragment : Fragment(R.layout.fragment_products) {

    private val binding by viewBinding(FragmentProductsBinding::bind)

    private val viewModel by viewModels<ProductsFragmentViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = findNavController()

        binding.img.setOnClickListener {
            val imageUris = arrayOf("https://source.unsplash.com/random".toUri())
            Log.d(TAG, "onViewCreated: ${GalleryDirections.gallery(imageUris)}")
            navController.navigate(
                GalleryDirections.gallery(imageUris),
                FragmentNavigatorExtras(
                    it to getString(R.string.image_origin)
                )
            )
        }
    }
}