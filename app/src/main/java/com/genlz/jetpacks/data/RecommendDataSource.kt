package com.genlz.jetpacks.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.genlz.jetpacks.api.RecommendApi

class RecommendDataSource(
    private val backendApi: RecommendApi,
    private val query: String,
) : PagingSource<Int, RecommendResponse>() {
    override fun getRefreshKey(state: PagingState<Int, RecommendResponse>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RecommendResponse> {
        val page = params.key ?: INITIAL_PAGE
        val response = backendApi.loadRecommendData(query, page, params.loadSize)
        TODO()
    }
}

private const val INITIAL_PAGE = 1
