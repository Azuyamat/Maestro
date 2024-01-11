package com.azuyamat.maestro.common.models

data class VinylData(
    val name: String,
    val description: String,
    val cooldown: Long,
    var commands: List<CommandData> = emptyList()
)
