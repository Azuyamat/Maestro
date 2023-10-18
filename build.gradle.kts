plugins {
    id("java")
    kotlin("jvm") version "1.9.0"
}

group = "com.azuyamat"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

subprojects {
    apply(plugin = "kotlin")
    repositories {
        mavenCentral()
    }
    dependencies {
        if (project.name != "common") implementation(project(":common"))
        testImplementation ("io.github.cdimascio:dotenv-java:3.0.0")
    }
}

kotlin {
    jvmToolchain(17)
}