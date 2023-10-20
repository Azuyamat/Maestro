package commands

import annotations.Command
import annotations.Subcommand
import annotations.SubcommandGroup
import dev.kord.common.entity.Permission
import dev.kord.core.behavior.interaction.respondEphemeral
import dev.kord.core.event.interaction.ChatInputCommandInteractionCreateEvent
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import interfaces.RecordPlayer

@Command("jeff", "Jeff", false, ["admin", "kill", "lol", "aliases", "so", "many"])
object JeffCommand : RecordPlayer {
    override val permission: Permission = Permission.Administrator

    suspend fun poop(event: GuildChatInputCommandInteractionCreateEvent, poop: String) {
        println(poop)
        event.interaction.respondEphemeral {
            content = "poop"
        }
    }

    class group {
        suspend fun nop(event: GuildChatInputCommandInteractionCreateEvent, dope: String) {
            println(dope)
            event.interaction.respondEphemeral {
                content = "dope"
            }
        }
    }
}