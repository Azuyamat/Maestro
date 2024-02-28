//import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
//    id("com.github.johnrengelman.shadow") version "7.1.2"
    `maven-publish`
}

group = "com.azuyamat.maestro.bukkit"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
}

dependencies {
    // PaperMC
    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
    implementation(kotlin("stdlib"))
    testImplementation(kotlin("stdlib"))
    testImplementation("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
    testImplementation(project(":bukkit"))
    api("org.reflections:reflections:0.9.12") // Reflections
    implementation("net.kyori:adventure-text-minimessage:4.14.0")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}
//
//tasks.withType<ShadowJar> {
//    archiveClassifier.set("")
//}