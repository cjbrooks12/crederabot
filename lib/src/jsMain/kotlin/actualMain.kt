package com.caseyjbrooks.netlify.expect

import com.caseyjbrooks.netlify.EnvVar
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

actual fun envString() = object : ReadOnlyProperty<EnvVar, String> {
    override fun getValue(thisRef: EnvVar, property: KProperty<*>): String {
        val env: dynamic = js("process.ENV")
        return if(env != null) "${env[property.name]}" else ""
    }
}

actual fun envBoolean() = object : ReadOnlyProperty<EnvVar, Boolean> {
    override fun getValue(thisRef: EnvVar, property: KProperty<*>): Boolean {
        val env: dynamic = js("process.ENV")
        return if(env != null) "${env[property.name]}".toBoolean() else false
    }
}
