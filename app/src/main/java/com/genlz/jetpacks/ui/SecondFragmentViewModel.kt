package com.genlz.jetpacks.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.genlz.jetpacks.repository.ThumbRepository
import com.genlz.jetpacks.utility.Loading
import com.genlz.jetpacks.utility.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SecondFragmentViewModel @Inject constructor(
    private val repository: ThumbRepository,
) : ViewModel() {

    private val _t = MutableStateFlow<UiState<Int>>(Loading).apply {
        viewModelScope.launch {
            repository.getThumbs().collect(::emit)
        }
    }
    val t = _t.asStateFlow()

    fun thumbUp() {
        viewModelScope.launch {
            repository.thumbUp()
            _t.value = repository.getThumbs().last()
        }
    }

    fun thumbDown() {
        viewModelScope.launch {
            repository.thumbDown()
            _t.value = repository.getThumbs().last()
        }
    }
}