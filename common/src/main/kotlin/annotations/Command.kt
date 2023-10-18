package annotations

annotation class Command(
    val name: String,
    val description: String,
    val useAnnotation: Boolean = true,
    val aliases: Array<String> = []
)
