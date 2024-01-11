package com.azuyamat.maestro.common.models

import com.azuyamat.maestro.common.enum.Scope
import java.util.Collections.emptyList

data class CommandData(
    val name: String,
    val scope: Scope,
    val description: String = "",
    val cooldown: Long = 0,
    val arguments: List<ArgumentData> = emptyList(),
)
