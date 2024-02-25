package com.azuyamat.maestro.common

import java.util.*


class CooldownManager {

    private val cooldowns = mutableMapOf<String, Long>()

    fun isOnCooldown(uuid: UUID, cooldownId: String): Boolean {
        val id = "$uuid:$cooldownId"
        val cooldown = cooldowns[id] ?: return false
        return cooldown > System.currentTimeMillis()
    }

    fun setCooldown(uuid: UUID, cooldownId: String, cooldown: Long) {
        val id = "$uuid:$cooldownId"
        cooldowns[id] = System.currentTimeMillis() + cooldown
    }

    fun timeLeft(uuid: UUID, cooldownId: String): Long {
        val id = "$uuid:$cooldownId"
        val cooldown = cooldowns[id] ?: return 0
        return cooldown - System.currentTimeMillis()
    }
}