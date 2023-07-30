package net.asaken1021.velocitywhitelist.util.dependency

import java.net.URL

class VWDependencies {
    private val mavenCentralURL: String = "https://repo1.maven.org/maven2"
    private val dependencies: List<Dependency> = listOf(
        Dependency("net.kyori", "adventure-extra-kotlin", "4.14.0"),
        Dependency("org.jetbrains.kotlinx", "kotlinx-serialization-core-jvm", "1.5.1"),
        Dependency("org.jetbrains.kotlinx", "kotlinx-serialization-json-jvm", "1.5.1")
    )

    fun getDependencies(): List<Dependency> {
        return dependencies
    }

    fun getDependencyJarFileName(dependency: Dependency): String {
        return "${dependency.artifactID}-${dependency.version}.jar"
    }

    fun getDependencyURL(dependency: Dependency): URL {
        val slashedGroupID: String = dependency.groupID.replace(".", "/")

        return URL("$mavenCentralURL/$slashedGroupID/${dependency.artifactID}/${dependency.version}/${getDependencyJarFileName(dependency)}")
    }
}