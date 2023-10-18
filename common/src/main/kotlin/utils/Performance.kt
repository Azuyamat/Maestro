package utils

fun String.firstWord() = Regex("^([A-Z]|[a-z])[a-z]+").find(this)?.value?.lowercase()