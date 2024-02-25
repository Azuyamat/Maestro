package com.azuyamat.maestro.common.annotations

/**
 * Craft a command from a class.
 *
 * @param name Name of the command. This will be used to register it /<name>
 * @param description Short description of the command
 * @param aliases Array of aliases to register
 * @param permission Permission required to execute the command
 * @param permissionMessage Permission message, supports minimessage formatting
 * @param cooldown Time before the command can be sent again
 */
@Target(AnnotationTarget.CLASS)
annotation class Command(
    val name: String,
    val description: String = "No description",
    val aliases: Array<String> = [],
    val permission: String = "",
    val permissionMessage: String = "You do not have permission to use this command",
    val cooldown: Long = 0, // In milliseconds
)
