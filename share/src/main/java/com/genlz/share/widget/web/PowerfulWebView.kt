package com.genlz.share.widget.web

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.webkit.*
import androidx.annotation.RequiresApi
import androidx.annotation.StyleRes
import androidx.core.view.*
import androidx.core.view.ViewCompat.NestedScrollType
import androidx.core.view.ViewCompat.ScrollAxis
import androidx.core.widget.NestedScrollView
import androidx.webkit.ServiceWorkerClientCompat
import androidx.webkit.ServiceWorkerControllerCompat
import androidx.webkit.WebViewCompat
import androidx.webkit.WebViewFeature
import androidx.webkit.internal.FrameworkServiceWorkerClient
import androidx.webkit.internal.ServiceWorkerClientAdapter
import com.genlz.share.util.appcompat.postVisualStateCallback
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@SuppressLint("SetJavaScriptEnabled")
class PowerfulWebView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0
) : WebView(context, attrs, defStyleAttr, defStyleRes),
    NestedScrollingChild3,
    NestedScrollingParent3,
    GestureDetector.OnGestureListener,
    GestureDetector.OnDoubleTapListener {
    /**
     * When double tap and release,event order:
     *
     * 1.[onSingleTapUp]
     *
     * 2.[onDoubleTap]
     *
     * 3.[onDoubleTapEvent]
     *
     * 4.[onDoubleTapEvent]
     *
     * [onSingleTapUp] is traditional click.
     *
     * When long press,event order:
     *
     * 1.[onShowPress]
     *
     * 2.[onLongPress]
     */
    private val gestureDetector = GestureDetectorCompat(context, this)

    /**
     * Usage refers to [NestedScrollView].
     */
    private val childHelper = NestedScrollingChildHelper(this)

    private val parentHelper = NestedScrollingParentHelper(this)

    var showPressListener: ((MotionEvent) -> Boolean)? = null

    var singleTapListener: ((MotionEvent) -> Boolean)? = null

    var longPressListener: ((MotionEvent) -> Unit)? = null

    var singleTapConfirmedListener: ((MotionEvent) -> Boolean)? = null

    var doubleTapListener: ((MotionEvent) -> Boolean)? = null

    var doubleTapEventListener: ((MotionEvent) -> Boolean)? = null

    var domTouchListener: DomTouchListener? = null

    /**
     * The map of file name to script content.
     */
    val scripts = HashMap<String, String>()

    init {
        settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
        }
        webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                for ((name, content) in scripts) {
                    evaluateJavascript(content, null)
                    Log.d(TAG, "script $name inject done")
                }
            }

            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest?
            ): Boolean = false
        }
        isNestedScrollingEnabled = true
        addJavascriptInterface(com.genlz.share.widget.web.bridge.Log, "Log")
    }

    private fun cache() {
        if (WebViewFeature.isFeatureSupported(WebViewFeature.SERVICE_WORKER_BASIC_USAGE)) {
            val controller = ServiceWorkerControllerCompat.getInstance()
            controller.serviceWorkerWebSettings.apply {
                if (WebViewFeature.isFeatureSupported(WebViewFeature.SERVICE_WORKER_CACHE_MODE)) {
                    this.cacheMode
                }
                if (WebViewFeature.isFeatureSupported(WebViewFeature.SERVICE_WORKER_FILE_ACCESS)) {
                    this.allowFileAccess = true
                }
            }
            controller.setServiceWorkerClient(object : ServiceWorkerClientCompat() {
                override fun shouldInterceptRequest(request: WebResourceRequest): WebResourceResponse? {

                    return null
                }
            })
        }
        postVisualStateCallback(2) {}
    }

    override fun onCheckIsTextEditor(): Boolean {
        return super.onCheckIsTextEditor()
    }

    override fun onCreateInputConnection(outAttrs: EditorInfo): InputConnection {
        return super.onCreateInputConnection(outAttrs)
    }

    override fun startNestedScroll(axes: Int): Boolean {
        return childHelper.startNestedScroll(axes)
    }

    // NestedScrollingChild2
    override fun startNestedScroll(
        @ScrollAxis axes: Int,
        @NestedScrollType type: Int
    ): Boolean {
        return childHelper.startNestedScroll(axes, type)
    }

    override fun stopNestedScroll(@NestedScrollType type: Int) {
        childHelper.stopNestedScroll(type)
    }

    override fun hasNestedScrollingParent(@NestedScrollType type: Int): Boolean {
        return childHelper.hasNestedScrollingParent(type)
    }

    override fun setNestedScrollingEnabled(enabled: Boolean) {
        childHelper.isNestedScrollingEnabled = enabled
    }

    override fun isNestedScrollingEnabled(): Boolean {
        return childHelper.isNestedScrollingEnabled
    }

    // NestedScrollChild3
    override fun dispatchNestedScroll(
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        offsetInWindow: IntArray?
    ): Boolean {
        return childHelper.dispatchNestedScroll(
            dxConsumed,
            dyConsumed,
            dxUnconsumed,
            dyUnconsumed,
            offsetInWindow
        )
    }

    override fun dispatchNestedScroll(
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        offsetInWindow: IntArray?,
        @NestedScrollType type: Int,
        consumed: IntArray
    ) {
        childHelper.dispatchNestedScroll(
            dxConsumed,
            dyConsumed,
            dxUnconsumed,
            dyUnconsumed,
            offsetInWindow,
            type,
            consumed
        )
    }

    override fun dispatchNestedScroll(
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        offsetInWindow: IntArray?,
        @NestedScrollType type: Int
    ): Boolean {
        return childHelper.dispatchNestedScroll(
            dxConsumed,
            dyConsumed,
            dxUnconsumed,
            dyUnconsumed,
            offsetInWindow,
            type
        )
    }

    override fun dispatchNestedPreScroll(
        dx: Int,
        dy: Int,
        consumed: IntArray?,
        offsetInWindow: IntArray?
    ): Boolean {
        return childHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow)
    }

    override fun dispatchNestedFling(
        velocityX: Float,
        velocityY: Float,
        consumed: Boolean
    ): Boolean {
        return childHelper.dispatchNestedFling(velocityX, velocityY, consumed)
    }

    override fun dispatchNestedPreFling(velocityX: Float, velocityY: Float): Boolean {
        return childHelper.dispatchNestedPreFling(velocityX, velocityY)
    }

    override fun dispatchNestedPreScroll(
        dx: Int,
        dy: Int,
        consumed: IntArray?,
        offsetInWindow: IntArray?,
        @NestedScrollType type: Int
    ): Boolean {
        return childHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, type)
    }

    // NestedScrollingParent3
    override fun onStartNestedScroll(child: View, target: View, axes: Int, type: Int): Boolean {
        return axes and ViewCompat.SCROLL_AXIS_VERTICAL != 0
    }

    override fun onNestedScrollAccepted(child: View, target: View, axes: Int, type: Int) {
        parentHelper.onNestedScrollAccepted(child, target, axes, type)
        startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL, type)
    }

    override fun onStopNestedScroll(target: View, type: Int) {
        parentHelper.onStopNestedScroll(target, type)
        stopNestedScroll(type)
    }

    override fun stopNestedScroll() {
        childHelper.stopNestedScroll()
    }

    override fun onStopNestedScroll(child: View) {
        childHelper.onStopNestedScroll(child)
    }

    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int,
        consumed: IntArray
    ) {
        onNestedScrollInternal(dyUnconsumed, type, consumed)
    }

    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int
    ) {
        onNestedScrollInternal(dyUnconsumed, type, null)
    }

    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int
    ) {
        onNestedScrollInternal(dyUnconsumed, ViewCompat.TYPE_TOUCH, null)
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        dispatchNestedPreScroll(dx, dy, consumed, null, type)
    }

    override fun getNestedScrollAxes(): Int {
        return parentHelper.nestedScrollAxes
    }

    /**
     * Copy from [NestedScrollView.onNestedScrollInternal]
     */
    private fun onNestedScrollInternal(dyUnconsumed: Int, type: Int, consumed: IntArray?) {
        val oldScrollY = scrollY
        scrollBy(0, dyUnconsumed)
        val myConsumed = scrollY - oldScrollY
        consumed?.apply {
            this[1] += myConsumed
        }
        val myUnconsumed = dyUnconsumed - myConsumed
        childHelper.dispatchNestedScroll(0, myConsumed, 0, myUnconsumed, null, type, consumed)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        gestureDetector.onTouchEvent(event)
        return super.onTouchEvent(event)
    }

    companion object {
        private const val TAG = "PowerfulWebView"

        /**
         * Convert the callback style to kotlin coroutine.
         */
        suspend fun WebView.evaluateJavascript(script: String): String {
            return suspendCoroutine { cont ->
                evaluateJavascript(script) {
                    cont.resume(it)
                }
            }
        }
    }

    // Gesture detector
    override fun onDown(e: MotionEvent) = true

    override fun onShowPress(e: MotionEvent) {
        showPressListener?.invoke(e)
    }

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        performClick()
        return singleTapListener?.invoke(e) ?: false
    }

    override fun onScroll(
        e1: MotionEvent,
        e2: MotionEvent,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        dispatchNestedPreScroll(
            distanceX.toInt(),
            distanceY.toInt(),
            intArrayOf(0, 0),
            null,
            ViewCompat.TYPE_TOUCH
        )
        return startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL, ViewCompat.TYPE_TOUCH)
    }

    override fun onLongPress(e: MotionEvent) {
        longPressListener?.invoke(e)
    }

    override fun onFling(
        e1: MotionEvent,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {

        return dispatchNestedFling(velocityX, velocityY, false)
    }

    override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
        return singleTapConfirmedListener?.invoke(e) ?: false
    }

    override fun onDoubleTap(e: MotionEvent): Boolean {
        return doubleTapListener?.invoke(e) ?: false
    }

    override fun onDoubleTapEvent(e: MotionEvent): Boolean {
        return doubleTapEventListener?.invoke(e) ?: false
    }
}