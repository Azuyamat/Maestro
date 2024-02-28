package com.azuyamat.maestro.velocity.builders

import com.azuyamat.maestro.common.annotations.Command
import com.azuyamat.maestro.common.annotations.SubCommand
import com.azuyamat.maestro.common.data.command.CommandData
import com.azuyamat.maestro.common.data.command.SubCommandData
import com.azuyamat.maestro.common.enums.SenderType
import com.azuyamat.maestro.common.parse
import com.velocitypowered.api.proxy.ConsoleCommandSource
import com.velocitypowered.api.proxy.Player
import net.kyori.adventure.text.Component
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.functions
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.valueParameters

class CommandBuilder(
    private val clazz: KClass<*>
) {
    private val commandAnnotation = clazz.findAnnotation<Command>()

    private val mainFunction = clazz.functions.find { it.name == "onCommand" }
    private val subCommands = clazz.functions.filter { it.hasAnnotation<SubCommand>() && it.name != "onCommand" }

    fun name() = commandAnnotation?.name ?: throw IllegalArgumentException("Command name not found")
    fun description() = commandAnnotation?.description ?: ""
    fun aliases() = commandAnnotation?.aliases ?: arrayOf()
    fun permission() = commandAnnotation?.permission
    fun permissionMessage() = commandAnnotation?.permissionMessage
    fun senderType() = mainFunction?.valueParameters?.firstOrNull()?.let { getSenderType(it) } ?: SenderType.BOTH
    fun cooldown() = commandAnnotation?.cooldown ?: 0
    fun usage() = buildUsage(mainFunction, name())
    fun subCommands() = subCommands.map {
        val subCommandAnnotation = it.findAnnotation<SubCommand>()!!
        val senderType = getSenderType(it.parameters.firstOrNull())
        SubCommandData(
            name = subCommandAnnotation.name,
            description = subCommandAnnotation.description,
            permission = subCommandAnnotation.permission,
            permissionMessage = subCommandAnnotation.permissionMessage,
            senderType = senderType,
            cooldown = subCommandAnnotation.cooldown,
            usage = buildUsage(it, name())
        )
    }

    fun build() = CommandData(
        clazz = clazz,
        name = name(),
        description = description(),
        aliases = aliases(),
        permission = permission() ?: "",
        permissionMessage = permissionMessage() ?: "",
        senderType = senderType(),
        cooldown = cooldown(),
        subCommands = subCommands().associateBy { it.name },
        usage = usage()
    )
}

fun getSenderType(parameter: KParameter?) = when (parameter?.type?.classifier) {
    Player::class -> SenderType.PLAYER
    ConsoleCommandSource::class -> SenderType.CONSOLE
    else -> SenderType.BOTH
}

fun buildUsage(function: KFunction<*>?, parent: String = ""): Component {
    if (function == null) return Component.empty()
    val parameters = function.valueParameters.slice(1 until function.valueParameters.size)
    var usage = parent
    val parsedParameters: MutableList<String> = mutableListOf()
    for (parameter in parameters) {
        val name = parameter.name
        val type = parameter.type.classifier
        val optional = parameter.type.isMarkedNullable
        var parsedParameter = if (optional) "[$name]" else "<$name>"
        parsedParameter = "<hover:show_text:'<gray>${type?:String::class.simpleName}<reset>'><gray>$parsedParameter<reset>"
        parsedParameters.add(parsedParameter)
    }
    usage += parsedParameters.joinToString(" ")
    return usage.parse()
}