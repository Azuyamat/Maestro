import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.entity.interaction.GroupCommand
import dev.kord.core.entity.interaction.RootCommand
import dev.kord.core.entity.interaction.SubCommand
import dev.kord.core.event.gateway.ReadyEvent
import dev.kord.core.event.interaction.ChatInputCommandCreateEvent
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.core.event.interaction.InteractionCreateEvent
import dev.kord.core.on
import dev.kord.rest.builder.interaction.mentionable
import enums.SoundLevel
import interfaces.MaestroInterface
import interfaces.RecordPlayerInterface
import models.Package
import models.Vinyl
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.callSuspend
import kotlin.reflect.full.declaredMemberFunctions

class Maestro(private val bot: Kord) : MaestroInterface {
    override val maestroType: String = "kord"

    override var commands: List<Vinyl> = listOf()
    override var packages: List<Package> = listOf()
    override var maxSoundLevel: SoundLevel = SoundLevel.HIGH

    init {
        bot.on<GuildChatInputCommandInteractionCreateEvent> {
            playVinyl(interaction.command.rootName, this)
        }
    }

    override suspend fun implementCommands() {
        /**
         * Global commands
         */
        bot.createGlobalApplicationCommands {
            globalCommands.map {
                val instance = it.instance
                instance.addCommand(this)
                confirmCommandAddition(it.rootName)
            }
        }

        /**
         * Guild commands
         */
        for (pkg in packages){
            val guildId = pkg.guildId?.let { Snowflake(it) }?: return
            bot.createGuildApplicationCommands(guildId){
                guildCommands.filter { it.guildId == pkg.guildId }.map {
                    val instance = it.instance
                    instance.addCommand(this)
                    confirmCommandAddition(it.rootName)
                }
            }
        }
    }

    override suspend fun implementVinyl(event: Any, instance: RecordPlayerInterface) {

        event as GuildChatInputCommandInteractionCreateEvent
        val interaction = event.interaction

        val c = interaction.command
        val groupName = when (c) {
            is RootCommand, is SubCommand -> null
            is GroupCommand -> c.groupName
        }
        val subCommandName = when (c) {
            is RootCommand -> null
            is SubCommand -> c.name
            is GroupCommand -> null
        }

        val function: KFunction<*> = if (groupName != null && subCommandName != null) {
            val group = instance::class.nestedClasses.find { it.simpleName?.startsWith(groupName)?:false }?: return
            group.declaredMemberFunctions.find { it.name.startsWith(subCommandName) }?: return
        } else if (groupName != null) {
            val group = instance::class.nestedClasses.find { it.simpleName?.startsWith(groupName)?:false }?: return
            group.declaredMemberFunctions.firstOrNull()?:return
        } else if (subCommandName != null) {
            instance.otherFunctions?.firstOrNull(){ it.name.startsWith(subCommandName) }?:return
        } else {
            instance.mainFunction?:return
        }

        val options = interaction.command.options.map {
            var value = it.value.value
            if (value is Snowflake) value = bot.getUser(value)
            value
        }

        try {
            if (function.isSuspend)
                function.callSuspend(instance, event, *options.toTypedArray())
            else
                function.call(instance, event, *options.toTypedArray())
        } catch (e: Exception) {
            println("<------------------> COMMAND ERROR <------------------>")
            println(options)
            e.printStackTrace()
        }
    }
}