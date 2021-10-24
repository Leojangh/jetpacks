@file:Suppress("NOTHING_TO_INLINE", "UNUSED") // Aliases to other public API.

package com.genlz.jetpacks.utility.appcompat

import android.content.ClipData
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import androidx.annotation.IdRes
import androidx.annotation.Px
import androidx.annotation.StyleableRes
import androidx.annotation.UiThread
import androidx.core.view.*
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import androidx.core.view.accessibility.AccessibilityNodeProviderCompat
import androidx.core.view.accessibility.AccessibilityViewCommand

inline fun View.updateMargin(
    @Px left: Int = marginLeft,
    @Px top: Int = marginTop,
    @Px right: Int = marginRight,
    @Px bottom: Int = marginBottom
) {
    updateLayoutParams<ViewGroup.MarginLayoutParams> {
        leftMargin = left
        topMargin = top
        rightMargin = right
        bottomMargin = bottom
    }
}

/**
 * A Quadruple form to record paddings or margins.
 */
data class EdgeDistance(
    val left: Int,
    val top: Int,
    val right: Int,
    val bottom: Int
)
typealias Paddings = EdgeDistance
typealias Margins = EdgeDistance

/**
 * Extension from [OnApplyWindowInsetsListener],help to remember the initial paddings.
 *
 * @see OnApplyWindowInsetsListener
 * @see ViewCompat.setOnApplyWindowInsetsListener
 */
inline fun View.setOnApplyWindowInsetsListener(
    crossinline listener: (View, WindowInsetsCompat, Paddings) -> WindowInsetsCompat
) {
    val initialPadding = Paddings(paddingLeft, paddingTop, paddingRight, paddingBottom)
    ViewCompat.setOnApplyWindowInsetsListener(this) { v, i ->
        listener(v, i, initialPadding)
    }
}

/**
 * @see ViewCompat.saveAttributeDataForStyleable
 */
inline fun View.saveAttributeDataForStyleableExt(
    context: Context,
    @StyleableRes styleable: IntArray,
    attrs: AttributeSet?,
    typedArray: TypedArray,
    defStyleAttrs: Int,
    defStyleRes: Int
) {
    ViewCompat.saveAttributeDataForStyleable(
        this,
        context,
        styleable,
        attrs,
        typedArray,
        defStyleAttrs,
        defStyleRes
    )
}

/**
 * @see ViewCompat.onInitializeAccessibilityNodeInfo
 */
inline fun View.onInitializeAccessibilityNodeInfoExt(
    info: AccessibilityNodeInfoCompat
) = ViewCompat.onInitializeAccessibilityNodeInfo(this, info)

/**
 * @see ViewCompat.setAccessibilityDelegate
 */
inline fun View.setAccessibilityDelegateExt(delegate: AccessibilityDelegateCompat) =
    ViewCompat.setAccessibilityDelegate(this, delegate)

/**
 * @see ViewCompat.setAutofillHints
 */
inline fun View.setAutofillHintsExt(vararg autofillHints: String) =
    ViewCompat.setAutofillHints(this, *autofillHints)

/**
 * @see ViewCompat.getImportantForAutofill
 * @see ViewCompat.setImportantForAutofill
 */
inline var View.importantForAutofillExt
    get() = ViewCompat.getImportantForAutofill(this)
    set(value) {
        ViewCompat.setImportantForAutofill(this, value)
    }

/**
 * @see ViewCompat.isImportantForAutofill
 */
inline val View.isImportantForAutofillExt get() = ViewCompat.isImportantForAutofill(this)

/**
 * @see ViewCompat.hasAccessibilityDelegate
 */
inline val View.hasAccessibilityDelegate get() = ViewCompat.hasAccessibilityDelegate(this)

/**
 * @see ViewCompat.getAccessibilityDelegate
 */
inline val View.accessibilityDelegateExt get() = ViewCompat.getAccessibilityDelegate(this)

/**
 * @see ViewCompat.hasTransientState
 * @see ViewCompat.setHasTransientState
 */
inline var View.hasTransientState
    get() = ViewCompat.hasTransientState(this)
    set(value) {
        ViewCompat.setHasTransientState(this, value)
    }

/**
 * @see ViewCompat.postInvalidateOnAnimation
 */
inline fun View.postInvalidateOnAnimationExt() = ViewCompat.postInvalidateOnAnimation(this)

/**
 * @see ViewCompat.postInvalidateOnAnimation
 */
inline fun View.postInvalidateOnAnimationExt(left: Int, top: Int, right: Int, bottom: Int) =
    ViewCompat.postInvalidateOnAnimation(this, left, top, right, bottom)

/**
 * @see ViewCompat.postOnAnimation
 */
inline fun View.postOnAnimationExt(action: Runnable) = ViewCompat.postOnAnimation(this, action)


/**
 * Make you can place lambda out of parentheses.
 *
 *@see ViewCompat.postOnAnimationDelayed
 */
inline fun View.postOnAnimationDelayed(delayMillis: Long, action: Runnable) =
    ViewCompat.postOnAnimationDelayed(this, action, delayMillis)

/**
 * @see ViewCompat.setImportantForAccessibility
 * @see ViewCompat.getImportantForAccessibility
 */
inline var View.importantForAccessibilityExt
    get() = ViewCompat.getImportantForAccessibility(this)
    set(value) {
        ViewCompat.setImportantForAccessibility(this, value)
    }

/**
 * @see ViewCompat.isImportantForAccessibility
 */
inline val View.isImportantForAccessibilityExt get() = ViewCompat.isImportantForAccessibility(this)

/**
 * @see ViewCompat.performAccessibilityAction
 */
inline fun View.performAccessibilityActionExt(action: Int, args: Bundle) =
    ViewCompat.performAccessibilityAction(this, action, args)

/**
 * @see ViewCompat.addAccessibilityAction
 */
inline fun View.addAccessibilityAction(label: CharSequence, command: AccessibilityViewCommand) =
    ViewCompat.addAccessibilityAction(this, label, command)

/**
 * @see ViewCompat.replaceAccessibilityAction
 */
inline fun View.replaceAccessibilityAction(
    replacedAction: AccessibilityNodeInfoCompat.AccessibilityActionCompat,
    label: CharSequence?,
    command: AccessibilityViewCommand?
) = ViewCompat.replaceAccessibilityAction(this, replacedAction, label, command)

/**
 * @see ViewCompat.removeAccessibilityAction
 */
inline fun View.removeAccessibilityAction(action: Int) =
    ViewCompat.removeAccessibilityAction(this, action)

/**
 * @see ViewCompat.setStateDescription
 * @see ViewCompat.getStateDescription
 */
inline var View.setStateDescription
    @UiThread get() = ViewCompat.getStateDescription(this)
    @UiThread set(value) = ViewCompat.setStateDescription(this, value)


/**
 * @see ViewCompat.enableAccessibleClickableSpanSupport
 */
inline fun View.enableAccessibleClickableSpanSupport() =
    ViewCompat.enableAccessibleClickableSpanSupport(this)

/**
 * @see ViewCompat.getAccessibilityNodeProvider
 */
inline val View.accessibilityNodeProviderExt: AccessibilityNodeProviderCompat?
    get() = ViewCompat.getAccessibilityNodeProvider(this)

/**
 * @see ViewCompat.setLabelFor
 * @see ViewCompat.getLabelFor
 */
inline var View.labelForExt
    get() = ViewCompat.getLabelFor(this)
    set(value) = ViewCompat.setLabelFor(this, value)

/**
 * @see ViewCompat.setLayerPaint
 */
inline fun View.setLayerPaintExt(paint: Paint) = ViewCompat.setLayerPaint(this, paint)

/**
 * @see ViewCompat.setLayoutDirection
 * @see ViewCompat.getLayoutDirection
 */
inline var View.layoutDirectionExt
    get() = ViewCompat.getLayoutDirection(this)
    set(value) = ViewCompat.setLayoutDirection(this, value)

/**
 * @see ViewCompat.getParentForAccessibility
 */
inline fun View.getParentForAccessibilityExt(): ViewParent =
    ViewCompat.getParentForAccessibility(this)

/**
 * Remove redundant logic(API level judgement).
 * @see ViewCompat.requireViewById
 */
inline fun <T : View> View.requireViewByIdExt(@IdRes id: Int) = findViewById<T>(id)
    ?: throw IllegalArgumentException("ID does not reference a View inside this View")

/**
 * @see ViewCompat.setAccessibilityLiveRegion
 * @see ViewCompat.getAccessibilityLiveRegion
 */
inline var View.accessibilityLiveRegionExt
    get() = ViewCompat.getAccessibilityLiveRegion(this)
    set(value) = ViewCompat.setAccessibilityLiveRegion(this, value)

/**
 * @see ViewCompat.getPaddingStart
 */
inline val View.paddingStartExt
    @Px get() = ViewCompat.getPaddingStart(this)

/**
 * @see ViewCompat.getPaddingEnd
 */
inline val View.paddingEndExt
    @Px get() = ViewCompat.getPaddingEnd(this)

/**
 * @see ViewCompat.dispatchStartTemporaryDetach
 */
inline fun View.dispatchStartTemporaryDetachExt() = ViewCompat.dispatchStartTemporaryDetach(this)

/**
 * @see ViewCompat.dispatchFinishTemporaryDetach
 */
inline fun View.dispatchFinishTemporaryDetachExt() = ViewCompat.dispatchFinishTemporaryDetach(this)

/**
 * @see ViewCompat.getMinimumWidth
 */
inline val View.minimumWidthExt get() = ViewCompat.getMinimumWidth(this)

/**
 * @see ViewCompat.getMinimumHeight
 */
inline val View.minimumHeightExt get() = ViewCompat.getMinimumHeight(this)

/**
 * @see ViewCompat.animate
 */
inline fun View.animateExt() = ViewCompat.animate(this)

/**
 * @see ViewCompat.setElevation
 * @see ViewCompat.getElevation
 */
inline var View.elevationExt
    get() = ViewCompat.getElevation(this)
    set(value) = ViewCompat.setElevation(this, value)

/**
 * @see ViewCompat.setTranslationZ
 * @see ViewCompat.getTranslationZ
 */
inline var View.transitionZExt
    get() = ViewCompat.getTranslationZ(this)
    set(value) = ViewCompat.setTranslationZ(this, value)

/**
 * @see ViewCompat.setTransitionName
 * @see ViewCompat.getTransitionName
 */
inline var View.setTransitionNameExt
    get() = ViewCompat.getTransitionName(this)
    set(value) = ViewCompat.setTransitionName(this, value)

/**
 * @see ViewCompat.getWindowSystemUiVisibility
 */
inline val View.windowSystemUiVisibilityExt get() = ViewCompat.getWindowSystemUiVisibility(this)

/**
 * @see ViewCompat.requestApplyInsets
 */
inline fun View.requestApplyInsetsExt() = ViewCompat.requestApplyInsets(this)

/**
 * @see ViewCompat.getFitsSystemWindows
 * @see ViewCompat.setFitsSystemWindows
 */
inline var View.fitsSystemWindowsExt: Boolean
    get() = ViewCompat.getFitsSystemWindows(this)
    set(value) {
        fitsSystemWindows = value
    }

/**
 * @see ViewCompat.onApplyWindowInsets
 */
inline fun View.onApplyWindowInsets(insets: WindowInsetsCompat) =
    ViewCompat.onApplyWindowInsets(this, insets)

/**
 * @see ViewCompat.dispatchApplyWindowInsets
 */
inline fun View.dispatchApplyWindowInsets(insets: WindowInsetsCompat) =
    ViewCompat.dispatchApplyWindowInsets(this, insets)

/**
 * @see ViewCompat.getSystemGestureExclusionRects
 * @see ViewCompat.setSystemGestureExclusionRects
 */
inline var View.setSystemGestureExclusionRectsExt: List<Rect>
    get() = ViewCompat.getSystemGestureExclusionRects(this)
    set(value) = ViewCompat.setSystemGestureExclusionRects(this, value)

/**
 * @see ViewCompat.getRootWindowInsets
 */
inline val View.rootWindowInsetsExt get() = ViewCompat.getRootWindowInsets(this)

/**
 * @see ViewCompat.computeSystemWindowInsets
 */
inline fun View.computeSystemWindowInsets(insets: WindowInsetsCompat, outLocalInsets: Rect) =
    ViewCompat.computeSystemWindowInsets(this, insets, outLocalInsets)

/**
 * @see ViewCompat.getWindowInsetsController
 */
inline val View.windowInsetsControllerExt get() = ViewCompat.getWindowInsetsController(this)

/**
 * @see ViewCompat.setWindowInsetsAnimationCallback
 */
inline fun View.setWindowInsetsAnimationCallback(cb: WindowInsetsAnimationCompat.Callback?) =
    ViewCompat.setWindowInsetsAnimationCallback(this, cb)

/**
 * @see ViewCompat.setOnReceiveContentListener
 */
inline fun View.setOnReceiveContentListener(
    mineTypes: Array<String>?,
    listener: OnReceiveContentListener?
) = ViewCompat.setOnReceiveContentListener(this, mineTypes, listener)

/**
 * @see ViewCompat.getOnReceiveContentMimeTypes
 */
inline val View.onReceiveContentMimeTypes: Array<String>?
    get() = ViewCompat.getOnReceiveContentMimeTypes(this)

/**
 * @see ViewCompat.performReceiveContent
 */
inline fun View.performReceiveContent(payload: ContentInfoCompat) =
    ViewCompat.performReceiveContent(this, payload)

/**
 * @see ViewCompat.hasOverlappingRendering
 */
inline val View.hasOverlappingRenderingExt get() = ViewCompat.hasOverlappingRendering(this)

/**
 * @see ViewCompat.isPaddingRelative
 */
inline val View.isPaddingRelativeExt get() = ViewCompat.isPaddingRelative(this)

/**
 * @see ViewCompat.setBackground
 */
inline fun View.setBackgroundExt(background: Drawable?) = ViewCompat.setBackground(this, background)

/**
 * @see ViewCompat.setBackgroundTintList
 * @see ViewCompat.getBackgroundTintList
 */
inline var View.backgroundTintListExt: ColorStateList
    get() = ViewCompat.getBackgroundTintList(this)
    set(value) = ViewCompat.setBackgroundTintList(this, value)

/**
 * @see ViewCompat.setBackgroundTintMode
 * @see ViewCompat.getBackgroundTintMode
 */
inline var View.backgroundTintModeExt: PorterDuff.Mode
    get() = ViewCompat.getBackgroundTintMode(this)
    set(value) = ViewCompat.setBackgroundTintMode(this, value)

/**
 * @see ViewCompat.setNestedScrollingEnabled
 * @see ViewCompat.isNestedScrollingEnabled
 */
inline var View.isNestedScrollingEnable
    get() = ViewCompat.isNestedScrollingEnabled(this)
    set(value) = ViewCompat.setNestedScrollingEnabled(this, value)

/**
 * @see ViewCompat.startNestedScroll
 */
inline fun View.startNestedScrollExt(
    @ViewCompat.ScrollAxis axis: Int
) = ViewCompat.startNestedScroll(this, axis)

/**
 * @see ViewCompat.stopNestedScroll
 */
inline fun View.stopNestedScrollExt() = ViewCompat.stopNestedScroll(this)

/**
 * @see ViewCompat.hasNestedScrollingParent
 */
inline val View.hasNestedScrollingParent get() = ViewCompat.hasNestedScrollingParent(this)

/**
 * @see ViewCompat.dispatchNestedScroll
 */
inline fun View.dispatchNestedScrollExt(
    dxConsumed: Int,
    dyConsumed: Int,
    dxUnConsumed: Int,
    dyUnconsumed: Int,
    offsetInWindow: IntArray?
) = ViewCompat.dispatchNestedScroll(
    this,
    dxConsumed,
    dyConsumed,
    dxUnConsumed,
    dyUnconsumed,
    offsetInWindow
)

/**
 * @see ViewCompat.dispatchNestedPreScroll
 */
inline fun View.dispatchNestedPreScrollExt(
    dx: Int, dy: Int, consumed: IntArray?, offsetInWindow: IntArray?
) = ViewCompat.dispatchNestedPreScroll(this, dx, dy, consumed, offsetInWindow)

/**
 * @see ViewCompat.startNestedScroll that overloads three parameters.
 */
inline fun View.startNestedScroll(
    @ViewCompat.ScrollAxis axes: Int,
    @ViewCompat.NestedScrollType type: Int
) = ViewCompat.startNestedScroll(this, axes, type)

/**
 * @see ViewCompat.stopNestedScroll
 */
inline fun View.stopNestedScroll(
    @ViewCompat.NestedScrollType type: Int
) = ViewCompat.stopNestedScroll(this, type)

/**
 * @see ViewCompat.hasNestedScrollingParent
 */
inline fun View.hasNestedScrollingParent(
    @ViewCompat.NestedScrollType type: Int
) = ViewCompat.hasNestedScrollingParent(this, type)

/**
 * @see ViewCompat.dispatchNestedScroll
 */
inline fun View.dispatchNestedScroll(
    dxConsumed: Int,
    dyConsumed: Int,
    dxUnconsumed: Int,
    dyUnconsumed: Int,
    offsetInWindow: IntArray?,
    @ViewCompat.NestedScrollType type: Int,
    consumed: IntArray
) = ViewCompat.dispatchNestedScroll(
    this,
    dxConsumed,
    dyConsumed,
    dxUnconsumed,
    dyUnconsumed,
    offsetInWindow,
    type,
    consumed
)

/**
 * @see ViewCompat.dispatchNestedScroll
 */
inline fun View.dispatchNestedScroll(
    dxConsumed: Int,
    dyConsumed: Int,
    dxUnconsumed: Int,
    dyUnconsumed: Int,
    offsetInWindow: IntArray?,
    @ViewCompat.NestedScrollType type: Int
) = ViewCompat.dispatchNestedScroll(
    this,
    dxConsumed,
    dyConsumed,
    dxUnconsumed,
    dyUnconsumed,
    offsetInWindow,
    type
)

/**
 * @see ViewCompat.dispatchNestedPreScroll
 */
inline fun View.dispatchNestedPreScroll(
    dx: Int,
    dy: Int,
    consumed: IntArray?,
    offsetInWindow: IntArray?,
    @ViewCompat.NestedScrollType type: Int
) = ViewCompat.dispatchNestedPreScroll(this, dx, dy, consumed, offsetInWindow, type)

/**
 * @see ViewCompat.dispatchNestedFling
 */
inline fun View.dispatchNestedFlingExt(
    velocityX: Float,
    velocityY: Float,
    consumed: Boolean
) = ViewCompat.dispatchNestedFling(this, velocityX, velocityY, consumed)

/**
 * @see ViewCompat.dispatchNestedPreFling
 */
inline fun View.dispatchNestedPreFlingExt(
    velocityX: Float,
    velocityY: Float
) = ViewCompat.dispatchNestedPreFling(this, velocityX, velocityY)

/**
 * @see ViewCompat.isInLayout
 */
inline val View.isInLayoutExt get() = ViewCompat.isInLayout(this)

/**
 * @see ViewCompat.isLaidOut
 */
inline val View.isLaidOutExt get() = ViewCompat.isLaidOut(this)

/**
 * @see ViewCompat.isLayoutDirectionResolved
 */
inline val View.isLayoutDirectionResolvedExt get() = ViewCompat.isLayoutDirectionResolved(this)

/**
 * @see ViewCompat.setZ
 * @see ViewCompat.getZ
 */
inline var View.zExt
    get() = ViewCompat.getZ(this)
    set(value) = ViewCompat.setZ(this, value)

/**
 * @see ViewCompat.offsetTopAndBottom
 */
inline fun View.offsetTopAndBottomExt(@Px offset: Int) = ViewCompat.offsetTopAndBottom(this, offset)

/**
 * @see ViewCompat.offsetLeftAndRight
 */
inline fun View.offsetLeftAndRightExt(@Px offset: Int) = ViewCompat.offsetLeftAndRight(this, offset)

/**
 * @see ViewCompat.setClipBounds
 * @see ViewCompat.getClipBounds
 */
inline var View.clipBoundsExt
    get() = ViewCompat.getClipBounds(this)
    set(value) = ViewCompat.setClipBounds(this, value)

/**
 * @see ViewCompat.isAttachedToWindow
 */
inline val View.isAttachedToWindowExt get() = ViewCompat.isAttachedToWindow(this)

/**
 * @see ViewCompat.hasOnClickListeners
 */
inline val View.hasOnClickListener get() = ViewCompat.hasOnClickListeners(this)

/**
 * @see ViewCompat.setScrollIndicators
 */
@ViewCompat.ScrollIndicators
inline var View.setScrollIndicatorsExt
    set(value) = ViewCompat.setScrollIndicators(this, value)
    get() = ViewCompat.getScrollIndicators(this)

/**
 * @see ViewCompat.setScrollIndicators
 */
inline fun View.setScrollIndicatorsExt(
    @ViewCompat.ScrollIndicators indicators: Int,
    @ViewCompat.ScrollIndicators mask: Int
) = ViewCompat.setScrollIndicators(this, indicators, mask)

/**
 * @see ViewCompat.setPointerIcon
 */
inline fun View.setPointerIconExt(pointerIconCompat: PointerIconCompat) =
    ViewCompat.setPointerIcon(this, pointerIconCompat)

/**
 * @see ViewCompat.getDisplay
 */
inline val View.displayExt get() = ViewCompat.getDisplay(this)

/**
 * @see ViewCompat.setTooltipText
 */
inline fun View.setTooltipTextExt(tips: CharSequence?) = ViewCompat.setTooltipText(this, tips)

/**
 * @see ViewCompat.startDragAndDrop
 */
inline fun View.startDragAndDropExt(
    data: ClipData,
    shadowBuilder: View.DragShadowBuilder,
    localState: Any,
    flags: Int
) = ViewCompat.startDragAndDrop(this, data, shadowBuilder, localState, flags)

/**
 * @see ViewCompat.cancelDragAndDrop
 */
inline fun View.cancelDragAndDropExt() = ViewCompat.cancelDragAndDrop(this)

/**
 * @see ViewCompat.updateDragShadow
 */
inline fun View.updateDragShadowExt(shadowBuilder: View.DragShadowBuilder) =
    ViewCompat.updateDragShadow(this, shadowBuilder)

/**
 * @see ViewCompat.setNextClusterForwardId
 * @see ViewCompat.getNextClusterForwardId
 */
inline var View.nextClusterForwardIdExt
    get() = ViewCompat.getNextClusterForwardId(this)
    set(value) = ViewCompat.setNextClusterForwardId(this, value)

/**
 * @see ViewCompat.setKeyboardNavigationCluster
 * @see ViewCompat.isKeyboardNavigationCluster
 */
inline var View.isKeyboardNavigationClusterExt
    get() = ViewCompat.isKeyboardNavigationCluster(this)
    set(value) = ViewCompat.setKeyboardNavigationCluster(this, value)

/**
 * @see ViewCompat.setFocusedByDefault
 * @see ViewCompat.isFocusedByDefault
 */
inline var View.isFocusedByDefaultExt
    get() = ViewCompat.isFocusedByDefault(this)
    set(value) = ViewCompat.setFocusedByDefault(this, value)

/**
 * @see ViewCompat.keyboardNavigationClusterSearch
 */
inline fun View.keyboardNavigationClusterSearchExt(
    currentCluster: View,
    @ViewCompat.FocusDirection direction: Int
): View? = ViewCompat.keyboardNavigationClusterSearch(this, currentCluster, direction)

/**
 * @see ViewCompat.addKeyboardNavigationClusters
 */
inline fun View.addKeyboardNavigationClustersExt(
    views: Collection<View>,
    direction: Int
) = ViewCompat.addKeyboardNavigationClusters(this, views, direction)

/**
 * @see ViewCompat.restoreDefaultFocus
 */
inline fun View.restoreDefaultFocusExt() = ViewCompat.restoreDefaultFocus(this)

/**
 * @see ViewCompat.hasExplicitFocusable
 */
inline val View.hasExplicitFocusable get() = ViewCompat.hasExplicitFocusable(this)

/**
 * @see ViewCompat.addOnUnhandledKeyEventListener
 */
inline fun View.addOnUnhandledKeyEventListener(listener: ViewCompat.OnUnhandledKeyEventListenerCompat) =
    ViewCompat.addOnUnhandledKeyEventListener(this, listener)

/**
 * @see ViewCompat.removeOnUnhandledKeyEventListener
 */
inline fun View.removeOnUnhandledKeyEventListener(
    listener: ViewCompat.OnUnhandledKeyEventListenerCompat
) = ViewCompat.removeOnUnhandledKeyEventListener(this, listener)

/**
 * @see ViewCompat.setScreenReaderFocusable
 * @see ViewCompat.isScreenReaderFocusable
 */
inline var View.setScreenReaderFocusableExt
    @UiThread set(value) = ViewCompat.setScreenReaderFocusable(this, value)
    @UiThread get() = ViewCompat.isScreenReaderFocusable(this)

/**
 * @see ViewCompat.setAccessibilityPaneTitle
 * @see ViewCompat.getAccessibilityPaneTitle
 */
inline var View.accessibilityPaneTitleExt: CharSequence?
    @UiThread get() = ViewCompat.getAccessibilityPaneTitle(this)
    @UiThread set(value) = ViewCompat.setAccessibilityPaneTitle(this, value)

/**
 * @see ViewCompat.setAccessibilityHeading
 * @see ViewCompat.isAccessibilityHeading
 */
inline var View.accessibilityHeading
    @UiThread get() = ViewCompat.isAccessibilityHeading(this)
    @UiThread set(value) = ViewCompat.setAccessibilityHeading(this, value)


inline var View.foregroundExt: Drawable?
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) foreground else null
    set(value) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            foreground = value
        }
    }