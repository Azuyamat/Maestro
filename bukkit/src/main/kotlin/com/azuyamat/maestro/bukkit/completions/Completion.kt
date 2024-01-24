package com.azuyamat.maestro.bukkit.completions

interface Completion {

    fun complete(): List<String> = emptyList()
}