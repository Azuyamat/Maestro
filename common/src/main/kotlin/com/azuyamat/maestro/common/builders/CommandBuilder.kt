package com.azuyamat.maestro.common.builders

import com.azuyamat.maestro.common.annotations.Command
import com.azuyamat.maestro.common.models.CommandData
import com.azuyamat.maestro.common.models.VinylData
import com.azuyamat.maestro.common.parser.Parser
import java.lang.IllegalArgumentException
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.findAnnotation

class CommandBuilder(private val `class`: KClass<*>) {

    private val info = `class`.findAnnotation<Command>() ?:
        throw IllegalArgumentException("Command annotation not found for ${`class`.simpleName}")
    val data = VinylData(
        name = info.name,
        description = info.description,
        cooldown = info.cooldown,
        commands = registerCommands()
    )

    private fun registerCommands() = `class`.declaredMemberFunctions.map(::registerCommand)

    private fun registerCommand(method: KFunction<*>): CommandData {
        val parser = Parser(method)
        return CommandData(
            name = method.name,
            scope = parser.scope,
            description = "",
            arguments = parser.arguments
        )
    }

    fun printTree() {
        println("Vinyl: ${data.name}")
        val commands = data.commands.sortedBy { it.scope.depth }
        for (command in commands) {
            println("  ${" ".repeat(command.scope.depth * 2)} -- ${command.name}")
        }
    }
}