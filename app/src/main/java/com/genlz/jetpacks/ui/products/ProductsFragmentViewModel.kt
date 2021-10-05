package com.genlz.jetpacks.ui.products

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.genlz.jetpacks.R
import com.genlz.jetpacks.repository.AdRepository
import com.genlz.jetpacks.ui.GalleryFragment
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductsFragmentViewModel @Inject constructor(
    private val adRepository: AdRepository
) : ViewModel() {

    fun loadRandomImage() {
        viewModelScope.launch {
            adRepository.loadSplash()
        }
    }

    fun mockResource(): List<Uri> = listOf(
        GalleryFragment.localResUri(R.mipmap.unsplash1),
        GalleryFragment.localResUri(R.mipmap.unsplash2),
        GalleryFragment.localResUri(R.mipmap.unsplash3),
        GalleryFragment.localResUri(R.mipmap.unsplash4),
        GalleryFragment.localResUri(R.mipmap.unsplash5),
        GalleryFragment.localResUri(R.mipmap.unsplash6),
        GalleryFragment.localResUri(R.mipmap.unsplash7),
        GalleryFragment.localResUri(R.mipmap.unsplash8),
        GalleryFragment.localResUri(R.mipmap.unsplash9),
    )
}