package commands

import com.azuyamat.maestro.common.annotations.Command

@Command(
    name = "jeff",
    description = "Jeff is a cool guy",
    cooldown = 1000
)
class JeffCommand {

    fun onCommand(jeff: String) {
        println("Jeff is a cool guy")
    }

    fun change(jeff: String, cool: String) {
        println("Jeff is a cool guy")
    }
}