import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.0"
    kotlin("kapt") version "1.9.0"
    kotlin("plugin.serialization") version "1.9.0"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "net.asaken1021"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        name = "papermc"
        setUrl("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    implementation("net.kyori:adventure-extra-kotlin:4.14.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
    testImplementation(kotlin("test"))
    implementation("com.velocitypowered:velocity-api:3.1.1")
    kapt("com.velocitypowered:velocity-api:3.1.1")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

tasks.jar {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    from(
        configurations.runtimeClasspath.get().map {
            if (it.isDirectory) it
            else {
                println(it.name)
                if (it.name.startsWith("kotlin-")) return@map zipTree(it) else { return@map null }
            }
        }
    )
}