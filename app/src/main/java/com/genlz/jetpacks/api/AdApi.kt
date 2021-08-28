package com.genlz.jetpacks.api

import retrofit2.http.GET

interface AdApi {

    @GET("random")
    suspend fun getRandomSplash(): String
}
