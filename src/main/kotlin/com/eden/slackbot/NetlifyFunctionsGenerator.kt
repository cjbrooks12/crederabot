package com.eden.slackbot

import com.eden.common.json.JSONElement
import com.eden.common.util.EdenPair
import com.eden.orchid.api.OrchidContext
import com.eden.orchid.api.converters.FlexibleMapConverter
import com.eden.orchid.api.converters.TypeConverter
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
import com.google.inject.Provider
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
    @StringDefault("functions")
    lateinit var functionsDir: String

    @Option
    lateinit var openApi: JSONObject

    override fun startIndexing(): MutableList<out OrchidPage>? {
        return null
    }

    override fun startGeneration(pages: Stream<out OrchidPage>?) {
        val functions = context.getLocalResourceEntries(functionsDir, arrayOf("js"), false)

        // render functions in parent directory
        functions
                .map { FunctionPage(it) }
                .map {
                    it.reference.isUsePrettyUrl = false
                    it.reference.replacePathSegment(0, "../functions")
                    it
                }
                .forEach { context.renderRaw(it) }

        // get list of functions that we can use to generate openApi JSON
        val functionPages = functions
                .map { FunctionPage(it) }
                .map {
                    it.reference.isUsePrettyUrl = false
                    it.reference.replacePathSegment(0, ".netlify/functions")
                    it
                }

        // build and render openApi JSON page
        val openApiJson = buildOpenApiJson(functionPages)
        val openApiJsonPage = OrchidPage(openApiJson, "netlifyOpenApi")
        context.renderRaw(openApiJsonPage)
    }

    private fun buildOpenApiJson(functions: List<FunctionPage>): OrchidResource {
        val json = openApi
        val reference = OrchidReference(context, "netlify/openApi.json")
        reference.isUsePrettyUrl = false

        // get or create 'paths' object
        if (!json.has("paths")) {
            json.put("paths", JSONObject())
        }
        val paths = json.getJSONObject("paths")

        for (function in functions) {
            val pathStr = "/${function.reference.relativePath}"

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
    class FunctionPage(resource: OrchidResource) : OrchidPage(resource, "netlifyFunction") {

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

        class ParamConverter @Inject
        constructor(private val context: OrchidContext, private val mapConverter: Provider<FlexibleMapConverter>) : TypeConverter<OpenApiParam> {

            override fun acceptsClass(clazz: Class<*>): Boolean {
                return clazz == OpenApiParam::class.java
            }

            override fun convert(o: Any): EdenPair<Boolean, OpenApiParam> {
                val itemSource = mapConverter.get().convert(o).second as Map<String, Any>

                val item = OpenApiParam()
                item.extractOptions(context, itemSource)

                return EdenPair(true, item)
            }
        }

        class ResponseConverter @Inject
        constructor(private val context: OrchidContext, private val mapConverter: Provider<FlexibleMapConverter>) : TypeConverter<OpenApiParamResponse> {

            override fun acceptsClass(clazz: Class<*>): Boolean {
                return clazz == OpenApiParamResponse::class.java
            }

            override fun convert(o: Any): EdenPair<Boolean, OpenApiParamResponse> {
                val itemSource = mapConverter.get().convert(o).second as Map<String, Any>

                val item = OpenApiParamResponse()
                item.extractOptions(context, itemSource)

                return EdenPair(true, item)
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