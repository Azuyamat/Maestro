package models

import interfaces.RecordPlayerInterface

data class Vinyl(
    val rootName: String,
    val description: String,
    val aliases: List<String>,
    val instance: RecordPlayerInterface,
    val guildId: String? = null
)
