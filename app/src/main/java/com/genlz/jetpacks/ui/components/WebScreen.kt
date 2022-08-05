package com.genlz.jetpacks.ui.components

import androidx.compose.runtime.Composable
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewState

@Composable
fun WebScreen() {
    val state = rememberWebViewState(url = "https://genlz.com")
    WebView(state = state)
}