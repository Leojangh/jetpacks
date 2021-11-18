import kotlinx.browser.document
import kotlinx.dom.createElement

fun main() {
    setupImageTransition()
    injectJQuery()
}

/**
 * Inject JQuery.
 */
fun injectJQuery() {
    val elementId = "jQuery"
    if (document.getElementById(elementId) == null) {
        val jQ = document.createElement("script") {
            setAttribute("src", "https://apps.bdimg.com/libs/jquery/2.1.4/jquery.min.js")
            setAttribute("id", elementId)
        }
        document.head?.appendChild(jQ)
    }
}