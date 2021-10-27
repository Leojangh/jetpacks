package com.genlz.jetpacks.ui.web

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.addCallback
import androidx.annotation.UiThread
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.genlz.android.viewbinding.viewBinding
import com.genlz.jetpacks.BuildConfig
import com.genlz.jetpacks.R
import com.genlz.jetpacks.databinding.FragmentWebBinding
import com.genlz.jetpacks.ui.common.FabSetter.Companion.findFabSetter
import com.genlz.jetpacks.ui.common.FullscreenController.Companion.findFullscreenController
import com.genlz.jetpacks.utility.appcompat.*
import java.io.InputStreamReader
import java.lang.reflect.Proxy
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
//            if (binding.webView.canGoBack()) {
            binding.webView.goBack()
//            }
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

        private const val JQUERY_SCRIP = "jQ"
        private const val IMAGE_HOOK_SCRIPT = "image_hook"

    }
}

const val TAG = "WebFragment"

/**
 * All method run in a dedicate thread named "JavaBridge",but you don't
 * care about thread.
 */
@Suppress("UNUSED")
private class JavascriptBridgeImpl(
    override val context: Context
) : JavascriptBridge {

    override fun toast(message: String) {
        Toast.makeText(context.applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    override fun transit(left: Float, top: Float, right: Float, bottom: Float) {
        Log.d(TAG, "transit: $left,$top,$right,$bottom")
    }
}

/**
 * Dynamic proxy.
 */
fun JavascriptBridge.wrap(): JavascriptBridge {
    val proxy = Proxy.newProxyInstance(
        context.classLoader,
        arrayOf(JavascriptBridge::class.java)
    ) { _, method, args ->
        if (method.declaringClass == Any::class.java) {
            if (args.isNullOrEmpty()) method(this) else method(this, args)
        } else {
            val safeArgs = if (args.isNullOrEmpty()) emptyArray<Any?>() else args
            if (method.isAnnotationPresent(UiThread::class.java)) {
                //Post to ui thread.
                context.mainExecutorExt.execute {
                    method(this, *safeArgs)
                }
            } else {
                method(this, *safeArgs) //Invoke directly
            }
        }
    } as JavascriptBridge
    return JavascriptBridgeWrapper(proxy)
}

interface JavascriptBridge {

    val context: Context

    @UiThread
    @JavascriptInterface
    fun toast(message: String)

    @JavascriptInterface
    fun transit(left: Float, top: Float, right: Float, bottom: Float)
}

/**
 * Static proxy.
 */
class JavascriptBridgeWrapper(
    private val base: JavascriptBridge
) : JavascriptBridge by base