package com.genlz.jetpacks.di

import com.genlz.jetpacks.api.AdApi
import com.genlz.jetpacks.api.RecommendApi
import com.genlz.jetpacks.api.ThumbApi
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(ViewModelComponent::class)
object RetrofitModule {

    private const val BASE_URL = "http://192.168.18.219:8080/"

    const val SPLASH_BASE_URL = "https://source.unsplash.com/"

    const val RECOMMEND_BASE_URL = BASE_URL

    @Provides
    fun provideGson(): Gson {
        return GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create()
    }

    @Provides
    fun provideRetrofitBuilder(
        client: OkHttpClient,
        gson: Gson,
    ): Retrofit.Builder {
        return Retrofit.Builder()
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
    }

    @Provides
    fun provideThumbApi(
        retrofit: Retrofit.Builder,
    ): ThumbApi {
        return retrofit
            .baseUrl(BASE_URL)
            .build()
            .create(ThumbApi::class.java)
    }

    @Provides
    fun provideAdApi(retrofit: Retrofit.Builder): AdApi {
        return retrofit
            .baseUrl(SPLASH_BASE_URL)
            .build()
            .create(AdApi::class.java)
    }

    @Provides
    fun provideRecommendApi(retrofit: Retrofit.Builder): RecommendApi {
        return retrofit
            .baseUrl(RECOMMEND_BASE_URL)
            .build()
            .create(RecommendApi::class.java)
    }
}