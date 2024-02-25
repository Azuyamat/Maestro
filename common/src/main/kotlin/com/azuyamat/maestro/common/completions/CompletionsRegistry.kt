package com.azuyamat.maestro.common.completions

import org.reflections.Reflections

object CompletionsRegistry {

    private val completions = mutableMapOf<String, Completion>()

    fun registerCompletions(vararg packages: String) {
        for (packageName in packages) {
            val completionsPackage = Reflections(packageName)
            val completions = completionsPackage.getSubTypesOf(Completion::class.java)

            for (completion in completions) {
                val instance = completion.constructors.first().newInstance() as Completion
                val name = completion.simpleName.lowercase().replace("completion", "")
                CompletionsRegistry.completions[name] = instance
                println("Registered completion ${completion.simpleName}: $name")
            }
        }
    }

    fun getCompletion(name: String): Completion? {
        return completions[name]
    }
}