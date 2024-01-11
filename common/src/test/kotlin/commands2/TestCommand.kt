package commands2

import com.azuyamat.maestro.common.annotations.Command

@Command(
    name = "test",
    description = "Test command",
    cooldown = 1000
)
class TestCommand {

    fun onCommand(test: String) {
        println("Test command")
    }

    fun change(test: String, cool: String) {
        println("Test command")
    }
}