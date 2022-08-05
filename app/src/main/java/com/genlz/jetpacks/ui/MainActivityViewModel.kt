package com.genlz.jetpacks.ui

import android.graphics.drawable.Drawable
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.genlz.jetpacks.repository.AdRepository
import com.genlz.jetpacks.utility.Loading
import com.genlz.jetpacks.utility.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val adRepository: AdRepository,
) : ViewModel() {

    private val _splash = MutableStateFlow<UiState<Drawable>>(Loading)
    val splash = _splash.asStateFlow()

    private val _currentScreenIndex = MutableStateFlow(0)
    val currentScreenIndex = _currentScreenIndex.asStateFlow()

    fun onNewScreenSelected(screenIndex: Int) {
        _currentScreenIndex.update { screenIndex }
    }

    /**
     * A hot flow,no need to activate.
     * A detail：[LiveData] must observe it to activate.
     */
    val ready = flow {
//        adRepository.loadSplash().collect {
//            _splash.emit(it)
//            delay(MainActivity.SPLASH_DISPLAY_TIME)
//            emit(true)
//        }
        delay(MainActivity.SPLASH_DISPLAY_TIME)
        emit(true)
    }.catch { t ->
        t.printStackTrace()
        emit(true)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    @Suppress("UNUSED")
    companion object {
        private const val TAG = "MainActivityViewModel"
    }
}