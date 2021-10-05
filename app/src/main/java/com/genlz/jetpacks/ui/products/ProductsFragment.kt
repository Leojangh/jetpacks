package com.genlz.jetpacks.ui.products

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.memory.MemoryCache
import com.genlz.android.viewbinding.viewBinding
import com.genlz.jetpacks.R
import com.genlz.jetpacks.databinding.FragmentProductsBinding
import com.genlz.jetpacks.databinding.ProductsListItemBinding
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            recyclerView.adapter = ProductsAdapter(this@ProductsFragment, mockResource())
        }
    }

    private fun mockResource(): List<Uri> = listOf(
        GalleryFragment.localResUri(R.mipmap.unsplash1),
        GalleryFragment.localResUri(R.mipmap.unsplash2),
        GalleryFragment.localResUri(R.mipmap.unsplash3),
        GalleryFragment.localResUri(R.mipmap.unsplash4),
        GalleryFragment.localResUri(R.mipmap.unsplash5),
        GalleryFragment.localResUri(R.mipmap.unsplash6),
        GalleryFragment.localResUri(R.mipmap.unsplash7),
        GalleryFragment.localResUri(R.mipmap.unsplash8),
        GalleryFragment.localResUri(R.mipmap.unsplash9),
        GalleryFragment.localResUri(R.mipmap.unsplash10),
    ).shuffled()
}

private class ProductsAdapter(
    private val fragment: Fragment,
    private val uris: List<Uri>
) : RecyclerView.Adapter<ProductsAdapter.ProductsViewHolder>() {

    class ProductsViewHolder(val binding: ProductsListItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsViewHolder {
        return ProductsViewHolder(
            ProductsListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ProductsViewHolder, position: Int) {

        val keys = Array(itemCount) {
            MemoryCache.Key("thumbnail$it")
        }
        holder.binding.img.apply {
            load(uris[position]) {
                memoryCacheKey(keys[position])
                listener { _, _ ->
                    setOnClickListener {
                        GalleryFragment.navigate(fragment.findNavController(), it, position, keys)
                    }
                }
            }
            ViewCompat.setTransitionName(
                this,
                context.getString(R.string.image_thumbnail, position)
            )
        }
    }

    override fun getItemCount(): Int = uris.size
}