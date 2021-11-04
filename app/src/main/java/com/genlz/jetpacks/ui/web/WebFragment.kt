package com.genlz.jetpacks.ui.web

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.activity.addCallback
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import com.genlz.jetpacks.BuildConfig
import com.genlz.jetpacks.ui.GalleryFragment
import com.genlz.jetpacks.ui.common.ActionBarCustomizer.Companion.findActionBarCustomizer
import com.genlz.jetpacks.ui.web.bridge.JavascriptBridge.Companion.wrap
import com.genlz.jetpacks.ui.web.bridge.JavascriptBridgeImpl
import com.genlz.jetpacks.utility.appcompat.imeInsets
import com.genlz.jetpacks.utility.appcompat.plus
import com.genlz.jetpacks.utility.appcompat.setOnApplyWindowInsetsListener
import com.genlz.jetpacks.utility.appcompat.statusBarInsets
import com.genlz.share.widget.PowerfulWebView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

class WebFragment : Fragment() {

    private val args by navArgs<WebFragmentArgs>()

    /**
     * Be care for it's lifecycle.
     */
    private val webView get() = view as PowerfulWebView

    /**
     * Make sure invoke the method during view lifecycle.
     */
    private fun injectJavascript() {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val assets = requireContext().assets
            val files = assets.list("") ?: return@launch
            files.asFlow().filter { file ->
                file.endsWith(".js") && file !in webView.scripts.keys
            }.map { file ->
                file to assets.open(file).use {
                    InputStreamReader(it, StandardCharsets.UTF_8).readText()
                }
            }.catch {
                Log.e(TAG, "injectJavascript: ", it)
            }.toSet().toMap().also {
                webView.scripts = it
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = PowerfulWebView(requireContext())

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        findFullscreenController()?.enterFullscreen()
        findActionBarCustomizer()?.custom {
            setDisplayHomeAsUpEnabled(true)
        }
        WebView.setWebContentsDebuggingEnabled(BuildConfig.DEBUG)

        injectJavascript()
        webView.apply {
            setOnApplyWindowInsetsListener { v, i, ip ->
                val insets = i.statusBarInsets + i.imeInsets
                v.updatePadding(top = insets.top + ip.top, bottom = insets.bottom + ip.bottom)
                i
            }
            addJavascriptInterface(JavascriptBridgeImpl(context).wrap(), "Android")

            longPressListener = {

                when (hitTestResult.type) {
                    WebView.HitTestResult.IMAGE_TYPE -> {
                        Log.d(TAG, "onViewCreated: ${hitTestResult.extra}")
                        GalleryFragment.navigate(findNavController(), listOf(), 0, mapOf())
                    }
                }
            }

            if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
                WebSettingsCompat.setForceDark(settings, WebSettingsCompat.FORCE_DARK_AUTO)
            }

        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            webView.goBack()
        }
//        findFabSetter()?.setupFab {
//            setOnClickListener {
//
//            }
//        }
        webView.loadUrl(args.uri)
    }

    override fun onResume() {
        super.onResume()
        webView.onResume()
    }

    override fun onPause() {
        super.onPause()
        webView.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
//        findFullscreenController()?.exitFullscreen()
    }

    companion object {

        private const val JQUERY_SCRIP = "jQ"
        private const val IMAGE_HOOK_SCRIPT = "image_hook"

        const val TAG = "WebFragment"
    }
}