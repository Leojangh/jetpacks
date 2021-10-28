package com.genlz.jetpacks.ui.web

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.activity.addCallback
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.genlz.jetpacks.BuildConfig
import com.genlz.jetpacks.ui.common.FabSetter.Companion.findFabSetter
import com.genlz.jetpacks.ui.common.FullscreenController.Companion.findFullscreenController
import com.genlz.jetpacks.ui.web.bridge.JavascriptBridge.Companion.wrap
import com.genlz.jetpacks.ui.web.bridge.JavascriptBridgeImpl
import com.genlz.jetpacks.utility.appcompat.*
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

class WebFragment : Fragment() {

    private val args by navArgs<WebFragmentArgs>()

    /**
     * Be care for it's lifecycle.
     */
    private val webView get() = view as PowerfulWebView

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = PowerfulWebView(requireContext())

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        findFullscreenController()?.enterFullscreen()
        WebView.setWebContentsDebuggingEnabled(BuildConfig.DEBUG)
        val jQ = requireArguments()[JQUERY_SCRIP] as String
        val imageHook = requireArguments()[IMAGE_HOOK_SCRIPT] as String
        webView.scripts = setOf(jQ, imageHook)
        webView.apply {
            setOnApplyWindowInsetsListener { v, i, ip ->
                val insets = i.statusBarInsets + i.imeInsets
                v.updatePadding(top = insets.top + ip.top, bottom = insets.bottom + ip.bottom)
                i
            }
            addJavascriptInterface(JavascriptBridgeImpl(context).wrap(), "Android")
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            webView.goBack()
        }
        findFabSetter()?.setupFab {
            setOnClickListener {

            }
        }
        webView.loadUrl(args.uri)
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