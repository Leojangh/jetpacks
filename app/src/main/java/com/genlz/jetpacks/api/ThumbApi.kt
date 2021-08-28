package com.genlz.jetpacks.api

import retrofit2.http.GET
import retrofit2.http.POST

interface ThumbApi {

    @GET("api/thumbs")
    suspend fun getThumbs(): Int

    @POST("api/thumbUp")
    suspend fun thumbUp()

    @POST("api/thumbDown")
    suspend fun thumbDown()
}