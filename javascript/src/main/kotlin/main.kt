import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.HTMLImageElement
import org.w3c.dom.get
import org.w3c.fetch.Response
import org.w3c.workers.*
import kotlin.js.Promise

private const val TAG = "App"

fun main() {
    setupImageTransition()
    injectJQuery()
    setupCache()
}