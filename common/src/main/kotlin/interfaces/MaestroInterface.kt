package interfaces

import annotations.Command
import enums.SoundLevel
import models.Package
import models.Vinyl
import org.reflections.Reflections
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation

private val COLORS = mutableMapOf(
    "&0" to "\u001B[30m", // Black
    "&1" to "\u001B[34m", // Dark Blue
    "&2" to "\u001B[32m", // Dark Green
    "&3" to "\u001B[36m", // Dark Aqua
    "&4" to "\u001B[31m", // Dark Red
    "&5" to "\u001B[35m", // Dark Purple
    "&6" to "\u001B[33m", // Gold
    "&7" to "\u001B[37m", // Gray
    "&8" to "\u001B[90m", // Dark Gray
    "&9" to "\u001B[94m", // Blue
    "&a" to "\u001B[92m", // Green
    "&b" to "\u001B[96m", // Aqua
    "&c" to "\u001B[91m", // Red
    "&d" to "\u001B[95m", // Light Purple
    "&e" to "\u001B[93m", // Yellow
    "&f" to "\u001B[97m"  // White
)


/**
 * Interface for a Maestro (a command handler)
 * @author Azuyamat
 */
interface MaestroInterface {
    val maestroType: String

    var commands: List<Vinyl>
    var packages : List<Package>
    var maxSoundLevel: SoundLevel

    val globalCommands get() = commands.filter { it.guildId == null }
    val guildCommands get() = commands.filter { it.guildId != null }

    /**
     * Initializes the Maestro
     */
    suspend fun init(){
        echo("Maestro &b$maestroType&r is ready to conduct")
        registerPackages()
        implementCommands()
        echo("Registered &b${commands.size}&r commands")
    }

    /**
     * Registers packages
     */
    private fun registerPackages(){
        if (packages.isEmpty()) {
            echo("&cCouldn't find any packages to register. If this is unintentional please use addPackage(...) or addPackages(...) to register one or more packages.")
            return
        }
        else echo("Registering &b${packages.size}&r packages...", SoundLevel.MEDIUM)
        packages.forEach(::composePackage)
    }

    /**
     * Composes a package (registers commands from a package)
     *
     * @property pkg The package to compose
     */
    private fun composePackage(pkg: Package) {
        val prefix = pkg.name
        val guildId = pkg.guildId
        val reflections: MutableSet<Class<*>> = Reflections(prefix).getTypesAnnotatedWith(Command::class.java).toMutableSet()
        echo("Composing commands from package &b$prefix&r with &b${reflections.size}&r commands", SoundLevel.MEDIUM)

        reflections.map { it.kotlin }.forEach{ recordCommand(it, guildId) }
    }

    /**
     * Records a command
     *
     * @property command The command to record
     * @property guildId The guildId to register the command to (optional)
     */
    private fun recordCommand(command: KClass<*>, guildId: String?) {
        val instance = command.objectInstance as RecordPlayerInterface
        val commandData = instance::class.findAnnotation<Command>() ?: throw Exception("Command annotation not found")
        val vinyl = Vinyl(
            commandData.name,
            commandData.description,
            commandData.aliases.toList(),
            instance,
            guildId
        )
        commands += vinyl
        echo("Registered command &b${instance.simpleName}", SoundLevel.MEDIUM)
        implementCommand(instance)
    }

    /**
     * Implements a command from a specific interface aspect
     *
     * @property command The command to implement
     */
    fun implementCommand(command: RecordPlayerInterface) {}

    /**
     * Implements commands from a specific interface aspect
     *
     * @property commands The commands to implement
     */
    suspend fun implementCommands() {}

    /**
     * Echoes a message to the console
     *
     * @property message The message to echo
     * @property soundLevel The sound level of the message
     */
    fun echo(message: String, soundLevel: SoundLevel = SoundLevel.HIGH) {
        val output = message
            .replace("&[0-9a-fA-F]".toRegex()) { COLORS[it.value] ?: "" }
            .replace("&r".toRegex(), "\u001B[0m")
        if (soundLevel.level <= maxSoundLevel.level) println("${COLORS["&3"]}[MAESTRO-$maestroType]\u001B[0m $output\u001B[0m")
    }

    /**
     * Plays a vinyl (command)
     *
     * @property id The id of the vinyl to play
     * @property event The event to play the vinyl on
     * @property guildId The guildId to play the vinyl on (optional)
     */
    suspend fun playVinyl(id: String, event: Any, guildId: String? = null) {
        val vinyl = commands.find { it.rootName.equals(id, true) || it.aliases.contains(id) } ?: return echo("Command $id rendered failure.")
        val instance = vinyl.instance

        implementVinyl(event, instance)
    }

    /**
     *
     */
    suspend fun implementVinyl(event: Any, instance: RecordPlayerInterface)

    /**
     * Adds a package to the maestro
     *
     * @property name The name of the package
     * @property guildId The guildId to register the package to (optional)
     */
    fun addPackage(name: String, guildId: String? = null) {
        packages += Package(name, guildId)
    }

    /**
     * Adds packages to the maestro
     *
     * @property packages The packages to add
     */
    fun addPackages(vararg packages: Package) {
        this.packages += packages
    }

    /**
     * Confirms the addition of a command to discord API
     * @param name The name of the command to be added
     * @param isGuild Whether the command is a guild command or not
     */
    fun confirmCommandAddition(name: String, isGuild: Boolean = false) = echo("&b$name&r has been successfully registered to Discord's API. ${if (isGuild) "(&bGUILD&r command)" else "(&bGLOBAL&r command)"}")
}