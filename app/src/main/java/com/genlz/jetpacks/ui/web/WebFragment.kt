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
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import coil.memory.MemoryCache
import com.genlz.jetpacks.ui.gallery.GalleryFragment
import com.genlz.jetpacks.ui.web.bridge.AndroidImpl
import com.genlz.share.util.appcompat.*
import com.genlz.share.util.postponeEnterTransitionUtilDraw
import com.genlz.webview.DomTouchListener
import com.genlz.webview.PowerfulWebView
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

        webView.apply {
            isNestedScrollingEnabled = false
            val bridge = AndroidImpl(this)
            addJavascriptInterface(bridge, bridge.name)
            domTouchListener = this@WebFragment
        }
        postponeEnterTransitionUtilDraw()
    }

    override fun onDomTouch(webView: WebView, hitTestResult: WebView.HitTestResult, rectF: RectF) {
        val rect = rectF.toRect()
        when (hitTestResult.type) {
            WebView.HitTestResult.IMAGE_TYPE -> {
                val img = ImageView(webView.context)
                val uri = hitTestResult.extra ?: run {
                    Log.i(TAG, "No image uri support!")
                    return
                }
                val key = MemoryCache.Key(uri)
                img.load(uri) {
                    memoryCacheKey(key)
                }
                webView.addView(
                    img,
                    @Suppress("DEPRECATION")
                    android.widget.AbsoluteLayout.LayoutParams(
                        rect.width(),
                        rect.height(),
                        rect.left,
                        rect.top
                    )
                )
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
        webView.removeAllViews()
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
        webView.removeAllViews()
    }

    companion object {

        const val TAG = "WebFragment"
    }
}