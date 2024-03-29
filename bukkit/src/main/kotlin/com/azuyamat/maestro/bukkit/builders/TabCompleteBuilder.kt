package com.azuyamat.maestro.bukkit.builders

import com.azuyamat.maestro.common.annotations.Tab
import com.azuyamat.maestro.common.completions.CompletionsRegistry.getCompletion
import com.azuyamat.maestro.common.data.command.CommandData
import org.bukkit.OfflinePlayer
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.valueParameters

class TabCompleteBuilder(
    private val instance: JavaPlugin,
    private val command: CommandData,
    private val mainFunction: KFunction<*>?,
    private val subFunctions: List<KFunction<*>>
) {

    fun build(): TabCompleter {
        val subCommands = command.subCommands
        return TabCompleter { sender, _, _, args ->
            if (args.size < 2 && subCommands.isNotEmpty()) {
                // Main function tab complete (sub commands)
                subCommands.keys.toList()
            } else if (subCommands.isNotEmpty()) {
                // Sub functions tab complete (sub command args)
                val subCommandName = args[0]
                val subFunction = subFunctions.firstOrNull { it.name == subCommandName } ?: return@TabCompleter emptyList()
                val parameters = subFunction.valueParameters

                complete(parameters, args.size)
            } else {
                // Main function tab complete (main command args)
                if (mainFunction == null) return@TabCompleter subCommands.map { it.key }
                val parameters = mainFunction.valueParameters.slice(1 until mainFunction.valueParameters.size)

                complete(parameters, args.size)
            }
        }
    }

    private fun complete(parameters: List<KParameter>, size: Int): List<String> {
        val index = size - 1
        if (index < 0 || index > parameters.size - 1) return emptyList()

        val parameter = parameters[index]

        val type = parameter.type
        val tab = parameter.findAnnotation<Tab>()
        val tabTypeList = tab?.list ?: "default"

        return if (tabTypeList == "default") {
            when (type.classifier) {
                Boolean::class -> listOf("true", "false")
                Player::class -> instance.server.onlinePlayers.map { it.name }
                OfflinePlayer::class -> instance.server.offlinePlayers.mapNotNull { it.name }
                Int::class -> (0..100).map { it.toString() }
                else -> emptyList()
            }
        } else {
            val completion = getCompletion(tabTypeList) ?: return emptyList()
            completion.complete()
        }
    }
}