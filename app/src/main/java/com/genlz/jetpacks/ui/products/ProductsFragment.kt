package com.genlz.jetpacks.ui.products

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.genlz.android.viewbinding.viewBinding
import com.genlz.jetpacks.R
import com.genlz.jetpacks.databinding.FragmentProductsBinding

class ProductsFragment : Fragment(R.layout.fragment_products) {

    private val binding by viewBinding<FragmentProductsBinding>()

    private val viewModel by viewModels<ProductsFragmentViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding
    }
}