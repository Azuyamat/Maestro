package com.azuyamat.maestro.common.parser

import com.azuyamat.maestro.common.annotations.Argument
import com.azuyamat.maestro.common.enum.Scope
import com.azuyamat.maestro.common.models.ArgumentData
import kotlin.reflect.KFunction
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.valueParameters

val ENTRY_SCOPES: Array<String> = arrayOf("execute", "onCommand")

class Parser(private val method: KFunction<*>) {

    private val parameters = method.valueParameters
    val arguments = parameters.map {
        val info = it.findAnnotation<Argument>()
        ArgumentData(
            name = it.name ?: "",
            description = info?.description ?: "",
            position = it.index,
            type = it.type
        )
    }
    val scope = if (method.name in ENTRY_SCOPES) Scope.COMMAND else Scope.SUBCOMMAND
}