package com.genlz.jetpacks.ui.products

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import coil.memory.MemoryCache
import com.genlz.android.viewbinding.viewBinding
import com.genlz.jetpacks.R
import com.genlz.jetpacks.databinding.FragmentProductsBinding
import com.genlz.jetpacks.datasource.SplashDataSource
import com.genlz.jetpacks.ui.GalleryFragment
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
//            val randomSplash = splashDataSource.getRandomSplash()
            img.load(R.mipmap.pawel_unsplash) {
                memoryCacheKey(MemoryCache.Key("thumbnail"))
                listener { _, metadata ->
                    val memoryCacheKey = metadata.memoryCacheKey
                    img.setOnClickListener {
                        val keys = arrayOf(memoryCacheKey!!)
                        GalleryFragment.navigate(findNavController(), it, 0, keys)
                        //TODO fix shared element transition.
//                        GalleryFragment.navigate(requireActivity(), it, 0, keys)
                    }
                }
            }
        }
    }
}