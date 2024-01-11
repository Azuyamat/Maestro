package com.azuyamat.maestro.common.annotations

/**
 * Marks a class as a command.
 * @param name The name of the command.
 * @param description The description of the command.
 * @param cooldown The cooldown of the command.
 */

@Target(AnnotationTarget.CLASS)
annotation class Command(
    val name: String,
    val description: String = "",
    val cooldown: Long = 0,
)
