package interfaces

import annotations.Command
import enums.SoundLevel
import models.Package
import models.Vinyl
import org.reflections.Reflections
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation

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
        echo("Maestro $maestroType is ready to conduct")
        registerPackages()
        implementCommands()
        echo("Registered ${commands.size} commands")
    }

    /**
     * Registers packages
     */
    private fun registerPackages(){
        if (packages.isEmpty()) {
            echo("Couldn't find any packages to register. If this is unintentional please use addPackage(...) or addPackages(...) to register one or more packages.")
            return
        }
        else echo("Registering ${packages.size} packages...", SoundLevel.MEDIUM)
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
        echo("Composing commands from package $prefix with ${reflections.size} commands", SoundLevel.MEDIUM)

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
        echo("Registered command ${instance.simpleName}", SoundLevel.MEDIUM)
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
        if (soundLevel.level <= maxSoundLevel.level) println("[MAESTRO-$maestroType] $message")
    }

    /**
     * Plays a vinyl (command)
     *
     * @property id The id of the vinyl to play
     * @property event The event to play the vinyl on
     * @property guildId The guildId to play the vinyl on (optional)
     */
    fun playVinyl(id: String, event: Any, guildId: String? = null) {
        val vinyl = commands.find { it.rootName.equals(id, true) } ?: return echo("Command $id rendered failure.")
        //TODO ("Play command depending on event sub, groups, options")
    }

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

    fun confirmCommandAddition(name: String) = echo("$name has been successfully registered to Discord's API.")
}