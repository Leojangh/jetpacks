import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.DOMRect
import org.w3c.dom.HTMLElement

data class Rect(
    val left: Double,
    val top: Double,
    val right: Double,
    val bottom: Double
)

/**
 * The Javascript bridge supplied by Android
 */
external object Android {
    fun toast(msg: String)
    fun transit(rect: Rect)
}

fun main() {
    document.addEventListener("click", {
        val target = it.target as HTMLElement
        val rect = target.getBoundingOnDeviceRect()
        Android.transit(rect)
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