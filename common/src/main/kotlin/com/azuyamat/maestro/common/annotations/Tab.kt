package com.azuyamat.maestro.common.annotations

/**
 * Tab completion list to be used. You can also register tab completion lists in the CompletionsRegistry.
 *
 * @see com.azuyamat.maestro.bukkit.completions.CompletionsRegistry
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class Tab(
    val list: String
)
