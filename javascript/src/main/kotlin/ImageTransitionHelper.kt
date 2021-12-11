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
        (it.target as? HTMLImageElement)?.run {
            // Use your own identifier.
            if (hasAttribute("data-galleryid")) {
                val rect = getBoundingOnDeviceRect()
                Android.transit(
                    rect.left,
                    rect.top,
                    rect.right,
                    rect.bottom
                )
            }
        }
    }, /*bubble*/false)
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
        ((left + documentElement.scrollLeft) * ratio).toFloat(),
        ((top + documentElement.scrollTop) * ratio).toFloat(),
        ((right + documentElement.scrollLeft) * ratio).toFloat(),
        ((bottom + documentElement.scrollTop) * ratio).toFloat()
    )
}