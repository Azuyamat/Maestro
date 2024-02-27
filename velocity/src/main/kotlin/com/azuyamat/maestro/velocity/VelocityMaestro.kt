package com.azuyamat.maestro.velocity

import com.azuyamat.maestro.common.Maestro
import com.azuyamat.maestro.common.annotations.Command
import com.azuyamat.maestro.velocity.builders.CommandBuilder
import com.azuyamat.maestro.velocity.commands.VelocityCommand
import com.velocitypowered.api.proxy.ProxyServer
import org.reflections.Reflections
import org.reflections.scanners.SubTypesScanner
import org.reflections.scanners.TypeAnnotationsScanner
import org.reflections.util.ClasspathHelper
import org.reflections.util.ConfigurationBuilder
import kotlin.reflect.KClass
import kotlin.time.measureTime

class VelocityMaestro(
    private val proxyServer: ProxyServer
): Maestro {
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


            println("Registered commands from package $packageName in $time")
        }
    }

    override fun registerCommand(clazz: KClass<*>) {
        println("Registering command ${clazz.simpleName}")

        val builder = CommandBuilder(clazz)
        val data = builder.build()
        val velocityCommand = VelocityCommand(proxyServer, data)
        val meta = velocityCommand.buildMeta()

        proxyServer.commandManager.register(meta, velocityCommand)

        println("Registered command: ${data.name}")
    }
}