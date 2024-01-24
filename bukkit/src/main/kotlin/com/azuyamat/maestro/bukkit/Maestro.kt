package com.azuyamat.maestro.bukkit

import com.azuyamat.maestro.bukkit.annotations.Command
import com.azuyamat.maestro.bukkit.builders.CommandBuilder
import com.azuyamat.maestro.bukkit.data.CommandData
import org.bukkit.Bukkit
import org.bukkit.command.PluginCommand
import org.bukkit.plugin.java.JavaPlugin
import org.reflections.Reflections
import org.reflections.scanners.SubTypesScanner
import org.reflections.scanners.TypeAnnotationsScanner
import org.reflections.util.ClasspathHelper
import org.reflections.util.ConfigurationBuilder
import kotlin.reflect.KClass
import kotlin.time.measureTime

class Maestro(
    private val instance: JavaPlugin
) {
    private val logger = instance.logger
    private val commandMap = Bukkit.getCommandMap()
    private val builderConstructor = PluginCommand::class.java.declaredConstructors.first()
    val commands: MutableList<CommandData> = mutableListOf()

    init {
        builderConstructor.isAccessible = true
    }

    fun registerCommands(vararg packages: String) {

        for (packageName in packages) {
            val pkg = Reflections(
                ConfigurationBuilder()
                    .setUrls(ClasspathHelper.forPackage(packageName))
                    .setScanners(SubTypesScanner(), TypeAnnotationsScanner()))
            val commands = pkg.getTypesAnnotatedWith(Command::class.java)
            val time = measureTime {
                for (command in commands) {
                    val builder = registerCommand(command.kotlin)
                    commandMap.register(builder.label, builder)
                }
            }
            logger.info("Registered commands from package $packageName in $time")
        }
    }

    private fun registerCommand(clazz: KClass<*>): PluginCommand {

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

        return builder
    }
}
