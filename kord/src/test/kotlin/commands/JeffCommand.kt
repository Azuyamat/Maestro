package commands

import annotations.Command
import annotations.Subcommand
import annotations.SubcommandGroup
import dev.kord.common.entity.Permission
import dev.kord.core.behavior.interaction.respondEphemeral
import dev.kord.core.entity.Attachment
import dev.kord.core.entity.Role
import dev.kord.core.entity.channel.Channel
import dev.kord.core.event.interaction.ChatInputCommandInteractionCreateEvent
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import interfaces.RecordPlayer

@Command("jeff", "Jeff", false, ["admin", "kill", "lol", "aliases", "so", "many"])
object JeffCommand : RecordPlayer {
    override val permission: Permission = Permission.Administrator

    suspend fun poop(event: GuildChatInputCommandInteractionCreateEvent, role: Attachment) {
        println("nerd")
        println(role)
        event.interaction.respondEphemeral {
            content = "Role name: ${role}"
        }
    }

    class group {
        suspend fun nop(event: GuildChatInputCommandInteractionCreateEvent, dope: Boolean) {
            println(dope)
            event.interaction.respondEphemeral {
                content = "dope"
            }
        }
    }
}