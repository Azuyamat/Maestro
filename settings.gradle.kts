plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}
rootProject.name = "Maestro"
include("kord")
include("common")
include("bukkit")
include("format")
include("velocity")
