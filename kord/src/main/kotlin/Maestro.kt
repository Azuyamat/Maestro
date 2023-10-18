import dev.kord.core.Kord
import dev.kord.rest.builder.interaction.ChatInputCreateBuilder
import dev.kord.rest.builder.interaction.group
import dev.kord.rest.builder.interaction.string
import dev.kord.rest.builder.interaction.subCommand
import enums.SoundLevel
import interfaces.MaestroInterface
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.toList
import models.Package
import models.Vinyl
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.valueParameters

class Maestro(private val bot: Kord) : MaestroInterface {
    override val maestroType: String = "kord"

    override var commands: List<Vinyl> = listOf()
    override var packages: List<Package> = listOf()
    override var maxSoundLevel: SoundLevel = SoundLevel.HIGH

    override suspend fun implementCommands() {
        bot.createGlobalApplicationCommands {
            globalCommands.map {
                val instance = it.instance
                instance.addCommand(this)
                confirmCommandAddition(it.rootName)
            }
        }
    }
}