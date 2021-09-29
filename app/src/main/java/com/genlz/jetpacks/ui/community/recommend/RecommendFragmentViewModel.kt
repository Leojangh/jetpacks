package com.genlz.jetpacks.ui.community.recommend

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.genlz.jetpacks.pojo.Post
import com.genlz.jetpacks.repository.RecommendRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class RecommendFragmentViewModel @Inject constructor(
    private val repository: RecommendRepository
) : ViewModel() {

    fun loadRecommend(): Flow<PagingData<Post>> {
        return repository.loadRecommendData().cachedIn(viewModelScope)
    }
}