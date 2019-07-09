package com.caseyjbrooks.netlify.expect

import com.caseyjbrooks.netlify.EnvVar
import kotlin.properties.ReadOnlyProperty

expect fun envString() : ReadOnlyProperty<EnvVar, String>
expect fun envBoolean() : ReadOnlyProperty<EnvVar, Boolean>