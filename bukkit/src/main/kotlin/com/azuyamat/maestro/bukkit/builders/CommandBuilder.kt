package com.azuyamat.maestro.bukkit.builders

import com.azuyamat.maestro.bukkit.CooldownManager
import com.azuyamat.maestro.bukkit.annotations.Command
import com.azuyamat.maestro.bukkit.annotations.SubCommand
import com.azuyamat.maestro.bukkit.data.CommandData
import com.azuyamat.maestro.bukkit.data.SubCommandData
import com.azuyamat.maestro.bukkit.enums.SenderType
import com.azuyamat.maestro.bukkit.parse
import net.kyori.adventure.text.Component
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.functions
import kotlin.reflect.full.valueParameters

class CommandBuilder(
    private val instance: JavaPlugin,
    clazz: KClass<*>
) {

    val cooldownManager = CooldownManager()
    private val annotation = clazz.findAnnotation<Command>()!! // !! is safe because we check for it in CommandRegistry before calling this

    private val functions = clazz.functions
    // This can now be null
    private val mainFunction = functions.find { it.name == "onCommand" }
    private val subFunctions = functions.filter { it.name != "onCommand" }

    private val subCommands = subFunctions.mapNotNull {
        val subAnnotation = it.findAnnotation<SubCommand>() ?: return@mapNotNull null
        val name = subAnnotation.name

        val senderType = getSenderType(it.valueParameters.first())

        val info = SubCommandData(
            name = name,
            description = subAnnotation.description,
            permission = subAnnotation.permission,
            permissionMessage = subAnnotation.permissionMessage,
            senderType = senderType,
            cooldown = subAnnotation.cooldown,
            usage = buildUsage(it, annotation.name)
        )

        name to info
    }.toMap()

    var commandInfo = CommandData(
        clazz = clazz,
        name = annotation.name,
        description = annotation.description,
        aliases = annotation.aliases,
        permission = annotation.permission,
        permissionMessage = annotation.permissionMessage,
        senderType = getSenderType(mainFunction?.valueParameters?.first()),
        cooldown = annotation.cooldown,
        subCommands = subCommands,
        usage = buildUsage(mainFunction)
    )

    val executor = ExecutorBuilder(
        instance,
        commandInfo,
        cooldownManager,
        mainFunction,
        subFunctions
    ).build()

    val tabCompleter = TabCompleteBuilder(
        instance,
        commandInfo,
        mainFunction,
        subFunctions
    ).build()

    companion object {
        fun getSenderType(parameter: KParameter?) = when(parameter?.type?.classifier) {
            Player::class -> SenderType.PLAYER
            ConsoleCommandSender::class -> SenderType.CONSOLE
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
    }
}