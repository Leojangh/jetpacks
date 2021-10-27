package com.genlz.jetpacks.ui.web

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.genlz.jetpacks.ui.common.FabSetter.Companion.findFabSetter
import com.genlz.jetpacks.ui.common.FullscreenController.Companion.findFullscreenController
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

class WebFragment : Fragment() {

    private val args by navArgs<WebFragmentArgs>()

    private val webView get() = view as WebView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = WebView(requireContext()).apply {
        settings.apply {
            javaScriptEnabled = true
        }
        val script = requireContext().assets.open("jQuery_injection.js").use {
            InputStreamReader(it, StandardCharsets.UTF_8).readText()
        }
        webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest) = true

            override fun onPageFinished(view: WebView, url: String) {
                //inject script
                view.evaluateJavascript(script, null)
            }
        }
        findFullscreenController()?.enterFullscreen()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (webView.canGoBack()) {
                webView.goBack()
            }
        }
        findFabSetter()?.setupFab {

        }
        webView.loadUrl(args.uri)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        findFullscreenController()?.exitFullscreen()
    }

    companion object {
        private const val TAG = "WebFragment"
    }
}