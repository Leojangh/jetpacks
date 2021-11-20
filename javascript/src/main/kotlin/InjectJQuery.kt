import kotlinx.browser.document
import kotlinx.dom.createElement
import org.w3c.dom.HTMLScriptElement

/**
 * Inject JQuery.
 */
fun injectJQuery() {
    val elementId = "jQuery"
    if (document.getElementById(elementId) == null) {
        val jQ = document.createElement("script") {
            with(this as HTMLScriptElement) {
                src = "https://apps.bdimg.com/libs/jquery/2.1.4/jquery.min.js"
                id = elementId
                type = "text/javascript"
                onerror = { _, _, _, _, _ ->
                    throw Exception("load error")
                }
            }
        }
        document.head?.appendChild(jQ)
    }
}