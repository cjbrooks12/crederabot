
import router.get
import router.post
import router.route
import router.slack
import router.slackEvent

fun Router.init() {
    get("sitemap") {
        Response(200, app().printPaths())
    }
    post("slack-user-mentioned") {
        Response(200, "handled /slack-user-mentioned")
    }
    route("slack") {

        // handle Slack URL verification
        slack("url_verification") {
            Response(200, "handled /slack url_verification")
        }

        // handle Slack message posted
        slackEvent("message") {
            Response(200, "handled /slack message")
        }
    }
}

