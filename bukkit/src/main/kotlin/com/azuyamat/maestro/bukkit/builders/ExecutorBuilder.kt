package com.azuyamat.maestro.bukkit.builders

import com.azuyamat.maestro.bukkit.CooldownManager
import com.azuyamat.maestro.bukkit.annotations.Catcher
import com.azuyamat.maestro.bukkit.data.CommandData
import com.azuyamat.maestro.bukkit.enums.SenderType
import com.azuyamat.maestro.bukkit.parse
import net.kyori.adventure.text.Component
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.valueParameters

class ExecutorBuilder(
    private val instance: JavaPlugin,
    private val command: CommandData,
    private val cooldownManager: CooldownManager,
    private val mainFunction: KFunction<*>,
    private val subFunctions: List<KFunction<*>>
) {

    fun build() = CommandExecutor { sender, _, _, args ->

        val senderType = SenderType.fromSender(sender)

        // Make sure player has permission to run the main command scope
        if (!checkForPermission(sender, command.permission)) {
            sender.sendMessage(command.permissionMessage.parse())
            return@CommandExecutor true
        }

        val commandInstance = command.clazz.constructors.first().call()

        val function: KFunction<*>
        val parsedArgs: Array<*>
        var cooldownId = command.name.lowercase()
        var cooldown = command.cooldown
        val usage: Component

        val params: List<KParameter>

        var requiredSender = command.senderType

        // Main `onCommand` function
        if (args.isEmpty() || command.subCommands.isEmpty()) {
            usage = command.usage

            parsedArgs = parseCommandArgs(mainFunction, args)
            params = mainFunction.valueParameters.slice(1 until mainFunction.valueParameters.size)
            function = mainFunction
        }
        // SubCommand function
        else {

            val subCommandName = args[0].lowercase()
            val subArgs = args.copyOfRange(1, args.size)
            val subFunction = subFunctions.firstOrNull { it.name == subCommandName } ?: run {
                subCommandNotFound(sender, subCommandName)
                return@CommandExecutor true
            }
            val subCommand = command.subCommands[subCommandName] ?: run {
                subCommandNotFound(sender, subCommandName)
                return@CommandExecutor true
            }
            usage = subCommand.usage
            requiredSender = subCommand.senderType

            // Make sure player has permission to run the sub command scope
            if (!checkForPermission(sender, subCommand.permission)) {
                sender.sendMessage(subCommand.permissionMessage.parse())
                return@CommandExecutor true
            }

            cooldownId += ".$subCommandName"

            parsedArgs = parseCommandArgs(subFunction, subArgs)
            params = subFunction.valueParameters.slice(1 until subFunction.valueParameters.size)
            function = subFunction
            cooldown = subCommand.cooldown.takeIf { it > 0 } ?: command.cooldown
        }

        // Make sure the sender is proper type
        if (requiredSender != SenderType.BOTH && requiredSender != senderType) {
            sender.sendMessage("<gray>This command can only be used by <main>${requiredSender.cleanName}".parse())
            return@CommandExecutor true
        }

        val requiredParams = params.filterNot { it.type.isMarkedNullable }
//        val optionalParams = params.filter { it.type.isMarkedNullable }

        // Make sure all required parameters are present
        if (parsedArgs.filterNotNull().size < requiredParams.size) {
            sender.sendMessage("<gray>Invalid usage: <gray>/".parse().append(usage))
            return@CommandExecutor true
        }

        // Make sure the player is not on cooldown
        if (sender is Player && cooldown > 0) {
            if (cooldownManager.isOnCooldown(sender, cooldownId)) {
                val timeLeft = cooldownManager.timeLeft(sender, cooldownId)
                sender.sendMessage("<gray>You are on cooldown for <main>$timeLeft <gray>ms".parse())
                return@CommandExecutor true
            }
        }

        // Execute command
        val result = function.call(commandInstance, sender, *parsedArgs)

        // Set cooldown
        if (sender is Player && cooldown > 0) {
            cooldownManager.setCooldown(sender, cooldownId, cooldown)
        }

        true
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
                Player::class -> instance.server.getPlayer(arg)
                OfflinePlayer::class -> instance.server.getOfflinePlayer(arg)
                else -> null
            }
        }

        return parsedArgs
    }

    private val subCommandNotFound = { sender: CommandSender, name: String -> {
        sender.sendMessage("<gray>Unknown subcommand: <main>$name".parse())
    }}

    private fun checkForPermission(player: CommandSender, permission: String): Boolean {
        return if (permission.isEmpty()) true else player.hasPermission(permission)
    }
}