package com.genlz.jetpacks.ui.products

import android.content.Context
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.genlz.jetpacks.R
import com.genlz.jetpacks.datasource.SplashDataSource
import com.genlz.jetpacks.pojo.Post
import com.genlz.jetpacks.pojo.User
import com.genlz.jetpacks.repository.AdRepository
import com.genlz.jetpacks.ui.GalleryFragment
import dagger.Reusable
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class ProductsFragmentViewModel @Inject constructor(
    private val adRepository: AdRepository,
    @ApplicationContext private val context: Context,
) : ViewModel() {

    fun loadRandomImage() {
        viewModelScope.launch {
            adRepository.loadSplash()
        }
    }

    val posts = MutableStateFlow(mockPosts()).asStateFlow()

    private fun mockResource() = listOf(
        GalleryFragment.localResUri(R.mipmap.unsplash1),
        GalleryFragment.localResUri(R.mipmap.unsplash2),
        GalleryFragment.localResUri(R.mipmap.unsplash3),
        GalleryFragment.localResUri(R.mipmap.unsplash4),
        GalleryFragment.localResUri(R.mipmap.unsplash5),
        GalleryFragment.localResUri(R.mipmap.unsplash6),
        GalleryFragment.localResUri(R.mipmap.unsplash7),
        GalleryFragment.localResUri(R.mipmap.unsplash8),
        GalleryFragment.localResUri(R.mipmap.unsplash9),
    ).map { it.toString() }

    private fun randomResource() = List(9) {
        SplashDataSource.SPLASH_RANDOM_URL + "/$it"
    }

    private fun mockPosts() = List(10) {
        Post(
            it.toLong(),
            "title$it",
            "abstraction_$it" + context.getString(R.string.long_text),
            randomResource().shuffled(),
            listOf("666", "bbb"),
            User(
                "$it",
                GalleryFragment.localResUri(R.drawable.ic_twitter).toString(),
                "Leojangh$it",
                true,
                "device$it",
                it,
                "additional"
            ),
            LocalDateTime.now(),
            it,
            it,
            it
        )
    }
}