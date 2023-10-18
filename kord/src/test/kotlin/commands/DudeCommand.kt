package commands

import annotations.Command
import annotations.Subcommand
import dev.kord.common.entity.Permission
import interfaces.RecordPlayer

@Command("dude", "Jeff")
object DudeCommand : RecordPlayer {
    override val permission: Permission = Permission.Administrator

    suspend fun main(user: String){

    }
}