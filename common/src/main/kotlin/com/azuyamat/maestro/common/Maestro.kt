package com.azuyamat.maestro.common

import kotlin.reflect.KClass

interface Maestro {
    fun registerCommands(vararg packages: String)
    fun registerCommand(clazz: KClass<*>)
}