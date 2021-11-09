package com.genlz.jetpacks.ui.web

import android.graphics.RectF
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.ImageView
import androidx.core.graphics.toRect
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import coil.load
import coil.memory.MemoryCache
import com.genlz.jetpacks.BuildConfig
import com.genlz.jetpacks.ui.GalleryFragment
import com.genlz.jetpacks.ui.common.FabSetter.Companion.findFabSetter
import com.genlz.jetpacks.ui.web.bridge.Android
import com.genlz.share.util.appcompat.imeInsets
import com.genlz.share.util.appcompat.plus
import com.genlz.share.util.appcompat.setOnApplyWindowInsetsListener
import com.genlz.share.util.appcompat.statusBarInsets
import com.genlz.share.widget.web.PowerfulWebView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.*

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
        WebView.setWebContentsDebuggingEnabled(BuildConfig.DEBUG)

        injectJavascript()
        webView.apply {
            setOnApplyWindowInsetsListener { v, i, ip ->
                val insets = i.statusBarInsets + i.imeInsets
                v.updatePadding(top = insets.top + ip.top, bottom = insets.bottom + ip.bottom)
                i
            }
            addJavascriptInterface(Android(context, this), "Android")

            domTouchListener = this@WebFragment.domTouchListener

            if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
                WebSettingsCompat.setForceDark(settings, WebSettingsCompat.FORCE_DARK_AUTO)
            }
        }
        findFabSetter()?.setupFab {
            setOnClickListener {
                webView.overlay.clear()
            }
        }
        Log.d(TAG, "onViewCreated: $savedInstanceState")
        webView.loadUrl(args.uri)
    }

    private val domTouchListener: (RectF) -> Unit = { rectF ->
        val rect = rectF.toRect()
        when (webView.hitTestResult.type) {
            WebView.HitTestResult.IMAGE_TYPE -> {
                val uri = webView.hitTestResult.extra!!
                val img = ImageView(context).apply {
                    layout(rect.left, rect.top, rect.right, rect.bottom)
                }
                val key = MemoryCache.Key(uri)
                img.load(uri) {
                    memoryCacheKey(uri)
                    listener { _, _ ->
                        startPostponedEnterTransition()
                    }
                }
                GalleryFragment.navigate(
                    findNavController(),
                    listOf(img),
                    0,
                    mapOf(key to uri)
                )
                postponeEnterTransition()
//                        img.doOnPreDraw {
//                            startPostponedEnterTransition()
//                        }
//                        img.background = ColorDrawable(Color.BLUE).apply { alpha = 20 }
                webView.overlay.add(img)
            }
        }
    }


    override fun onResume() {
        super.onResume()
        webView.onResume()
    }

    override fun onPause() {
        super.onPause()
        webView.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
//        findFullscreenController()?.exitFullscreen()
    }

    companion object {

        const val TAG = "WebFragment"
    }
}