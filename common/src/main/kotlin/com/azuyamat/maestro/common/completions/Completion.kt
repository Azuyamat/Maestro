package com.azuyamat.maestro.common.completions

interface Completion {

    fun complete(): List<String> = emptyList()
}