package com.genlz.jetpacks.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.genlz.jetpacks.api.RecommendApi
import javax.inject.Inject

class RecommendDataSource @Inject constructor(
    private val backendApi: RecommendApi,
) : PagingSource<Int, Post>() {
    override fun getRefreshKey(state: PagingState<Int, Post>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Post> {
        val page = params.key ?: INITIAL_PAGE
        return try {
            val response = backendApi.loadRecommendData(page, params.loadSize)
            LoadResult.Page(
                data = response.posts,
                prevKey = if (page == INITIAL_PAGE) null else page - 1,
                nextKey = if (page == response.totalPages) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}

private const val INITIAL_PAGE = 1
