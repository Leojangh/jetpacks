package com.genlz.jetpacks.repository

import android.util.Log
import com.genlz.jetpacks.api.ThumbApi
import com.genlz.jetpacks.di.IoDispatcher
import com.genlz.jetpacks.persistence.PostDao
import com.genlz.jetpacks.utility.Fail
import com.genlz.jetpacks.utility.Loading
import com.genlz.jetpacks.utility.Success
import com.genlz.jetpacks.utility.UiState
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import javax.inject.Inject

@ViewModelScoped
class ThumbRepository @Inject constructor(
    private val api: ThumbApi,
    private val dao: PostDao,
    @IoDispatcher
    private val dispatcher: CoroutineDispatcher,
) {
    private var cacheLast: UiState<Int> = Loading

    private val thumbs = flow<UiState<Int>> {
        emit(Success(api.getThumbs()))
        Log.d(TAG, "getThumbs: success")
    }.onEach {
        cacheLast = it
    }.catch {
        emit(Fail(it))
        emit(cacheLast)
        Log.d(TAG, "getThumbs: error")
    }.flowOn(dispatcher)

    fun getThumbs() = thumbs

    suspend fun thumbUp() {
        withContext(dispatcher) {
            api.thumbUp()
        }
    }

    suspend fun thumbDown() = withContext(dispatcher) {
        api.thumbDown()
    }

    companion object {
        private const val TAG = "ThumbRepository"
    }
}