plugins {
    kotlin("jvm")
}

group = "com.azuyamat.maestro.velocity"
version = "1.0"

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
}

dependencies {
    implementation("com.velocitypowered:velocity-api:3.3.0-SNAPSHOT")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}