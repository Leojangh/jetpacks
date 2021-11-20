import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.DOMRect
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLImageElement

/**
 * Set click listener that transit the position to Android.
 */
fun setupImageTransition() {
    document.addEventListener("click", {
        val target = it.target as HTMLElement
        (it.target as? HTMLImageElement)?.run {
            if (hasAttribute("data-galleryid")) {
                val rect = target.getBoundingOnDeviceRect()
                Android.transit(
                    rect.left,
                    rect.top,
                    rect.right,
                    rect.bottom
                )
            }
        }
    })
}

fun HTMLElement.getBoundingOnDeviceRect(): Rect {
    //the rect refer to parent.
    val rect = getBoundingClientRect()
    val ratio = window.devicePixelRatio
    return rect.toAndroidRect(ratio)
}

/**
 * Transform the [DOMRect] to Android Rect.
 */
fun DOMRect.toAndroidRect(ratio: Double): Rect {
    val documentElement = document.documentElement!!
    return Rect(
        (left + documentElement.scrollLeft) * ratio,
        (top + documentElement.scrollTop) * ratio,
        (right + documentElement.scrollLeft) * ratio,
        (bottom + documentElement.scrollTop) * ratio
    )
}