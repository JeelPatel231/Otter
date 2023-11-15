buildscript {
    repositories {
        google()
    }
    dependencies {
        // safe args for navigation
        val nav_version = "2.7.4"
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$nav_version")
    }
}

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.1.1" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
    id("com.android.library") version "8.1.1" apply false
    // dagger-hilt
    id("com.google.dagger.hilt.android") version "2.48.1" apply false
    // serialization
    id("org.jetbrains.kotlin.plugin.serialization") version "1.8.20" apply false
    id("org.jetbrains.kotlin.jvm") version "1.9.0" apply false
    ///

    id("com.github.johnrengelman.shadow") version "8.1.1" apply false
}
