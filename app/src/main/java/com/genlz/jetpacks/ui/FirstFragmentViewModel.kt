package com.genlz.jetpacks.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.genlz.jetpacks.repository.ThumbRepository
import com.genlz.jetpacks.utility.Loading
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FirstFragmentViewModel @Inject constructor(
    private val repository: ThumbRepository,
) : ViewModel() {

    val st = repository.getThumbs().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000L),
        Loading
    )

    fun thumbUp() {
        viewModelScope.launch {
            try {
                repository.thumbUp()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun thumbDown() {
        viewModelScope.launch {
            try {
                repository.thumbDown()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        private const val TAG = "FirstFragmentViewModel"
    }
}

