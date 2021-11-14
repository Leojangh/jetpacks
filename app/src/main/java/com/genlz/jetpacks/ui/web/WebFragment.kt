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
import androidx.core.view.doOnPreDraw
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.*

@AndroidEntryPoint
class WebFragment : Fragment() {

    private val args by navArgs<WebFragmentArgs>()

    private val viewModel by viewModels<WebFragmentViewModel>()

    /**
     * Be care for it's lifecycle.
     */
    private val webView get() = view as PowerfulWebView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = viewModel.getWeb(args.uri)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        findFullscreenController()?.enterFullscreen()

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
//        findFabSetter()?.setupFab {
//            setOnClickListener {
//                webView.overlay.clear()
//            }
//        }
        postponeEnterTransition()
        (view.parent as? ViewGroup)?.doOnPreDraw {
            startPostponedEnterTransition()
        }
    }

    private val domTouchListener: (RectF) -> Unit = { rectF ->
        val rect = rectF.toRect()
        when (webView.hitTestResult.type) {
            WebView.HitTestResult.IMAGE_TYPE -> {
                val uri = webView.hitTestResult.extra!!
                val img = ImageView(context).apply {
                    layout(rect.left, rect.top, rect.right, rect.bottom)
                    isFocusable = false
                    isClickable = false
                }
                val key = MemoryCache.Key(uri)
                img.load(uri) {
                    memoryCacheKey(uri)
                }
//                postponeEnterTransition()
//                img.doOnPreDraw {
//                    startPostponedEnterTransition()
//                }
                webView.overlay.apply {
                    clear()
                    add(img)
                }
                GalleryFragment.navigate(
                    findNavController(),
                    listOf(img),
                    0,
                    mapOf(key to uri)
                )
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