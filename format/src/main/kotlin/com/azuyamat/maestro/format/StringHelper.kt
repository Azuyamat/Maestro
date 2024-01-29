package com.azuyamat.maestro.format

import com.azuyamat.maestro.format.StringHelper.Companion.parse
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.Tag
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver

private val MINI_MESSAGE = MiniMessage.miniMessage()
private val SMALL_CAPS = mapOf(
    "a" to "ᴀ",
    "b" to "ʙ",
    "c" to "ᴄ",
    "d" to "ᴅ",
    "e" to "ᴇ",
    "f" to "ꜰ",
    "g" to "ɢ",
    "h" to "ʜ",
    "i" to "ɪ",
    "j" to "ᴊ",
    "k" to "ᴋ",
    "l" to "ʟ",
    "m" to "ᴍ",
    "n" to "ɴ",
    "o" to "ᴏ",
    "p" to "ᴘ",
    "q" to "ǫ",
    "r" to "ʀ",
    "s" to "ꜱ",
    "t" to "ᴛ",
    "u" to "ᴜ",
    "v" to "ᴠ",
    "w" to "ᴡ",
    "x" to "x",
    "y" to "ʏ",
    "z" to "ᴢ"
)
private var MAIN_COLOR = TextColor.color(140, 140, 255)
private var PREFIX = "<dark_gray>» <gray>".parse()
private val resolvers: MutableList<TagResolver> = mutableListOf()

class StringHelper(private val text: String) {

    // Convert text to small letters (small caps)
    fun toSmallCaps() = text.map { SMALL_CAPS[it.toString().lowercase()] ?: it }.joinToString("")

    // Parse text to MiniMessage component
    fun parse(prefix: Boolean = false): Component {
        val message = if (resolvers == null || resolvers.isEmpty()) MINI_MESSAGE.deserialize(text, mainColorResolver())
        else MINI_MESSAGE.deserialize(text, *resolvers.toTypedArray())
        return if (prefix) PREFIX.append(message)
        else message
    }

    // Sanitize the text from <> characters
    fun sanitize() = MINI_MESSAGE.escapeTags(text)

    // Convert text to title case
    fun toTitleCase() =
        text.split(" ").joinToString(" ") { it.lowercase().replaceFirstChar { char -> char.uppercase() } }


    companion object {
        fun String.parse(prefix: Boolean = false) = StringHelper(this).parse(prefix)
        fun setMainColor(color: TextColor) {
            MAIN_COLOR = color
        }

        fun setPrefix(prefix: String) {
            PREFIX = prefix.parse()
        }

        fun addResolver(vararg resolver: TagResolver) = resolver.forEach { resolvers.add(it) }

        // <main>
        private fun mainColorResolver(): TagResolver {
            return TagResolver.resolver(
                "main"
            ) { _: ArgumentQueue, _ ->
                Tag.styling(MAIN_COLOR) // #8c8cff
            }
        }
    }
}