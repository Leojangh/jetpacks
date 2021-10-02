package com.genlz.jetpacks.ui.products

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import coil.memory.MemoryCache
import coil.metadata
import com.genlz.android.viewbinding.viewBinding
import com.genlz.jetpacks.R
import com.genlz.jetpacks.databinding.FragmentProductsBinding
import com.genlz.jetpacks.datasource.SplashDataSource
import com.genlz.jetpacks.datasource.SplashDataSource.Companion.SPLASH_RANDOM_URL
import com.genlz.jetpacks.ui.GalleryFragment
import com.genlz.jetpacks.ui.GalleryFragment.Companion.localResUri
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "ProductsFragment"

@AndroidEntryPoint
class ProductsFragment : Fragment(R.layout.fragment_products) {

    private val binding by viewBinding(FragmentProductsBinding::bind)

    private val viewModel by viewModels<ProductsFragmentViewModel>()

    @Inject
    internal lateinit var splashDataSource: SplashDataSource

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            val img = binding.img
            val randomSplash = splashDataSource.getRandomSplash()
            img.load(randomSplash) {
                memoryCacheKey(MemoryCache.Key("thumbnail"))
                listener { _, metadata ->
                    val memoryCacheKey = metadata.memoryCacheKey
                    img.setOnClickListener {
                        GalleryFragment.navigate(findNavController(), it, arrayOf(memoryCacheKey!!))
                    }
                }
            }
        }
    }
}