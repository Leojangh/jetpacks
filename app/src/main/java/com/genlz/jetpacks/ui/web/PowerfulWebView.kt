package com.genlz.jetpacks.ui.web

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.MainThread
import androidx.annotation.StyleRes
import androidx.core.os.HandlerCompat
import androidx.core.view.*
import androidx.core.view.ViewCompat.NestedScrollType
import androidx.core.view.ViewCompat.ScrollAxis

class PowerfulWebView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0
) : WebView(context, attrs, defStyleAttr, defStyleRes),
    ScrollingView,
    NestedScrollingChild2,
    NestedScrollingChild3,
    GestureDetector.OnGestureListener,
    GestureDetector.OnDoubleTapListener
//    CircularRevealWidget
{

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

    private val scrollHelpers = NestedScrollingChildHelper(this)

    var showPressListener: ((MotionEvent) -> Boolean)? = null

    var singleTapListener: ((MotionEvent) -> Boolean)? = null

    var scrollListener: ((MotionEvent, MotionEvent, Float, Float) -> Boolean)? =
        null

    var longPressListener: ((MotionEvent) -> Unit)? = null

    var flingListener: ((MotionEvent, MotionEvent, Float, Float) -> Boolean)? =
        null

    var singleTapConfirmedListener: ((MotionEvent) -> Boolean)? = null

    var doubleTapListener: ((MotionEvent) -> Boolean)? = null

    var doubleTapEventListener: ((MotionEvent) -> Boolean)? = null

    /**
     * The map of file name to script content.
     */
    var scripts: Map<String, String> = HashMap()

//    private val helper = CircularRevealHelper(this)

    init {
        settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
        }
        webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                scripts.values.forEach {
                    evaluateJavascript(it, null)
                }
            }
        }
    }

    override fun computeHorizontalScrollRange(): Int {
        return super.computeHorizontalScrollRange()
    }

    override fun computeHorizontalScrollOffset(): Int {
        return super.computeHorizontalScrollOffset()
    }

    override fun computeHorizontalScrollExtent(): Int {
        return super.computeHorizontalScrollExtent()
    }

    override fun computeVerticalScrollRange(): Int {
        return super.computeVerticalScrollRange()
    }

    override fun computeVerticalScrollOffset(): Int {
        return super.computeVerticalScrollOffset()
    }

    override fun computeVerticalScrollExtent(): Int {
        return super.computeVerticalScrollExtent()
    }

    override fun startNestedScroll(
        @ScrollAxis axes: Int,
        @NestedScrollType type: Int
    ): Boolean {
        Log.d(TAG, "startNestedScroll: $type")
        return scrollHelpers.startNestedScroll(axes, type)
    }

    override fun stopNestedScroll(@NestedScrollType type: Int) {
        Log.d(TAG, "stopNestedScroll: $type")
        scrollHelpers.stopNestedScroll(type)
    }

    override fun hasNestedScrollingParent(@NestedScrollType type: Int): Boolean {
        Log.d(TAG, "hasNestedScrollingParent: $type")
        return scrollHelpers.hasNestedScrollingParent(type)
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
        Log.d(TAG, "dispatchNestedScroll: $type")
        scrollHelpers.dispatchNestedScroll(
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
        Log.d(TAG, "dispatchNestedScroll: $type")
        return scrollHelpers.dispatchNestedScroll(
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
        offsetInWindow: IntArray?,
        @NestedScrollType type: Int
    ): Boolean {
        Log.d(TAG, "dispatchNestedPreScroll: $type")
        return scrollHelpers.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, type)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
//        gestureDetector.onTouchEvent(event)
        return super.onTouchEvent(event)
    }

    companion object {
        private const val TAG = "PowerfulWebView"
    }

    override fun onDown(e: MotionEvent) = true

    override fun onShowPress(e: MotionEvent) {
        Log.d(TAG, "onShowPress: ")
        showPressListener?.invoke(e)
    }

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        Log.d(TAG, "onSingleTapUp: ")
        return singleTapListener?.invoke(e) ?: true
    }

    override fun onScroll(
        e1: MotionEvent,
        e2: MotionEvent,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        Log.d(TAG, "onScroll: ")
        scrollListener?.invoke(e1, e2, distanceX, distanceY)
        startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL, ViewCompat.TYPE_TOUCH)
        return dispatchNestedPreScroll(
            distanceX.toInt(),
            distanceY.toInt(),
            intArrayOf(0, 0),
            null,
            ViewCompat.TYPE_TOUCH
        )
    }

    override fun onLongPress(e: MotionEvent) {
        Log.d(TAG, "onLongPress: ")
        longPressListener?.invoke(e)
    }

    override fun onFling(
        e1: MotionEvent,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        Log.d(TAG, "onFling: ")
        return flingListener?.invoke(e1, e2, velocityX, velocityY) ?: true
    }

    override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
        return singleTapConfirmedListener?.invoke(e) ?: true
    }

    override fun onDoubleTap(e: MotionEvent): Boolean {
        Log.d(TAG, "onDoubleTap: ")
        return doubleTapListener?.invoke(e) ?: true
    }

    override fun onDoubleTapEvent(e: MotionEvent): Boolean {
        Log.d(TAG, "onDoubleTapEvent: ")
        return doubleTapEventListener?.invoke(e) ?: true
    }

//    override fun draw(canvas: Canvas) {
//        if (helper == null)
//            helper.draw(canvas)
//        else
//            super.draw(canvas)
//    }
//
//    override fun actualDraw(canvas: Canvas) {
//        super.draw(canvas)
//    }
//
//    override fun isOpaque(): Boolean {
//        return if (helper == null) super.isOpaque() else helper.isOpaque
//    }
//
//    override fun actualIsOpaque(): Boolean {
//        return super.isOpaque()
//    }
//
//    override fun buildCircularRevealCache() {
//        helper.buildCircularRevealCache()
//    }
//
//    override fun destroyCircularRevealCache() {
//        helper.destroyCircularRevealCache()
//    }
//
//    override fun getRevealInfo(): CircularRevealWidget.RevealInfo? {
//        return helper.revealInfo
//    }
//
//    override fun setRevealInfo(revealInfo: CircularRevealWidget.RevealInfo?) {
//        helper.revealInfo = revealInfo
//    }
//
//    override fun getCircularRevealScrimColor(): Int {
//        return helper.circularRevealScrimColor
//    }
//
//    override fun setCircularRevealScrimColor(color: Int) {
//        helper.circularRevealScrimColor = color
//    }
//
//    override fun getCircularRevealOverlayDrawable(): Drawable? {
//        return helper.circularRevealOverlayDrawable
//    }
//
//    override fun setCircularRevealOverlayDrawable(drawable: Drawable?) {
//        helper.circularRevealOverlayDrawable = drawable
//    }

}