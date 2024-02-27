import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    id("com.github.johnrengelman.shadow") version "7.1.2"
    `maven-publish`
}

group = "com.azuyamat.maestro.common"
version = "1.0-SNAPSHOT"

dependencies {
    implementation("net.kyori:adventure-text-minimessage:4.14.0")
    api("org.reflections:reflections:0.9.12")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

tasks.withType<ShadowJar> {
    archiveClassifier.set("")
}