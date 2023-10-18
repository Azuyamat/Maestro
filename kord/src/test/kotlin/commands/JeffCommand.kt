package commands

import annotations.Command
import annotations.Subcommand
import annotations.SubcommandGroup
import dev.kord.common.entity.Permission
import dev.kord.core.event.interaction.ChatInputCommandInteractionCreateEvent
import interfaces.RecordPlayer

@Command("jeff", "Jeff", false, ["admin", "kill", "lol", "aliases", "so", "many"])
object JeffCommand : RecordPlayer {
    override val permission: Permission = Permission.Administrator

    suspend fun poop(poop: String) {
        println(poop)
    }

    class group {
        fun nop(dope: String) {

        }
    }
}