package com.caseyjbrooks.netlify.api.router

import com.caseyjbrooks.netlify.api.expect.runTest
import com.caseyjbrooks.netlify.api.router.http.HttpRootRouter
import com.caseyjbrooks.netlify.api.router.http.HttpRouter
import com.caseyjbrooks.netlify.api.router.http.Request
import com.caseyjbrooks.netlify.api.router.http.delete
import com.caseyjbrooks.netlify.api.router.http.get
import com.caseyjbrooks.netlify.api.router.http.ok
import com.caseyjbrooks.netlify.api.router.http.post
import com.caseyjbrooks.netlify.api.router.http.put
import com.caseyjbrooks.netlify.api.router.http.route
import com.caseyjbrooks.netlify.containsExactly
import com.caseyjbrooks.netlify.expectThat
import com.caseyjbrooks.netlify.impl.TestApp
import com.caseyjbrooks.netlify.isEqualTo
import kotlin.test.Test

class HttpRouterTest {

    @Test
    fun testNormalAppRoutes() = runTest {
        val router = HttpRootRouter {
            usersResource()
        }
        expectThat(router.call(Request(TestApp(), "GET", "/users", "")).body).isEqualTo("all users")
        expectThat(router.call(Request(TestApp(), "GET", "/users/12", "")).body).isEqualTo("one user 12")
        expectThat(router.call(Request(TestApp(), "POST", "/users", "")).body).isEqualTo("creating user")
        expectThat(router.call(Request(TestApp(), "PUT", "/users/12", "")).body).isEqualTo("updating user 12")
        expectThat(router.call(Request(TestApp(), "DELETE", "/users/12", "")).body).isEqualTo("deleting user 12")

        expectThat(router.getPaths()).containsExactly(
            "GET" to "/users",
            "GET" to "/users/:id",
            "POST" to "/users",
            "PUT" to "/users/:id",
            "DELETE" to "/users/:id"
        )

        var visitorCount = 0
        router.visit { visitorCount++ }
        expectThat(visitorCount).isEqualTo(7) // root router, its subrouter, and 5 routes
    }

    @Test
    fun testAppRoutesAtSubRoute() = runTest {
        val router = HttpRootRouter {
            route("api") {
                usersResource()
            }
        }
        expectThat(router.call(Request(TestApp(), "GET", "/api/users", "")).body).isEqualTo("all users")
        expectThat(router.call(Request(TestApp(), "GET", "/api/users/12", "")).body).isEqualTo("one user 12")
        expectThat(router.call(Request(TestApp(), "POST", "/api/users", "")).body).isEqualTo("creating user")
        expectThat(router.call(Request(TestApp(), "PUT", "/api/users/12", "")).body).isEqualTo("updating user 12")
        expectThat(router.call(Request(TestApp(), "DELETE", "/api/users/12", "")).body).isEqualTo("deleting user 12")

        expectThat(router.getPaths()).containsExactly(
            "GET" to "/api/users",
            "GET" to "/api/users/:id",
            "POST" to "/api/users",
            "PUT" to "/api/users/:id",
            "DELETE" to "/api/users/:id"
        )

        var visitorCount = 0
        router.visit { visitorCount++; }
        expectThat(visitorCount).isEqualTo(8) // root router, its subrouter, which is a subrouter with 5 routes
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
