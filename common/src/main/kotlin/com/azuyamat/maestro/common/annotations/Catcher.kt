package com.azuyamat.maestro.common.annotations

/**
 * Catch **all** arguments after a given argument. Sort of like a **vararg**.
 *
 * Ex: Useful for a /msg command
 * ```
 * /message <player> @Catcher <message>
 * ```
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class Catcher
