package com.genlz.jetpacks.ui.products

import android.os.Bundle
import android.view.View
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.genlz.android.viewbinding.viewBinding
import com.genlz.jetpacks.R
import com.genlz.jetpacks.SeparateNavDirections
import com.genlz.jetpacks.databinding.FragmentProductsBinding
import com.genlz.jetpacks.ui.ImagesFragment
import com.genlz.jetpacks.ui.ImagesFragmentDirections

private const val TAG = "ProductsFragment"

class ProductsFragment : Fragment(R.layout.fragment_products) {

    private val binding by viewBinding(FragmentProductsBinding::bind)

    private val viewModel by viewModels<ProductsFragmentViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.thumbnail.setOnClickListener {
            val imageUris = arrayOf("https://source.unsplash.com/random".toUri())
            findNavController().navigate(
                SeparateNavDirections.actionGlobalImagesFragment(imageUris),
                FragmentNavigatorExtras(binding.thumbnail to getString(R.string.origin_image))
            )
        }
        binding.textView.setOnClickListener {
        }
    }
}