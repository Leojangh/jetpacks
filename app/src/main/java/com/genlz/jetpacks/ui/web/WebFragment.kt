package com.genlz.jetpacks.ui.web

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.genlz.android.viewbinding.viewBinding
import com.genlz.jetpacks.BuildConfig
import com.genlz.jetpacks.R
import com.genlz.jetpacks.databinding.FragmentWebBinding
import com.genlz.jetpacks.ui.common.FabSetter.Companion.findFabSetter
import com.genlz.jetpacks.ui.common.FullscreenController.Companion.findFullscreenController
import com.genlz.jetpacks.ui.web.bridge.JavascriptBridge.Companion.wrap
import com.genlz.jetpacks.ui.web.bridge.JavascriptBridgeImpl
import com.genlz.jetpacks.utility.appcompat.*
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

class WebFragment : Fragment(R.layout.fragment_web) {

    private val args by navArgs<WebFragmentArgs>()

    private val binding by viewBinding(FragmentWebBinding::bind)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val localArgs = requireArguments()
        //Save to args for read once.
        if (!localArgs.containsKey(JQUERY_SCRIP)) {
            val jQ = context.assets.open("jQuery_injection.js").use {
                InputStreamReader(it, StandardCharsets.UTF_8).readText()
            }
            localArgs.putString(JQUERY_SCRIP, jQ)
        }
        if (!localArgs.containsKey(IMAGE_HOOK_SCRIPT)) {
            val imageHook = context.assets.open("image_listener_hook.js").use {
                InputStreamReader(it, StandardCharsets.UTF_8).readText()
            }
            localArgs.putString(IMAGE_HOOK_SCRIPT, imageHook)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        findFullscreenController()?.enterFullscreen()
        WebView.setWebContentsDebuggingEnabled(BuildConfig.DEBUG)
        binding.webView.apply {
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
            }
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView, url: String) {
                    val jQ = requireArguments()[JQUERY_SCRIP] as String
                    val imageHook = requireArguments()[IMAGE_HOOK_SCRIPT] as String
                    //inject JQuery
                    view.evaluateJavascript(jQ, null)
                    //inject image hook
                    view.evaluateJavascript(imageHook, null)
                }
            }
            setOnApplyWindowInsetsListener { v, i, ip ->
                val insets = i.statusBarInsets + i.imeInsets
                v.updateMargin(top = insets.top + ip.top, bottom = insets.bottom + ip.bottom)
                i
            }
            addJavascriptInterface(JavascriptBridgeImpl(context).wrap(), "Android")
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            binding.webView.goBack()
//                findNavController().navigateUp()
        }
        findFabSetter()?.setupFab {
            setOnClickListener {
                binding.webView.evaluateJavascript("$('img')[0].src") {
                    Log.d(TAG, "onViewCreated: $it")
                }
            }
        }
        binding.webView.loadUrl(args.uri)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        findFullscreenController()?.exitFullscreen()
    }

    companion object {

        private const val JQUERY_SCRIP = "jQ"
        private const val IMAGE_HOOK_SCRIPT = "image_hook"

        const val TAG = "WebFragment"
    }
}