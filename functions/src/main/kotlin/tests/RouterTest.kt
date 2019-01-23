package com.caseyjbrooks.netlify.tests

import com.caseyjbrooks.netlify.asResponse
import com.caseyjbrooks.netlify.router.Request
import com.caseyjbrooks.netlify.router.Response
import com.caseyjbrooks.netlify.router.RootRouter
import com.caseyjbrooks.netlify.router.after
import com.caseyjbrooks.netlify.router.before
import com.caseyjbrooks.netlify.router.delete
import com.caseyjbrooks.netlify.router.get
import com.caseyjbrooks.netlify.router.post
import com.caseyjbrooks.netlify.router.put
import com.caseyjbrooks.netlify.router.route

fun <T> assertEquals(expected: T, actual: T?) {
    if (expected != actual) {
        throw RuntimeException("$actual should be equal to $expected")
    }
}

fun <T, R> T?.ensure(block: (T) -> R): R {
    if(this == null) throw RuntimeException("object cannot be null!")
    return block(this)
}

suspend fun runTests(): Boolean {
    return try {
        testSimpleRouting()
        testBasicSubRouting()
        testDeeplyNestedSubRouting()
        testMiddleware()
        true
    } catch (e: Throwable) {
        console.log(e.message)
        console.log(e.cause?.message)
        false
    }
}

suspend fun testSimpleRouting() {
    console.log("testSimpleRouting...")

    val router = RootRouter {
        get("route1") { "get success".asResponse(200) }
        post("route1") { "post success".asResponse(200) }
        put("route1") { "put success".asResponse(200) }
        delete("route1") { "delete success".asResponse(200) }
    }

    console.log(router.getPaths().joinToString { "[${it.first}] ${it.second}" })

    router.call(Request("GET", "/route1", object {})).ensure {
        assertEquals(200, it.statusCode)
        assertEquals("get success", it.body)
    }
    router.call(Request("POST", "/route1", object {})).ensure {
        assertEquals(200, it.statusCode)
        assertEquals("post success", it.body)
    }
    router.call(Request("PUT", "/route1", object {})).ensure {
        assertEquals(200, it.statusCode)
        assertEquals("put success", it.body)
    }
    router.call(Request("DELETE", "/route1", object {})).ensure {
        assertEquals(200, it.statusCode)
        assertEquals("delete success", it.body)
    }
    router.call(Request("GET", "/route2", object {})).ensure {
        assertEquals(404, it.statusCode)
        assertEquals("Route at '[GET] /route2' not found", it.body)
    }

    console.log("testSimpleRouting...success")
}

suspend fun testBasicSubRouting() {
    console.log("testBasicSubRouting...")

    val router = RootRouter {
        route("path1") {
            get("route1") { "success".asResponse(200) }
        }
        route("path2") {
            get("route2") { "success".asResponse(200) }
        }
    }

    console.log(router.getPaths().joinToString { "[${it.first}] ${it.second}" })

    router.call(Request("GET", "/path1/route1", object {})).ensure {
        assertEquals(200, it.statusCode)
        assertEquals("success", it.body)
    }
    router.call(Request("GET", "/path2/route2", object {})).ensure {
        assertEquals(200, it.statusCode)
        assertEquals("success", it.body)
    }
    router.call(Request("GET", "/path1/route2", object {})).ensure {
        assertEquals(404, it.statusCode)
        assertEquals("Route at '[GET] /path1/route2' not found", it.body)
    }
    router.call(Request("GET", "/path2/route1", object {})).ensure {
        assertEquals(404, it.statusCode)
        assertEquals("Route at '[GET] /path2/route1' not found", it.body)
    }

    console.log("testBasicSubRouting...success")
}

suspend fun testDeeplyNestedSubRouting() {
    console.log("testDeeplyNestedSubRouting...")

    val router = RootRouter {
        route("path1") {
            route("path2") {
                route("path3") {
                    route("path4") {
                        get("route1") { "get success".asResponse(200) }
                        post("route1") { "post success".asResponse(200) }
                    }
                }
            }
        }
    }

    console.log(router.getPaths().joinToString { "[${it.first}] ${it.second}" })

    router.call(Request("GET", "/path1/path2/path3/path4/route1", object {})).ensure {
        assertEquals(200, it.statusCode)
        assertEquals("get success", it.body)
    }
    router.call(Request("POST", "/path1/path2/path3/path4/route1", object {})).ensure {
        assertEquals(200, it.statusCode)
        assertEquals("post success", it.body)
    }
    router.call(Request("GET", "/path2/route1", object {})).ensure {
        assertEquals(404, it.statusCode)
        assertEquals("Route at '[GET] /path2/route1' not found", it.body)
    }

    console.log("testDeeplyNestedSubRouting...success")
}

suspend fun testMiddleware() {
    console.log("testMiddleware...")

    val router = RootRouter {
        route("upper") {
            after { _, resp ->
                resp.copy(body = resp.body.toString().toUpperCase())
            }
            get("upper") {
                Response(200, "With MiddleWares!")
            }
        }
        route("lower") {
            after { _, resp ->
                resp.copy(body = resp.body.toString().toLowerCase())
            }
            get("lower") {
                Response(200, "With MiddleWares!")
            }
        }
        route("upper") {
            get("lower") {
                Response(200, "With MiddleWares!")
            }
        }
        route("pass") {
            before {
                Pair(null, Response(200, "sorry, bro!"))
            }
            get("pass") {
                Response(200, "With MiddleWares!")
            }
        }

        route("reroute") {
            before { req ->
                Pair(req.copy(path = req.path.replace("from", "to")), null)
            }
            get("from") {
                Response(200, "handling in from")
            }
            get("to") {
                Response(200, "handling in to")
            }
        }
    }

    router.call(Request("GET", "/upper/upper", object {})).ensure {
        assertEquals(200, it.statusCode)
        assertEquals("WITH MIDDLEWARES!", it.body)
    }
    router.call(Request("GET", "/lower/lower", object {})).ensure {
        assertEquals(200, it.statusCode)
        assertEquals("with middlewares!", it.body)
    }
    router.call(Request("GET", "/upper/lower", object {})).ensure {
        assertEquals(200, it.statusCode)
        assertEquals("With MiddleWares!", it.body)
    }
    router.call(Request("GET", "/pass/pass", object {})).ensure {
        assertEquals(200, it.statusCode)
        assertEquals("sorry, bro!", it.body)
    }
    router.call(Request("GET", "/reroute/from", object {})).ensure {
        assertEquals(200, it.statusCode)
        assertEquals("handling in to", it.body)
    }
    router.call(Request("GET", "/reroute/to", object {})).ensure {
        assertEquals(200, it.statusCode)
        assertEquals("handling in to", it.body)
    }

    console.log("testMiddleware...success")
}
