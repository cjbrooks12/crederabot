package com.caseyjbrooks.netlify.api.expect

import com.caseyjbrooks.netlify.api.EnvVar
import kotlin.properties.ReadOnlyProperty

expect fun envString() : ReadOnlyProperty<EnvVar, String>
expect fun envBoolean() : ReadOnlyProperty<EnvVar, Boolean>