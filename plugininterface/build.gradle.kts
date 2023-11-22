plugins {
    id("java-library")
    id("org.jetbrains.kotlin.jvm")

    kotlin("plugin.serialization")
}

dependencies {
    // all the dependencies of app, that is not android specific, and can be used by plugins
    // should be placed here, so instead of bundling them in the plugin, they can directly use the one from app itself

    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

    api("com.squareup.okhttp3:okhttp:4.11.0")
    api("org.jsoup:jsoup:1.16.2")


    // pagination
    api("androidx.paging:paging-common-ktx:3.2.1")

}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}