plugins {
    id("java")
    kotlin("jvm") version "1.9.0"
    `maven-publish`
}

group = "com.azuyamat"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

subprojects {
    apply(plugin = "kotlin")
    apply(plugin = "java")
    apply(plugin = "maven-publish")

    repositories {
        mavenCentral()
    }

    dependencies {
        if (project.name != "common") implementation(project(":common"))
        testImplementation ("io.github.cdimascio:dotenv-java:3.0.0")
    }

    publishing {
        publications {
            create<MavenPublication>("maven") {
                groupId = "com.azuyamat.maestro"
                artifactId = project.name
                version = "1.0"

                from(components["java"])
            }
        }
    }
}

kotlin {
    jvmToolchain(17)
}