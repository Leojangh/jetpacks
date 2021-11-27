import kotlinx.browser.window
import org.w3c.fetch.Response
import org.w3c.workers.ExtendableEvent
import org.w3c.workers.FetchEvent
import org.w3c.workers.RegistrationOptions
import kotlin.js.Promise

fun setupCache() {
    window.navigator.serviceWorker.register("file://sw.js", RegistrationOptions(scope = "/"))
        .then { reg ->
            console.log("Registration succeeded. Scope is ${reg.scope}")
        }.catch { t ->
            console.log("Registration failed with $t");
        }
    
    window.self.addEventListener("install", { e ->
        (e as ExtendableEvent).waitUntil(
            window.caches.open("v1").then {
                it.addAll(
                    arrayOf(

                    )
                )
            }
        )
    })

    window.self.addEventListener("fetch", { e ->
        with(e as FetchEvent) {
            respondWith(window.caches.match(request).unsafeCast<Promise<Response>>())
        }
    })
}