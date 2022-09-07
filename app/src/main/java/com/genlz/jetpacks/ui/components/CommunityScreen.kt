package com.genlz.jetpacks.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import com.genlz.jetpacks.repository.CommunityRepository
import com.genlz.jetpacks.utility.Fail
import com.genlz.jetpacks.utility.Loading
import com.genlz.jetpacks.utility.Success
import com.genlz.jetpacks.utility.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@Composable
fun CommunityScreen(
    paddingValues: PaddingValues,
) {
    Box(modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
    ) {

    }
}

private const val TAG = "CommunityScreen"
