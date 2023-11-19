import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java-library")
    id("org.jetbrains.kotlin.jvm")

    id("com.apollographql.apollo3") version "3.8.2"
    kotlin("plugin.serialization")

    id("com.github.johnrengelman.shadow")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    implementation(project(mapOf("path" to ":plugininterface")))

//    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    // kotlinx serialization
//    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")

    // apollo client for anilist
    implementation("com.apollographql.apollo3:apollo-runtime:3.8.2")

    // Jsoup
//    implementation("org.jsoup:jsoup:1.16.1")
}


apollo {
    service("service") {
        packageName.set("tel.jeelpa.otter.anilisttrackerplugin.models")
    }
}

tasks.withType<ShadowJar> {
    minimize()
    dependencies {
        exclude(dependency("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3"))
        exclude(dependency("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1"))
//        exclude(dependency(project(":trackerinterface")))
//        exclude(dependency("org.jsoup:jsoup:1.16.1"))
    }
}