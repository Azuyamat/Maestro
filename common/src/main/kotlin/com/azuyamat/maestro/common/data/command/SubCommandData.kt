package com.azuyamat.maestro.common.data.command

import com.azuyamat.maestro.common.data.command.DEFAULT_PERMISSION_MESSAGE
import com.azuyamat.maestro.common.enums.SenderType
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
