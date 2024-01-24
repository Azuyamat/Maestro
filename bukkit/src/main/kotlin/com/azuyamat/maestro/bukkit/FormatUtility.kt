package com.azuyamat.maestro.bukkit

import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.Tag
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver

private val mm = MiniMessage.miniMessage()

internal fun String.parse() = mm.deserialize(this, mainColorResolver())

private fun mainColorResolver(): TagResolver {
    return TagResolver.resolver(
        "main"
    ) { args: ArgumentQueue, _ ->
        Tag.styling(TextColor.color(200, 80, 100))
    }
}