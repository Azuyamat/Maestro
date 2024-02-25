package com.azuyamat.maestro.bukkit

import com.azuyamat.maestro.common.annotations.Command
import com.azuyamat.maestro.bukkit.builders.CommandBuilder
import com.azuyamat.maestro.common.data.command.CommandData
import com.azuyamat.maestro.common.Maestro
import com.azuyamat.maestro.common.enums.SenderType
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.command.PluginCommand
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.reflections.Reflections
import org.reflections.scanners.SubTypesScanner
import org.reflections.scanners.TypeAnnotationsScanner
import org.reflections.util.ClasspathHelper
import org.reflections.util.ConfigurationBuilder
import kotlin.reflect.KClass
import kotlin.time.measureTime

class BukkitMaestro(
    private val instance: JavaPlugin
): Maestro {
    private val logger = instance.logger
    private val commandMap = Bukkit.getCommandMap()
    private val builderConstructor = PluginCommand::class.java.declaredConstructors.first()
    val commands: MutableList<CommandData> = mutableListOf()

    init {
        builderConstructor.isAccessible = true
    }

    override fun registerCommands(vararg packages: String) {

        for (packageName in packages) {
            val pkg = Reflections(
                ConfigurationBuilder()
                    .setUrls(ClasspathHelper.forPackage(packageName))
                    .setScanners(SubTypesScanner(), TypeAnnotationsScanner()))
            val commands = pkg.getTypesAnnotatedWith(Command::class.java)
            val time = measureTime {
                for (command in commands) {
                    registerCommand(command.kotlin)
                }
            }
            logger.info("Registered commands from package $packageName in $time")
        }
    }

    override fun registerCommand(clazz: KClass<*>) {

        logger.info("Registering command ${clazz.simpleName}")

        val parsedCommand = CommandBuilder(instance, clazz)
        val info = parsedCommand.commandInfo

        val builder = builderConstructor.newInstance(info.name, instance) as PluginCommand
        builder.apply {
            aliases = info.aliases.toList()
            description = info.description
            label = name
            setExecutor(parsedCommand.executor)
            tabCompleter = parsedCommand.tabCompleter
        }

        commands.add(info)
        commandMap.register(builder.label, builder)
    }

    companion object {
        fun CommandSender.asSenderType(): SenderType {
            return when (this) {
                is Player -> SenderType.PLAYER
                is ConsoleCommandSender -> SenderType.CONSOLE
                else -> SenderType.BOTH
            }
        }
    }
}
