package com.azuyamat.maestro.common

import com.azuyamat.maestro.common.annotations.Command
import com.azuyamat.maestro.common.builders.CommandBuilder
import com.azuyamat.maestro.common.models.VinylData
import org.reflections.Reflections
import kotlin.reflect.KClass

abstract class Maestro {

    var commands: MutableList<VinylData> = mutableListOf()

    private fun registerCommand(`class`: KClass<*>) {
        val builder = CommandBuilder(`class`)
        builder.printTree()
        commands.add(builder.data)
    }

    private fun registerCommands(pkg: Reflections) {
        val classes = pkg.getTypesAnnotatedWith(Command::class.java)
        for (`class` in classes)
            registerCommand(`class`.kotlin)
    }

    fun registerPackages(vararg `package`: String) {
        for (pkg in `package`) {
            val parsedPackage = Reflections(pkg)
            registerCommands(parsedPackage)
        }
    }
}