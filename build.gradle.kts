plugins {
    id("java")
    `maven-publish`
    kotlin("jvm") version "1.9.0"
}

val publicationVersion = "1.0"
group = "com.azuyamat"
version = "1.0"

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
        implementation("org.jetbrains.kotlin:kotlin-reflect")
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