
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.asPromise
import kotlinx.coroutines.async
import kotlin.js.Promise

class App(env: dynamic, routes: Router.()->Unit) {

    val router = RootRouter(routes)
    val env = EnvVar(env)

    @JsName("call")
    fun call(method: String, path: String, body: dynamic): Promise<*> {
        return GlobalScope.async { router.call(method, path, body) }.asPromise()
    }

    companion object {
        var instance: App? = null
    }

    fun printPaths() = "[" + app().router.getPaths().joinToString { "\"[${it.first}] ${it.second}\"" } + "]"

}

fun app(): App {
    if(App.instance == null) {
        App.instance = App(js("process.env")) { init() }
    }
    return App.instance!!
}
