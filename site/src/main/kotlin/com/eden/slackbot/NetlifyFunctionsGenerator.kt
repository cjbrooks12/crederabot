package com.eden.slackbot

import com.caseyjbrooks.clog.Clog
import com.eden.common.json.JSONElement
import com.eden.orchid.api.OrchidContext
import com.eden.orchid.api.generators.OrchidGenerator
import com.eden.orchid.api.options.OptionsHolder
import com.eden.orchid.api.options.annotations.BooleanDefault
import com.eden.orchid.api.options.annotations.Option
import com.eden.orchid.api.options.annotations.StringDefault
import com.eden.orchid.api.options.annotations.Validate
import com.eden.orchid.api.resources.resource.JsonResource
import com.eden.orchid.api.resources.resource.OrchidResource
import com.eden.orchid.api.theme.pages.OrchidPage
import com.eden.orchid.api.theme.pages.OrchidReference
import org.json.JSONObject
import java.util.stream.Stream
import javax.inject.Inject
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

class NetlifyFunctionsGenerator
@Inject
constructor(context: OrchidContext)
    : OrchidGenerator(context, "netlifyFunctions", OrchidGenerator.PRIORITY_LATE) {

    @Option
    @StringDefault("./../../../../netlify-lambda/src/main/js/")
    lateinit var functionsDir: String

    @Option
    lateinit var openApi: JSONObject

    override fun startIndexing(): MutableList<out OrchidPage>? {
        return null
    }

    override fun startGeneration(pages: Stream<out OrchidPage>?) {
        val functions = context.getLocalResourceEntries(functionsDir, arrayOf("js"), false)

        // get list of functions that we can use to generate openApi JSON
        val functionPages = functions
                .map { FunctionPage(it) }
                .map {
                    it.reference.isUsePrettyUrl = false
                    it.reference.path = ".netlify"
                    it
                }

        // build and render openApi JSON page
        val openApiJson = buildOpenApiJson(functionPages)
        val openApiJsonPage = OrchidPage(openApiJson, "netlifyOpenApi", null)
        context.renderRaw(openApiJsonPage)
    }

    private fun buildOpenApiJson(functions: List<FunctionPage>): OrchidResource {
        val json = openApi

        json.getJSONObject("info").put("version",
            Clog.format(json.getJSONObject("info").getString("version"))
        )
        json.getJSONArray("servers").forEach {
            (it as JSONObject).put("url", Clog.format(it.getString("url")))
        }

        val reference = OrchidReference(context, "netlify/openApi.json")
        reference.isUsePrettyUrl = false

        // get or create 'paths' object
        if (!json.has("paths")) {
            json.put("paths", JSONObject())
        }
        val paths = json.getJSONObject("paths")

        for (function in functions) {
            val pathStr = "/${function.reference.path}/${function.reference.fileName}"

            // get or create object for specific path
            if (!paths.has(pathStr)) {
                paths.put(pathStr, JSONObject())
            }
            val path = paths.getJSONObject(pathStr)
            path.put(function.method, function.createJson())
        }

        return JsonResource(JSONElement(json), reference)
    }

    @Validate
    class FunctionPage(resource: OrchidResource) : OrchidPage(resource, "netlifyFunction", null) {

        @Option
        @NotBlank
        lateinit var method: String

        @Option
        @NotBlank
        lateinit var summary: String

        @Option
        var operationId: String = ""
            get() {
                return if(field.isBlank()) {
                    reference.originalFileName
                }
                else {
                    field
                }
            }

        @Option @StringDefault("netlify")
        lateinit var tags: List<String>

        @Option
        lateinit var parameters: List<OpenApiParam>

        @Option
        lateinit var responses: List<OpenApiParamResponse>

        fun createJson(): JSONObject {
            return with(JSONObject()) {
                put("summary", summary)
                put("operationId", operationId)
                put("tags", tags)
                put("parameters", parameters.map { it.createJson() })
                put("responses", with(JSONObject()) {
                    responses.forEach {
                        put(it.status, it.createJson())
                    }
                })

                this
            }
        }
    }

    @Validate
    class OpenApiParam : OptionsHolder {

        public enum class In {
            query, body
        }

        @Option
        @NotBlank
        lateinit var name: String

        @Option
        @NotBlank
        lateinit var desc: String

        @Option @BooleanDefault(false)
        var required: Boolean = false

        @Option
        @NotNull
        lateinit var `in`: In

        fun createJson(): JSONObject {
            return JSONObject(this)
        }

        override fun getDescription(): String {
            return desc
        }
    }

    @Validate
    class OpenApiParamResponse : OptionsHolder {

        @Option
        @NotBlank
        lateinit var status: String

        @Option
        @NotBlank
        lateinit var desc: String

        fun createJson(): JSONObject {
            return JSONObject(this)
        }

        override fun getDescription(): String {
            return desc
        }
    }

}