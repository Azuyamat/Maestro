plugins {
    kotlin("jvm")
}

group = "com.azuyamat.maestro.format"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    implementation("net.kyori:adventure-text-minimessage:4.14.0")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}