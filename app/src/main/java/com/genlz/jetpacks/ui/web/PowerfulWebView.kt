package com.genlz.jetpacks.ui.web

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.StyleRes
import androidx.core.os.bundleOf
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.NestedScrollingChild2
import androidx.core.view.NestedScrollingChild3
import androidx.navigation.findNavController
import com.genlz.jetpacks.R
import com.genlz.jetpacks.ui.BottomDialogFragmentArgs
import com.genlz.jetpacks.utility.appcompat.*
import com.google.android.material.bottomsheet.BottomSheetDialog

class PowerfulWebView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0
) : WebView(context, attrs, defStyleAttr, defStyleRes),
    NestedScrollingChild2,
    NestedScrollingChild3,
    GestureDetector.OnGestureListener,
    GestureDetector.OnDoubleTapListener {

    private val gestureDetector = GestureDetectorCompat(context, this)

    var scripts: Set<String> = HashSet()

    init {
        settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
        }
        webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                scripts.forEach {
                    evaluateJavascript(it, null)
                }
            }
        }
    }

    override fun startNestedScroll(axes: Int, type: Int): Boolean {
        Log.d(TAG, "startNestedScroll: $type")
        return startNestedScrollExt(axes)
    }

    override fun stopNestedScroll(type: Int) {
        Log.d(TAG, "stopNestedScroll: $type")
        stopNestedScrollExt()
    }

    override fun hasNestedScrollingParent(type: Int): Boolean {
        Log.d(TAG, "hasNestedScrollingParent: $type")
        return hasNestedScrollingParent
    }

    override fun dispatchNestedScroll(
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        offsetInWindow: IntArray?,
        type: Int,
        consumed: IntArray
    ) {
        Log.d(TAG, "dispatchNestedScroll: $type")
        dispatchNestedScrollExt(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow)
    }

    override fun dispatchNestedScroll(
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        offsetInWindow: IntArray?,
        type: Int
    ): Boolean {
        Log.d(TAG, "dispatchNestedScroll: $type")
        return dispatchNestedScrollExt(
            dxConsumed,
            dyConsumed,
            dxUnconsumed,
            dyUnconsumed,
            offsetInWindow
        )
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        gestureDetector.onTouchEvent(event)
        return super.onTouchEvent(event)
    }

    override fun dispatchNestedPreScroll(
        dx: Int,
        dy: Int,
        consumed: IntArray?,
        offsetInWindow: IntArray?,
        type: Int
    ): Boolean {
        Log.d(TAG, "dispatchNestedPreScroll: $type")
        return dispatchNestedPreScrollExt(dx, dy, consumed, offsetInWindow)
    }

    companion object {
        private const val TAG = "PowerfulWebView"
    }

    override fun onDown(e: MotionEvent) = true

    override fun onShowPress(e: MotionEvent) {
        Log.d(TAG, "onShowPress: ")
    }

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        Log.d(TAG, "onSingleTapUp: ")
        return true
    }

    var scrollListener: ((MotionEvent, MotionEvent, Float, Float) -> Boolean)? = null
    override fun onScroll(
        e1: MotionEvent,
        e2: MotionEvent,
        distanceX: Float,
        distanceY: Float
    ) = scrollListener?.invoke(e1, e2, distanceX, distanceY) ?: true

    var longPressListener: ((MotionEvent) -> Unit)? = null

    override fun onLongPress(e: MotionEvent) {
        longPressListener?.invoke(e)
    }

    var flingListener: ((MotionEvent, MotionEvent, Float, Float) -> Boolean)? = null

    override fun onFling(
        e1: MotionEvent,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ) = flingListener?.invoke(e1, e2, velocityX, velocityY) ?: true

    var singleTapConfirmedListener: ((MotionEvent) -> Boolean)? = null

    override fun onSingleTapConfirmed(e: MotionEvent) =
        singleTapConfirmedListener?.invoke(e) ?: true

    var doubleTapListener: ((MotionEvent) -> Boolean)? = null

    override fun onDoubleTap(e: MotionEvent) = doubleTapListener?.invoke(e) ?: true

    var doubleTapEventListener: ((MotionEvent) -> Boolean)? = null
    override fun onDoubleTapEvent(e: MotionEvent) = doubleTapEventListener?.invoke(e) ?: true
}