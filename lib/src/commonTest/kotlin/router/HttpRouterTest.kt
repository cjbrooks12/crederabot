package com.caseyjbrooks.netlify.router

import com.caseyjbrooks.netlify.App
import com.caseyjbrooks.netlify.containsExactly
import com.caseyjbrooks.netlify.expect.runTest
import com.caseyjbrooks.netlify.expectThat
import com.caseyjbrooks.netlify.isEqualTo
import com.caseyjbrooks.netlify.router.http.HttpRootRouter
import com.caseyjbrooks.netlify.router.http.HttpRouter
import com.caseyjbrooks.netlify.router.http.Request
import com.caseyjbrooks.netlify.router.http.delete
import com.caseyjbrooks.netlify.router.http.get
import com.caseyjbrooks.netlify.router.http.ok
import com.caseyjbrooks.netlify.router.http.post
import com.caseyjbrooks.netlify.router.http.put
import com.caseyjbrooks.netlify.router.http.route
import kotlin.test.Test

class HttpRouterTest {

    @Test
    fun testNormalAppRoutes() = runTest {
        val router = HttpRootRouter {
            usersResource()
        }
        expectThat(router.call(Request(App {}, "GET", "/users", "")).body).isEqualTo("all users")
        expectThat(router.call(Request(App {}, "GET", "/users/12", "")).body).isEqualTo("one user 12")
        expectThat(router.call(Request(App {}, "POST", "/users", "")).body).isEqualTo("creating user")
        expectThat(router.call(Request(App {}, "PUT", "/users/12", "")).body).isEqualTo("updating user 12")
        expectThat(router.call(Request(App {}, "DELETE", "/users/12", "")).body).isEqualTo("deleting user 12")

        expectThat(router.getPaths()).containsExactly(
            "GET" to "/users",
            "GET" to "/users/:id",
            "POST" to "/users",
            "PUT" to "/users/:id",
            "DELETE" to "/users/:id"
        )
    }

    @Test
    fun testAppRoutesAtSubRoute() = runTest {
        val router = HttpRootRouter {
            route("api") {
                usersResource()
            }
        }
        expectThat(router.call(Request(App {}, "GET", "/api/users", "")).body).isEqualTo("all users")
        expectThat(router.call(Request(App {}, "GET", "/api/users/12", "")).body).isEqualTo("one user 12")
        expectThat(router.call(Request(App {}, "POST", "/api/users", "")).body).isEqualTo("creating user")
        expectThat(router.call(Request(App {}, "PUT", "/api/users/12", "")).body).isEqualTo("updating user 12")
        expectThat(router.call(Request(App {}, "DELETE", "/api/users/12", "")).body).isEqualTo("deleting user 12")

        expectThat(router.getPaths()).containsExactly(
            "GET" to "/api/users",
            "GET" to "/api/users/:id",
            "POST" to "/api/users",
            "PUT" to "/api/users/:id",
            "DELETE" to "/api/users/:id"
        )
    }

    private fun HttpRouter.usersResource() {
        get("users") {
            "all users".ok()
        }
        get("users/:id") { (id) ->
            "one user $id".ok()
        }
        post("users") {
            "creating user".ok()
        }
        put("users/:id") { (id) ->
            "updating user $id".ok()
        }
        delete("users/:id") { (id) ->
            "deleting user $id".ok()
        }
    }

}
