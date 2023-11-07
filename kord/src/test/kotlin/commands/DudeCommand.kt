package commands

import annotations.Command
import annotations.Subcommand
import dev.kord.common.entity.Permission
import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.interaction.respondEphemeral
import dev.kord.core.entity.Member
import dev.kord.core.entity.User
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import interfaces.RecordPlayer

@Command("dude", "Jeff")
object DudeCommand : RecordPlayer {
    override val permission: Permission = Permission.Administrator

    suspend fun main(event: GuildChatInputCommandInteractionCreateEvent, arg1: String, user: String){
        event.interaction.respondEphemeral {
            content = "Dude! --> $user"
        }
    }
}