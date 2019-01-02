
fun doAKotlinThing() : String {
    println("a kotlin thing")

    return "a kotlin thing"
}

class FunctionHandler(val method: String, path: String) {

    fun handle(): String {
        return "I got handled in Kotlin!"
    }

}