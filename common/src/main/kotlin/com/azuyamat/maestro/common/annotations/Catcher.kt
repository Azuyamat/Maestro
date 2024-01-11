package com.azuyamat.maestro.common.annotations

/**
 * Marks a parameter to catch all arguments that follow it.
 * Ex: `command @Catcher arg1, arg2, arg3`
 * In this case, arg1 will include all arguments that follow it. (arg1 = "arg1, arg2, arg3")
 * @see Command
 */

annotation class Catcher
