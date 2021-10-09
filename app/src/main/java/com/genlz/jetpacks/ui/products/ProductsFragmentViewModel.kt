package com.genlz.jetpacks.ui.products

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.genlz.jetpacks.R
import com.genlz.jetpacks.datasource.SplashDataSource
import com.genlz.jetpacks.pojo.Post
import com.genlz.jetpacks.pojo.User
import com.genlz.jetpacks.repository.AdRepository
import com.genlz.jetpacks.ui.GalleryFragment
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDateTime
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

    fun mockResource() = listOf(
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

    fun randomResource() = listOf(
        SplashDataSource.SPLASH_RANDOM_URL.toUri(),
        SplashDataSource.SPLASH_RANDOM_URL.toUri(),
        SplashDataSource.SPLASH_RANDOM_URL.toUri(),
        SplashDataSource.SPLASH_RANDOM_URL.toUri(),
        SplashDataSource.SPLASH_RANDOM_URL.toUri(),
        SplashDataSource.SPLASH_RANDOM_URL.toUri(),
        SplashDataSource.SPLASH_RANDOM_URL.toUri(),
        SplashDataSource.SPLASH_RANDOM_URL.toUri(),
        SplashDataSource.SPLASH_RANDOM_URL.toUri(),
        SplashDataSource.SPLASH_RANDOM_URL.toUri(),
    )

    fun mockPosts() = List(10) {
        Post(
            it.toLong(),
            "title$it",
            "abstraction_$it",
            mockResource().map { uri -> uri.toString() },
            listOf("666", "bbb"),
            User("$it", "sf", "Leojangh$it", true, "device$it", it, "additional"),
            LocalDateTime.now(),
            it,
            it,
            it
        )
    }
}