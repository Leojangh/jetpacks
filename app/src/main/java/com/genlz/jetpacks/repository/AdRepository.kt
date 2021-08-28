package com.genlz.jetpacks.repository

import com.genlz.jetpacks.api.SplashDataSource
import com.genlz.jetpacks.di.IoDispatcher
import com.genlz.jetpacks.utility.Fail
import com.genlz.jetpacks.utility.Loading
import com.genlz.jetpacks.utility.Success
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

@ViewModelScoped
class AdRepository @Inject constructor(
    private val splashDataSource: SplashDataSource,
    @IoDispatcher
    private val dispatcher: CoroutineDispatcher,
) {

    fun loadSplash() = flow {
        emit(Loading)
        splashDataSource.getRandomSplash()?.let {
            emit(Success(it))
        } ?: error("Load Failed")
    }.catch {
        emit(Fail(it))
    }.flowOn(dispatcher)

    @Suppress("UNUSED")
    companion object {
        private const val TAG = "AdRepository"
    }
}