
fun main() {
    val maestro = CommandManager()
    maestro.registerPackages("commands", "commands2")
    println(maestro.commands)
}