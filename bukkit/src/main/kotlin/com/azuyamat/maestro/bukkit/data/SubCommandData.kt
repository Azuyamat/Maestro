package com.azuyamat.maestro.bukkit.data

import com.azuyamat.maestro.bukkit.enums.SenderType
import net.kyori.adventure.text.Component

data class SubCommandData(
    val name: String,
    val description: String = "",
    val permission: String = "",
    val permissionMessage: String = DEFAULT_PERMISSION_MESSAGE,
    val senderType: SenderType = SenderType.BOTH,
    val cooldown: Long = 0,
    val usage: Component
)
