package com.eden.slackbot

import com.eden.orchid.api.converters.TypeConverter
import com.eden.orchid.api.generators.OrchidGenerator
import com.eden.orchid.api.registration.OrchidModule
import com.eden.orchid.utilities.addToSet

class SlackbotModule : OrchidModule() {

    override fun configure() {
        addToSet<OrchidGenerator, NetlifyFunctionsGenerator>()
        addToSet<TypeConverter<*>>(
                NetlifyFunctionsGenerator.FunctionPage.ParamConverter::class,
                NetlifyFunctionsGenerator.FunctionPage.ResponseConverter::class
        )
    }

}
