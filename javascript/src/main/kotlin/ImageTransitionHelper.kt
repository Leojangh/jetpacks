import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.DOMRect
import org.w3c.dom.HTMLElement

/**
 * Set click listener that transit the position to Android.
 */
fun setupImageTransition() {
    document.addEventListener("click", {
        val target = it.target as HTMLElement
        val rect = target.getBoundingOnDeviceRect()
        Android.transit(
            rect.left,
            rect.top,
            rect.right,
            rect.bottom
        )
    })
}

fun HTMLElement.getBoundingOnDeviceRect(): Rect {
    val rect = getBoundingClientRect()
    val ratio = window.devicePixelRatio
    return rect.toAndroidRect(ratio)
}

fun DOMRect.toAndroidRect(ratio: Double) = Rect(
    left * ratio,
    top * ratio,
    right * ratio,
    bottom * ratio
)