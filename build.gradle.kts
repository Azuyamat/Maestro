plugins {
    id("java")
    `maven-publish`
    kotlin("jvm") version "1.9.0"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

val publicationVersion = "1.0"
group = "com.azuyamat"
version = "3.2"

repositories {
    mavenCentral()
}

// test

subprojects {
    apply(plugin = "kotlin")
    apply(plugin = "maven-publish")
    repositories {
        mavenCentral()
    }
    dependencies {
        if (project.name != "common") implementation(project(":common"))
        testImplementation ("io.github.cdimascio:dotenv-java:3.0.0")
        api("org.jetbrains.kotlin:kotlin-reflect")
    }

    if (project.name != "common") {
        apply(plugin = "com.github.johnrengelman.shadow")

        tasks {
            build {
                dependsOn("shadowJar")
            }
            shadowJar {
                archiveClassifier.set("")
            }
        }
    }

    publishing {
        publications {
            create<MavenPublication>("maven") {
                groupId = "com.azuyamat.maestro"
                artifactId = project.name
                version = publicationVersion

                from(components["java"])
            }
        }
    }
}

kotlin {
    jvmToolchain(17)
}