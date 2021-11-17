package com.genlz.jetpacks.ui.web

import android.content.Context
import android.util.Log
import android.view.View
import android.webkit.WebView
import androidx.lifecycle.*
import com.genlz.jetpacks.BuildConfig
import com.genlz.share.widget.web.PowerfulWebView
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import javax.inject.Inject

@HiltViewModel
class WebFragmentViewModel @Inject constructor(
    @ApplicationContext val context: Context,
) : ViewModel() {

    //TODO: register overlays and support configuration change.
    private val _overlays = MutableLiveData<View>()
    val overlays: LiveData<View> = _overlays

    //A singleton instance during whole lifecycle.
    @OptIn(FlowPreview::class)
    private val webView = PowerfulWebView(context).apply {
        injectJavascript()
        WebView.setWebContentsDebuggingEnabled(BuildConfig.DEBUG)
    }

    fun getWeb(url: String): PowerfulWebView {
        return if (webView.url == url) {
            webView
        } else {
            webView.apply { loadUrl(url) }
        }
    }

    /**
     * Make sure invoke the method during view lifecycle.
     */
    @FlowPreview
    private fun injectJavascript() {
        viewModelScope.launch(Dispatchers.IO) {
            val assets = context.assets
            val files = assets.list("") ?: return@launch
            files.asFlow().filter { file ->
                file.endsWith(".js") && file !in webView.scripts.keys
            }.flatMapMerge { file ->
                //Concurrency map
                @Suppress("BlockingMethodInNonBlockingContext")
                val pair = file to assets.open(file).use {
                    InputStreamReader(it, StandardCharsets.UTF_8).readText()
                }
                flowOf(pair)
            }.catch { t ->
                Log.e(TAG, "injectJavascript: ", t)
            }.toSet().toMap().also {
                withContext(Dispatchers.Main) {
                    webView.scripts += it
                    Log.d(TAG, "the file would inject: $it")
                }
            }
        }
    }

    companion object {
        private const val TAG = "WebFragmentViewModel"
    }
}