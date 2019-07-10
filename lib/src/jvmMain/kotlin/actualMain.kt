package com.caseyjbrooks.netlify.api.expect

import com.caseyjbrooks.netlify.api.EnvVar
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

actual fun envString() = object : ReadOnlyProperty<EnvVar, String> {
    override fun getValue(thisRef: EnvVar, property: KProperty<*>): String {
        return System.getenv(property.name)
    }
}

actual fun envBoolean() = object : ReadOnlyProperty<EnvVar, Boolean> {
    override fun getValue(thisRef: EnvVar, property: KProperty<*>): Boolean {
        return System.getenv(property.name)?.toBoolean() ?: false
    }
}
