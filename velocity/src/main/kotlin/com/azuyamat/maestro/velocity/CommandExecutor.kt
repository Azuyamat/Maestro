package com.azuyamat.maestro.velocity

import com.azuyamat.maestro.common.CooldownManager
import com.azuyamat.maestro.common.annotations.Catcher
import com.azuyamat.maestro.common.annotations.SubCommand
import com.azuyamat.maestro.common.data.command.CommandData
import com.azuyamat.maestro.common.enums.SenderType
import com.azuyamat.maestro.common.parse
import com.velocitypowered.api.command.CommandSource
import com.velocitypowered.api.command.SimpleCommand.Invocation
import com.velocitypowered.api.proxy.ConsoleCommandSource
import com.velocitypowered.api.proxy.Player
import com.velocitypowered.api.proxy.ProxyServer
import kotlin.jvm.optionals.getOrNull
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.functions
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.valueParameters

class CommandExecutor(
    private val proxyServer: ProxyServer,
    private val data: CommandData
) {
    private val cooldownManager = CooldownManager()
    private val mainFunction = data.clazz.functions.firstOrNull { it.name == "onCommand" }
    private val subFunctions = data.clazz.functions.filter { it.hasAnnotation<SubCommand>() && it.name != "onCommand" }

    fun execute(invocation: Invocation) {
        val sender = invocation.source()
        val senderType = sender.asSenderType()
        val args = invocation.arguments()

        // Make sure player has permission to run the main command scope
        if (!sender.hasPermission(data.permission)) {
            sender.sendMessage(data.permissionMessage.parse())
            return
        }

        val commandInstance = data.clazz.constructors.first().call()

        val function: KFunction<*>
        val parsedArgs: Array<*>
        var cooldownId = data.name.lowercase()
        var cooldown = data.cooldown
        var usage = data.usage

        val params: List<KParameter>

        var requiredSender = data.senderType

        // Main `onCommand` function
        if (args.isEmpty() || data.subCommands.isEmpty()) {
            if (mainFunction == null) {
                sender.sendMessage("<red>Command not implemented. Use subcommands instead: ${data.subCommands.keys.joinToString(", ")}".parse())
                return
            }
            usage = data.usage

            parsedArgs = parseCommandArgs(mainFunction, args)
            params = mainFunction.valueParameters.slice(1 until mainFunction.valueParameters.size)
            function = mainFunction
        }
        // SubCommand function
        else {
            val subCommandName = args[0].lowercase()
            val subArgs = args.copyOfRange(1, args.size)
            val subFunction = subFunctions.firstOrNull { it.name == subCommandName } ?: run {
                sender.sendMessage("<red>Unknown subcommand".parse())
                return
            }
            val subCommand = data.subCommands[subCommandName] ?: run {
                sender.sendMessage("<red>Unknown subcommand".parse())
                return
            }
            usage = subCommand.usage
            requiredSender = subCommand.senderType

            // Make sure player has permission to run the subcommand scope
            if (!sender.hasPermission(subCommand.permission)) {
                sender.sendMessage(subCommand.permissionMessage.parse())
                return
            }

            cooldownId += ".$subCommandName"

            parsedArgs = parseCommandArgs(subFunction, subArgs)
            params = subFunction.valueParameters.slice(1 until subFunction.valueParameters.size)
            function = subFunction
            cooldown = subCommand.cooldown.takeIf { it > 0 } ?: data.cooldown
        }

        // Make sure the sender type is allowed to run the command
        if (requiredSender != SenderType.BOTH && requiredSender != senderType) {
            sender.sendMessage("<red>You cannot run this command as a ${senderType.name.lowercase()}".parse())
            return
        }

        val requiredParams = params.filterNot { it.type.isMarkedNullable }

        // Make sure all required parameters are present
        val givenArgs = parsedArgs.filterNotNull().size
        val requiredArgs = requiredParams.size
        println("Given: $givenArgs - Required: $requiredArgs - Params: $params - Parsed: ${parsedArgs.joinToString(", ")}")
        if (givenArgs < requiredArgs) {
            sender.sendMessage("<red>Invalid usage: /".parse().append(usage))
            return
        }

        // Make sure the player is not on cooldown
        if (sender is Player && cooldown > 0) {
            if (cooldownManager.isOnCooldown(sender.uniqueId, cooldownId)) {
                val timeLeft = cooldownManager.timeLeft(sender.uniqueId, cooldownId)
                sender.sendMessage("<red>You are on cooldown for this command for $timeLeft <red>ms".parse())
                return
            }
        }

        // Invoke the command
        function.call(commandInstance, sender, *parsedArgs)

        // Set cooldown
        if (sender is Player && cooldown > 0) {
            cooldownManager.setCooldown(sender.uniqueId, cooldownId, cooldown)
        }
    }

    private fun parseCommandArgs(method: KFunction<*>, args: Array<String>): Array<*> {

        // Slice the sender
        val parameters = method.valueParameters.slice(1 until method.valueParameters.size)
        val parsedArgs = arrayOfNulls<Any>(parameters.size)

        for ((index, arg) in args.withIndex()) {

            val parameter = parameters.getOrNull(index) ?: break
            val type = parameter.type

            if (parameter.hasAnnotation<Catcher>()) {
                parsedArgs[index] = args.slice(index until args.size).joinToString(" ")
                break
            }

            parsedArgs[index] = when(type.classifier) {
                String::class -> arg
                Int::class -> arg.toIntOrNull()
                Double::class -> arg.toDoubleOrNull()
                Float::class -> arg.toFloatOrNull()
                Boolean::class -> arg.toBoolean()
                Player::class -> proxyServer.getPlayer(arg).getOrNull()
                else -> null
            }
        }

        return parsedArgs
    }
}

fun CommandSource.asSenderType(): SenderType {
    return when (this) {
        is Player -> SenderType.PLAYER
        is ConsoleCommandSource -> SenderType.CONSOLE
        else -> SenderType.BOTH
    }
}