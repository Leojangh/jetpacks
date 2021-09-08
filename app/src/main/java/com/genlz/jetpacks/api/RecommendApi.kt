package com.genlz.jetpacks.api

import com.genlz.jetpacks.data.RecommendResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface RecommendApi {

    @GET("api/recommend")
    suspend fun loadRecommendData(
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
    ): RecommendResponse
}