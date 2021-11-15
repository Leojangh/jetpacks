package com.genlz.jetpacks.ui.web

import android.content.res.Configuration
import android.graphics.RectF
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.ImageView
import androidx.core.graphics.toRect
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import coil.load
import coil.memory.MemoryCache
import com.genlz.jetpacks.ui.gallery.GalleryActivity
import com.genlz.jetpacks.ui.web.bridge.Android
import com.genlz.share.util.appcompat.*
import com.genlz.share.widget.web.DomTouchListener
import com.genlz.share.widget.web.PowerfulWebView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WebFragment : Fragment(), DomTouchListener {

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
        viewModel.overlays.observe(viewLifecycleOwner) {
            webView.overlay.add(it)
        }
        webView.apply {
            isNestedScrollingEnabled = false
            val bridge = Android(context, this)
            addJavascriptInterface(bridge, bridge.name)
            setOnClickListener {
                Log.d(TAG, "onViewCreated: ")
            }
            domTouchListener = this@WebFragment

            if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
                WebSettingsCompat.setForceDark(settings, WebSettingsCompat.FORCE_DARK_AUTO)
            }
        }
    }

    override fun onDomTouch(webView: WebView, hitTestResult: WebView.HitTestResult, rectF: RectF) {
        val rect = rectF.toRect()
        when (hitTestResult.type) {
            WebView.HitTestResult.IMAGE_TYPE -> {
                val uri = hitTestResult.extra ?: return
                val img = ImageView(webView.context).apply {
                    val scrollY = webView.scrollY
                    val scrollX = webView.scrollX
                    // ViewGroupOverlay doesn't perform the layout pass on Views added to it;
                    // that is, as is automatically performed when adding a View to an existing layout,
                    // you need to manually perform measure() and layout() on a view in order for
                    // it to be correctly displayed on screen.
                    layout(
                        rect.left + scrollX,
                        rect.top + scrollY,
                        rect.right + scrollX,
                        rect.bottom + scrollY
                    )
                    isFocusable = true
                    isClickable = true
                }
                val key = MemoryCache.Key(uri)
                img.load(uri) {
                    memoryCacheKey(key)
                }
                webView.overlay.apply {
                    clear()
                    add(img)
                }
                GalleryActivity.navigate(requireActivity(), listOf(img), mapOf(key to uri))

//                GalleryFragment.navigate(
//                    findNavController(),
//                    listOf(img),
//                    0,
//                    mapOf(key to uri)
//                )
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

    /**
     * Because the view overlay layout step do manually,we need clear overlays when
     * configuration changed.
     */
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        webView.overlay.clear()
    }

    companion object {

        const val TAG = "WebFragment"
    }
}