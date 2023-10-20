package interfaces

import annotations.Command
import annotations.Param
import annotations.Subcommand
import annotations.SubcommandGroup
import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.Attachment
import dev.kord.core.entity.Member
import dev.kord.core.entity.Role
import dev.kord.core.entity.User
import dev.kord.core.entity.channel.Channel
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.core.event.interaction.InteractionCreateEvent
import dev.kord.rest.builder.interaction.*
import utils.firstWord
import kotlin.reflect.*
import kotlin.reflect.full.*

interface RecordPlayer : RecordPlayerInterface {


    override fun addCommand(builder: Any) {


        builder as GlobalMultiApplicationCommandBuilder
        val annotation = this::class.findAnnotation<Command>() ?: return
        val names: List<String> = listOf(annotation.name).plus(annotation.aliases.map { it })
        val description = annotation.description

        val nestedClasses = this::class.nestedClasses

        for (name in names) {
            builder.input(name, description) {
                nestedClasses.forEach { addGroup(this, it) }
                otherFunctions?.forEach { addSubcommand(this, it) }
                if (otherFunctions?.isEmpty() == true) {
                    mainFunction?.parameters?.forEach {
                        if (it.kind != KParameter.Kind.INSTANCE) {
                            addOption(this, it)
                        }
                    }
                }
            }
        }
    }

    override fun addGroup(builder: Any, cls: KClass<*>) {
        val annotation = cls.findAnnotation<SubcommandGroup>()
        val name: String
        val description: String
        if (annotation == null && useAnnotation) return
        else if (annotation != null && useAnnotation) {
            name = annotation.name
            description = annotation.description
        } else {
            name = cls.simpleName?.firstWord() ?: return
            description = "Utility group: $name"
        }

        builder as RootInputChatBuilder
        builder.group(name, description) {
            cls.declaredMemberFunctions.forEach { addSubcommand(this, it) }
        }
    }

    override fun addSubcommand(builder: Any, obj: KFunction<*>) {
        val annotation = obj.findAnnotation<Subcommand>()
        val name: String
        val description: String
        if (annotation == null && useAnnotation) return
        else if (annotation != null && useAnnotation) {
            name = annotation.name
            description = annotation.description
        } else {
            name = obj.name.firstWord() ?: return
            description = "Utility group: $name"
        }

        when (builder) {
            is GroupCommandBuilder -> builder.subCommand(name, description) {
                obj.valueParameters.forEach {
                    addOption(
                        this,
                        it
                    )
                }
            }

            is ChatInputCreateBuilder -> builder.subCommand(
                name,
                description
            ) { obj.valueParameters.forEach { addOption(this, it) } }
        }
    }

    override fun addOption(builder: Any, param: KParameter) {
        builder as? BaseInputChatBuilder ?: throw Exception("Builder isn't a builder (addOption)")

        val paramName = param.name?.firstWord() ?: throw Exception("Parameter ${param::class} must have a name")
        val description = param.findAnnotation<Param>()?.description ?: paramName

        return when (param.type.classifier) {
            String::class -> builder.string(paramName, description) { required = !param.isOptional }
            Boolean::class -> builder.boolean(paramName, description) { required = !param.isOptional }
            Integer::class -> builder.integer(paramName, description) { required = !param.isOptional }
            Channel::class -> builder.channel(paramName, description) { required = !param.isOptional }
            Role::class -> builder.role(paramName, description) { required = !param.isOptional }
            Snowflake::class -> builder.mentionable(paramName, description) { required = !param.isOptional }
            Attachment::class -> builder.attachment(paramName, description) { required = !param.isOptional }
            Double::class -> builder.number(paramName, description) { required = !param.isOptional }
            User::class -> builder.user(paramName, description) { required = !param.isOptional }
            GuildChatInputCommandInteractionCreateEvent::class -> return
            else -> {
                println("Couldn't add option option $paramName because the variable type isn't recognized")
            }
        }
    }
}