package com.azuyamat.maestro.common.models

import kotlin.reflect.KType

data class ArgumentData(
    val name: String,
    val description: String = "",
    val position: Int = 0,
    val type: KType,
)
