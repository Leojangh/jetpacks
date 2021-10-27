package com.genlz.jetpacks.ui.web

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.addCallback
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.webkit.WebViewCompat
import com.genlz.android.viewbinding.viewBinding
import com.genlz.jetpacks.BuildConfig
import com.genlz.jetpacks.R
import com.genlz.jetpacks.databinding.FragmentWebBinding
import com.genlz.jetpacks.ui.common.FabSetter.Companion.findFabSetter
import com.genlz.jetpacks.ui.common.FullscreenController.Companion.findFullscreenController
import com.genlz.jetpacks.utility.appcompat.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

class WebFragment : Fragment(R.layout.fragment_web) {

    private val args by navArgs<WebFragmentArgs>()

    private val binding by viewBinding(FragmentWebBinding::bind)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val script = context.assets.open("jQuery_injection.js").use {
            InputStreamReader(it, StandardCharsets.UTF_8).readText()
        }
        //Save to args for read once.
        requireArguments().putString(KEY_SCRIP, script)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        findFullscreenController()?.enterFullscreen()
        binding.webView.apply {
            settings.apply {
                javaScriptEnabled = true
            }
            WebView.setWebContentsDebuggingEnabled(BuildConfig.DEBUG)
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView, url: String) {
                    val script = requireArguments()[KEY_SCRIP] as String
                    //inject JQuery
                    view.evaluateJavascript(script, null)
                }
            }
            setOnApplyWindowInsetsListener { v, i, ip ->
                val insets = i.statusBarInsets + i.imeInsets
                v.updateMargin(top = insets.top + ip.top, bottom = insets.bottom + ip.bottom)
                i
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (binding.webView.canGoBack()) {
                binding.webView.goBack()
            }
        }
        findFabSetter()?.setupFab {
            setOnClickListener {
//                findNavController().navigateUp()
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
        private const val TAG = "WebFragment"

        private const val KEY_SCRIP = "script"
    }
}