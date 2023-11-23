plugins {
    id("java-library")
    id("org.jetbrains.kotlin.jvm")

    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.github.johnrengelman.shadow")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    implementation(project(mapOf("path" to ":plugininterface")))
}