import com.azuyamat.maestro.format.StringHelper
import com.azuyamat.maestro.format.StringHelper.Companion.parse
import net.kyori.adventure.text.format.TextColor

fun main() {
    StringHelper.setMainColor(TextColor.color(255, 255, 255))

    val text = StringHelper("Hello world!")
    println("Testing...")
    println(text.toTitleCase())
    println(text.toSmallCaps())
    println(text.sanitize())
}