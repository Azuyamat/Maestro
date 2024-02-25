package com.azuyamat.maestro.common.annotations

/**
 * Craft a subcommand from a function.
 *
 * @param name Name of the subcommand. This will be used to register the subcommand /command <name>
 * @param description Short description of the subcommand
 * @param permission Permission required to execute the subcommand
 * @param permissionMessage Permission message, supports minimessage formatting
 * @param cooldown Time before the subcommand can be sent again
 */
@Target(AnnotationTarget.FUNCTION)
annotation class SubCommand(
    val name: String,
    val description: String = "",
    val permission: String = "",
    val permissionMessage: String = "You do not have permission to use this command",
    val cooldown: Long = 0,
)
