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
    vm: CommunityScreenViewModel,
) {
    Box(modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
    ) {
        val data = when (val it = vm.allData.collectAsState(initial = Loading).value) {
            is Loading -> listOf()
            is Success -> it.data
            is Fail -> listOf("出错啦！${it.throwable}")
        }
        LazyColumn {
            for (it in data) item {
                Text(text = it)
            }
        }
    }
}

private const val TAG = "CommunityScreen"

@HiltViewModel
class CommunityScreenViewModel @Inject constructor(
    repo: CommunityRepository,
) : ViewModel() {

    val allData = flowOf(ArrayList<String>()).combine(
        repo.getFcm()
    ) { history, latest ->
        history += latest
        //type inferring has bug??
        Success(history.toList()) as UiState<List<String>>
    }.catch {
        emit(Fail(it))
    }
}