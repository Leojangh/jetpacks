package com.genlz.jetpacks.ui.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.genlz.jetpacks.repository.AdRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductsFragmentViewModel @Inject constructor(
    private val adRepository: AdRepository
) : ViewModel() {



    fun loadRandomImage() {
        viewModelScope.launch {
            adRepository.loadSplash()
        }
    }
}