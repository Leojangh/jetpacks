package com.genlz.jetpacks.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.genlz.jetpacks.api.RecommendApi
import com.genlz.jetpacks.data.Post
import com.genlz.jetpacks.data.RecommendDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RecommendRepository @Inject constructor(
    private val recommendDataSource: RecommendDataSource
) {
    fun loadRecommendData(): Flow<PagingData<Post>> {
        return Pager(
            config = PagingConfig(pageSize = NETWORK_PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = { recommendDataSource }
        ).flow
    }

    companion object {
        private const val NETWORK_PAGE_SIZE = 25
    }
}